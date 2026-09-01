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
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Level.entries.forEach { LevelChip(it) }
        }

        Section("Sport chips")
        // entries gives every value of the enum, so a new sport shows up
        // here on its own
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Sport.entries.forEach { SportChip(it) }
        }

        Section("Avatar stack")
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            AvatarStack(count = 8)
            AvatarStack(count = 3)
            AvatarStack(count = 1)
        }

        // cards go edge to edge on purpose: that is their real width in the
        // list, showing them padded here would be misleading
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

        Section("Empty state")
        Column(Modifier.height(120.dp).padding(horizontal = 16.dp)) {
            EmptyState("Aucune activité près de vous")
        }

        Section("Error state")
        Column(Modifier.height(160.dp).padding(horizontal = 16.dp)) {
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
        modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
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
