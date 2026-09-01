package ch.heigvd.fitmeet.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// participants shown as overlapping circles, like slack or google docs.
// real profile pictures come later with #58, grey circles for now.
@Composable
fun AvatarStack(
    count: Int,
    maxVisible: Int = 3,
    modifier: Modifier = Modifier,
) {
    // minOf so we never draw more circles than we have people
    val visible = minOf(count, maxVisible)
    val extra = count - visible

    Row(
        // negative spacing is what makes them overlap
        horizontalArrangement = Arrangement.spacedBy((-8).dp),
        modifier = modifier,
    ) {
        repeat(visible) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFB9C2BC))
            )
        }

        // no bubble when everybody fits
        if (extra > 0) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD5DBD7)),
                contentAlignment = Alignment.Center,
            ) {
                Text("+$extra", fontSize = 11.sp, color = Color(0xFF4C5652))
            }
        }
    }
}

@Preview
@Composable
private fun AvatarStackPreview() {
    // one preview per edge case: bubble, no bubble, single circle
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AvatarStack(count = 8)
        AvatarStack(count = 3)
        AvatarStack(count = 1)
    }
}
