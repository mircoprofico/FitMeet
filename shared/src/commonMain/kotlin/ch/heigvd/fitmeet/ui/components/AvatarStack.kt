package ch.heigvd.fitmeet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// the circle the profile header already uses, so a face is the same colour
// wherever it shows up
private val AvatarBackground = Color(0xFF16B0D7)
private val ExtraBackground = Color(0xFFD5DBD7)

/**
 * Participants shown as overlapping circles, like slack or google docs.
 *
 * With no picture stored, someone is their initials on the profile colour,
 * which is what the profile header does. [initials] is read in order and may
 * be shorter than [count]: the circles it does not cover stay blank, which is
 * the case whenever we know how many people attend but not who they are.
 */
@Composable
fun AvatarStack(
    count: Int,
    initials: List<String> = emptyList(),
    maxVisible: Int = 3,
    modifier: Modifier = Modifier,
) {
    // minOf so we never draw more circles than we have people
    val visible = minOf(count, maxVisible)
    val extra = count - visible

    Row(
        // negative spacing is what makes them overlap. 6 and not 8: two
        // letters need the room, at 8 the next circle ate the second one
        horizontalArrangement = Arrangement.spacedBy((-6).dp),
        modifier = modifier,
    ) {
        repeat(visible) { index ->
            Avatar(
                label = initials.getOrNull(index)?.take(2)?.uppercase().orEmpty(),
                background = AvatarBackground,
                color = Color.White,
            )
        }

        // no bubble when everybody fits
        if (extra > 0) {
            Avatar(
                label = "+$extra",
                background = ExtraBackground,
                color = Color(0xFF4C5652),
            )
        }
    }
}

@Composable
private fun Avatar(label: String, background: Color, color: Color) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(background)
            // a white ring, so two overlapping circles stay separate
            .border(1.5.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Preview
@Composable
private fun AvatarStackPreview() {
    // one preview per edge case: named, partly named, bubble, single circle
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AvatarStack(count = 3, initials = listOf("MP", "PG", "FB"))
        AvatarStack(count = 8, initials = listOf("MP", "PG"))
        AvatarStack(count = 8)
        AvatarStack(count = 1)
    }
}
