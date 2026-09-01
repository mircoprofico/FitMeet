package ch.heigvd.fitmeet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// shown instead of a blank screen when there is nothing to list,
// otherwise it just looks like the app is broken.
// a Column needs two properties to center, one per axis
// (a Box does both at once with contentAlignment).
@Composable
fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()   // without this there is nothing to center in
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            fontSize = 14.sp,
            color = Color(0xFF6B7C74),
            textAlign = TextAlign.Center, // in case the message wraps on two lines
        )
    }
}

@Preview
@Composable
private fun EmptyStatePreview() {
    EmptyState("Aucune activité près de vous")
}
