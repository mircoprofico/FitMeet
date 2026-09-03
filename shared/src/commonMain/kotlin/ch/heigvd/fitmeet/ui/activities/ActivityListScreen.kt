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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.heigvd.fitmeet.data.activities.sampleActivities
import ch.heigvd.fitmeet.model.Activity
import ch.heigvd.fitmeet.ui.components.ActivityCard
import ch.heigvd.fitmeet.ui.theme.Sport
import ch.heigvd.fitmeet.ui.components.EmptyState
import ch.heigvd.fitmeet.ui.components.SportFilterBar
import ch.heigvd.fitmeet.ui.components.ErrorState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityListScreen(
    state: ActivityListUiState = ActivityListUiState.Success(sampleActivities),
    onActivityClick: (String) -> Unit = {},
    onJoin: (String) -> Unit = {},
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    // a Set, so a sport cannot be picked twice. empty means "no filter".
    var selectedSports by remember { mutableStateOf(emptySet<Sport>()) }
    // the activity shown in the bottom sheet, null when it is closed.
    // kept here and not in the navigation: the list stays behind the sheet
    // and never loses its scroll position.
    var selected by remember { mutableStateOf<Activity?>(null) }

    // when on a sealed class is exhaustive: add a state and this stops
    // compiling until it is handled here too
    when (state) {
        is ActivityListUiState.Loading -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ActivityListUiState.Error -> {
            ErrorState(state.message, onRetry, modifier)
        }

        is ActivityListUiState.Success -> {
            if (state.activities.isEmpty()) {
                EmptyState("Aucune activité près de vous", modifier)
                return
            }

            // sortedBy returns a new list, it does not touch the one we got.
            // iso dates sort as plain text, so no date parsing needed here.
            // TODO: sort by distance first once the events carry coordinates (#75)
            val sorted = state.activities.sortedBy { it.startsAt }

            // no sport picked means everything, otherwise keep the matches
            val visible = if (selectedSports.isEmpty()) sorted
                          else sorted.filter { it.sport in selectedSports }

            // LazyColumn and not Column: it only builds the rows that are on
            // screen, so a long list stays smooth
            Column(modifier = modifier.fillMaxSize()) {
                SportFilterBar(
                    selected = selectedSports,
                    onToggle = { sport ->
                        // plus and minus on a Set return a new Set, they do
                        // not change the old one: that is what compose needs
                        // to notice the change and redraw
                        selectedSports =
                            if (sport in selectedSports) selectedSports - sport
                            else selectedSports + sport
                    },
                    modifier = Modifier.padding(top = 12.dp),
                )

                if (visible.isEmpty()) {
                    EmptyState("Aucune activité pour ce filtre")
                    return@Column
                }

                LazyColumn(
                // contentPadding is the space before the first card and after the
                // last one, spacedBy below is the gap between two cards
                contentPadding = PaddingValues(top = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp), // 11 on the mockup, a bit more here so
                    // the pale tinted blocks do not run into each other
            ) {
                items(visible, key = { it.id }) { activity ->
                    ActivityCard(
                        title = activity.title,
                        sport = activity.sport,
                        dateTime = activity.dateTime,
                        place = activity.place,
                        level = activity.level,
                        participants = activity.participants,
                        capacity = activity.capacity,
                        onClick = {
                            selected = activity
                            onActivityClick(activity.id)
                        },
                        onJoin = { onJoin(activity.id) },
                    )
                }
            }

            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            selected?.let { activity ->
                ModalBottomSheet(
                    onDismissRequest = { selected = null },
                    sheetState = sheetState,
                ) {
                    ActivityDetailScreen(activity = activity)
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
