package ch.heigvd.fitmeet.ui.activities
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun ActivityDateTimeDuration() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Text(
                "Cette activité a lieu (ajd/demain/le ...)",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                "à (heure)",
                style = MaterialTheme.typography.headlineSmall,
            )

            Text(
                "Et dure 1h",
                style = MaterialTheme.typography.headlineSmall,
            )


            Button(onClick = { currentScreen.value += 1 }) {
                Text("Click me!")
            }
        }
    }
}



