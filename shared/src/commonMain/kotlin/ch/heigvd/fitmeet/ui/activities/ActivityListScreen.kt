package ch.heigvd.fitmeet.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import ch.heigvd.fitmeet.data.activities.sampleActivities
import ch.heigvd.fitmeet.model.Activity
import ch.heigvd.fitmeet.ui.map.LocationEffect
import ch.heigvd.fitmeet.ui.map.isNamedPlace
import ch.heigvd.fitmeet.ui.map.rememberPlaceName
import ch.heigvd.fitmeet.ui.components.ActivityCard
import ch.heigvd.fitmeet.ui.components.DateRange
import ch.heigvd.fitmeet.ui.components.EmptyState
import ch.heigvd.fitmeet.ui.components.ErrorState
import ch.heigvd.fitmeet.ui.components.FilterSheet
import ch.heigvd.fitmeet.ui.components.SportFilterBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityListScreen(
    state: ActivityListUiState = ActivityListUiState.Success(sampleActivities),
    onActivityClick: (String) -> Unit = {},
    onToggleJoin: (String) -> Unit = {},
    onRetry: () -> Unit = {},
    // pull to refresh: the gesture calls onRefresh, and isRefreshing keeps
    // its spinner up until the answer is in
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    // attendee names per activity, for the avatar stack in the sheet
    attendees: Map<String, List<String>> = emptyMap(),
    modifier: Modifier = Modifier,
) {
    // one object for every filter, so the screen, the sheet and the test
    // all read the same thing. empty sports means "no filter".
    var filters by remember { mutableStateOf(ActivityFilters()) }
    var filtersOpen by remember { mutableStateOf(false) }

    // iso days, so the date filters are plain string comparisons.
    // remember: the clock is read once, not on every recomposition.
    val zone = TimeZone.currentSystemDefault()
    val today = remember { Clock.System.now().toLocalDateTime(zone).date.toString() }
    val inAWeek = remember {
        Clock.System.now().plus(7, DateTimeUnit.DAY, zone).toLocalDateTime(zone).date.toString()
    }

    // the activity shown in the bottom sheet, null when it is closed.
    // kept here and not in the navigation: the list stays behind the sheet
    // and never loses its scroll position.
    var selected by remember { mutableStateOf<Activity?>(null) }

    // the phone position, from the same source the map uses.
    // null until the user answers the permission prompt.
    var myLat by remember { mutableStateOf<Double?>(null) }
    var myLng by remember { mutableStateOf<Double?>(null) }
    LocationEffect { lat, lng ->
        myLat = lat
        myLng = lng
    }

    val screen = modifier.fillMaxSize()

    // when on a sealed class is exhaustive: add a state and this stops
    // compiling until it is handled here too
    when (state) {
        is ActivityListUiState.Loading -> {
            Box(screen, contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ActivityListUiState.Error -> {
            ErrorState(state.message, onRetry, screen)
        }

        is ActivityListUiState.Success -> {
            if (state.activities.isEmpty()) {
                EmptyState("Aucune activité près de vous", screen)
                return
            }

            // sorting and filtering live in feed(), which the test calls too.
            // remember keyed on what it reads: scrolling alone no longer
            // sorts the whole list again.
            val visible = remember(state.activities, filters, today, inAWeek, myLat, myLng) {
                state.activities.feed(filters, today, inAWeek, myLat, myLng)
            }

            Column(modifier = screen) {
                SportFilterBar(
                    selected = filters.sports,
                    activeFilters = filters.activeCount,
                    onOpenFilters = { filtersOpen = true },
                    onToggle = { sport ->
                        // plus and minus on a Set return a new Set, they do
                        // not change the old one: that is what compose needs
                        // to notice the change and redraw
                        filters = filters.copy(
                            sports = if (sport in filters.sports) filters.sports - sport
                                     else filters.sports + sport,
                        )
                    },
                    modifier = Modifier.padding(top = 12.dp),
                )

                if (visible.isEmpty()) {
                    EmptyState("Aucune activité pour ce filtre")
                } else PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = onRefresh,
                ) {
                    // LazyColumn and not Column: it only builds the rows that
                    // are on screen, so a long list stays smooth
                    LazyColumn(
                        // contentPadding is the space before the first card and
                        // after the last one, spacedBy is the gap between two
                        contentPadding = PaddingValues(top = 20.dp, bottom = 20.dp),
                        // 11 on the mockup, a bit more here so the pale tinted
                        // blocks do not run into each other
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        items(visible, key = { it.id }) { activity ->
                            // activities created before the geocoder landed
                            // all carry the same placeholder, so the point is
                            // resolved here instead. passing nulls when the
                            // row already has a name keeps the call cheap
                            // without making it conditional.
                            val named = activity.place.isNamedPlace()
                            val resolved = rememberPlaceName(
                                latitude = if (named) null else activity.latitude,
                                longitude = if (named) null else activity.longitude,
                            )
                            ActivityCard(
                                title = activity.title,
                                sport = activity.sport,
                                dateTime = activity.dateTime,
                                place = when {
                                    named -> activity.place
                                    resolved != null -> resolved.city
                                    else -> "Lieu sur la carte"
                                },
                                level = activity.level,
                                participants = activity.participants,
                                capacity = activity.capacity,
                                isJoined = activity.isJoined,
                                canLeave = activity.canLeave,
                                onClick = {
                                    selected = activity
                                    onActivityClick(activity.id)
                                },
                                onJoin = { onToggleJoin(activity.id) },
                            )
                        }
                    }
                }

                if (filtersOpen) {
                    ModalBottomSheet(onDismissRequest = { filtersOpen = false }) {
                        FilterSheet(
                            dateRange = filters.dateRange,
                            onDateRange = { filters = filters.copy(dateRange = it) },
                            onlyWithSpots = filters.onlyWithSpots,
                            onOnlyWithSpots = { filters = filters.copy(onlyWithSpots = it) },
                            onClearAll = { filters = ActivityFilters() },
                            onClose = { filtersOpen = false },
                        )
                    }
                }

                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                selected?.let { activity ->
                    ModalBottomSheet(
                        onDismissRequest = { selected = null },
                        sheetState = sheetState,
                    ) {
                        // the sheet reads the fresh copy from the state, so the
                        // button flips there too without closing it
                        val live = state.activities.firstOrNull { it.id == activity.id }
                            ?: activity
                        ActivityDetailScreen(
                            activity = live,
                            attendeeNames = attendees[live.id].orEmpty(),
                            onJoin = { onToggleJoin(live.id) },
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ActivityListScreenPreview() {
    ActivityListScreen()
}

@Preview
@Composable
private fun ActivityListScreenEmptyPreview() {
    ActivityListScreen(ActivityListUiState.Success(emptyList()))
}

@Preview
@Composable
private fun ActivityListScreenLoadingPreview() {
    ActivityListScreen(ActivityListUiState.Loading)
}

@Preview
@Composable
private fun ActivityListScreenErrorPreview() {
    ActivityListScreen(ActivityListUiState.Error("Impossible de charger les activités"))
}
