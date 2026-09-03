package ch.heigvd.fitmeet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.heigvd.fitmeet.ui.theme.Level
import ch.heigvd.fitmeet.ui.theme.Sport

// dev only: every component of #44 in one place, so we can check they
// still look like a set after a change. not reachable from the app.
@Composable
fun ComponentCatalog(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())  // there is more than one screen of it
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Section("Level chips")
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Level.entries.forEach { LevelChip(it) }
        }

        Section("Sport chips")
        // entries gives every value of the enum, so a new sport shows up
        // here on its own
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Sport.entries.forEach { SportChip(it) }
        }

        Section("Avatar stack")
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AvatarStack(count = 8)
            AvatarStack(count = 3)
            AvatarStack(count = 1)
        }

        Section("Activity card")
        ActivityCard(
            title = "Match de Foot",
            sport = Sport.FOOTBALL,
            dateTime = "Aujourd'hui - 14h30",
            place = "Morges, FC Forward",
            level = Level.ADVANCED,
            participants = 3,
            capacity = 10,
            onClick = {},
            onJoin = {},
        )
        // a long title, to check it gets cut instead of breaking the layout
        ActivityCard(
            title = "Badminton",
            sport = Sport.BADMINTON,
            dateTime = "Mercredi - 19h00",
            place = "Yverdon, Salle des Isles",
            level = Level.ALL,
            participants = 12,
            capacity = 12,
            onClick = {},
            onJoin = {},
        )

        // the three states the join button can be in, side by side: this is
        // the part that breaks most easily when the card is touched
        Section("Activity card, joined and full")
        ActivityCard(
            title = "Sortie velo du lac",
            sport = Sport.CYCLING,
            dateTime = "Samedi - 09h00",
            place = "Lausanne, Ouchy",
            level = Level.ALL,
            participants = 5,
            capacity = 12,
            isJoined = true,
            canLeave = true,
            onClick = {},
            onJoin = {},
        )
        // organiser: joined, but with no way out, so the button only states it
        ActivityCard(
            title = "Course matinale",
            sport = Sport.RUNNING,
            dateTime = "Dimanche - 07h30",
            place = "Nyon, Bord du lac",
            level = Level.INTERMEDIATE,
            participants = 2,
            capacity = 8,
            isJoined = true,
            canLeave = false,
            onClick = {},
            onJoin = {},
        )

        Section("Sport filter bar")
        // real state, so the pills can actually be picked from the catalog
        var pickedSports by remember { mutableStateOf(setOf(Sport.FOOTBALL)) }
        SportFilterBar(
            selected = pickedSports,
            activeFilters = pickedSports.size + 1,  // as if a date was picked too
            onToggle = { sport ->
                pickedSports =
                    if (sport in pickedSports) pickedSports - sport else pickedSports + sport
            },
        )

        Section("Filter sheet")
        var dateRange by remember { mutableStateOf(DateRange.THIS_WEEK) }
        var onlyWithSpots by remember { mutableStateOf(true) }
        FilterSheet(
            dateRange = dateRange,
            onDateRange = { dateRange = it },
            onlyWithSpots = onlyWithSpots,
            onOnlyWithSpots = { onlyWithSpots = it },
            onClearAll = {
                dateRange = DateRange.ALL
                onlyWithSpots = false
            },
            onClose = {},
        )

        Section("Empty state")
        Column(Modifier.height(120.dp)) {
            EmptyState("Aucune activité près de vous")
        }

        Section("Error state")
        Column(Modifier.height(160.dp)) {
            ErrorState(
                message = "Impossible de charger les activités",
                onRetry = {},
            )
        }
    }
}

// just a title with a line under it, to tell the blocks apart
@Composable
private fun Section(title: String) {
    Column(
        modifier = Modifier.padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF6B7C74))
        HorizontalDivider(color = Color(0xFFDDE5DD))
    }
}

@Preview
@Composable
private fun ComponentCatalogPreview() {
    ComponentCatalog()
}
