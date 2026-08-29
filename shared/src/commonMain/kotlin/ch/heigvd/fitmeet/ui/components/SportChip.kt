package ch.heigvd.fitmeet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.heigvd.fitmeet.ui.theme.Sport
import org.jetbrains.compose.resources.painterResource

/**
 * Shows a sport icon and its label on a rounded pill tinted for that sport.
 * The modifier order matters: shape, then background, then inner padding.
 */
@Composable
fun SportChip(sport: Sport, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(sport.tint)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Image(
            painter = painterResource(sport.icon),
            contentDescription = null,
            modifier = Modifier.size(14.dp),
        )
        Text(text = sport.label, color = Color(0xFF16261F), fontSize = 11.sp)
    }
}

@Preview
@Composable
private fun SportChipPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Sport.entries.forEach { SportChip(it) }
    }
}
