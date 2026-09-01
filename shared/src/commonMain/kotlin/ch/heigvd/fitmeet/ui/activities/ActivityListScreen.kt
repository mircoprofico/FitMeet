package ch.heigvd.fitmeet.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.heigvd.fitmeet.data.activities.sampleActivities
import ch.heigvd.fitmeet.model.Activity
import ch.heigvd.fitmeet.ui.components.ActivityCard
import ch.heigvd.fitmeet.ui.components.EmptyState

@Composable
fun ActivityListScreen(
    activities: List<Activity> = sampleActivities,   // fake data until the repository exists
    onActivityClick: (String) -> Unit = {},
    onJoin: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    if (activities.isEmpty()) {
        EmptyState("Aucune activité près de vous", modifier)
        return
    }

    // LazyColumn and not Column: it only builds the rows that are on
    // screen, so a long list stays smooth
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 11.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp), // gap measured on the mockup
    ) {
        items(activities, key = { it.id }) { activity ->
            ActivityCard(
                title = activity.title,
                sport = activity.sport,
                dateTime = activity.dateTime,
                place = activity.place,
                level = activity.level,
                participants = activity.participants,
                capacity = activity.capacity,
                onClick = { onActivityClick(activity.id) },
                onJoin = { onJoin(activity.id) },
            )
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
    ActivityListScreen(activities = emptyList())
}
