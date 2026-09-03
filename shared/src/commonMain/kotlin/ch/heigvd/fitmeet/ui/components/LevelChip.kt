package ch.heigvd.fitmeet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.heigvd.fitmeet.ui.theme.Level

// small pill with the level name, coloured per level.
// modifier order matters here: clip -> background -> padding.
// if padding comes first the background does not cover it.
@Composable
fun LevelChip(level: Level, modifier: Modifier = Modifier) {
    Text(
        text = level.label,
        color = Color.White,
        fontSize = 11.sp,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp)) // same radius as the buttons
            .background(level.color)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}

@Preview
@Composable
private fun LevelChipPreview() {
    Column {
        LevelChip(Level.ALL)
        LevelChip(Level.INTERMEDIATE)
        LevelChip(Level.ADVANCED)
    }
}
