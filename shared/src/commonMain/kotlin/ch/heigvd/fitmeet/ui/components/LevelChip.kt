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

/**
 * Shows the label of a level in white on a rounded pill of its own colour.
 * The modifier order matters: shape, then background, then inner padding.
 */
@Composable
fun LevelChip(level: Level, modifier: Modifier = Modifier) {
    Text(
        text = level.label,
        color = Color.White,
        fontSize = 11.sp,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
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
