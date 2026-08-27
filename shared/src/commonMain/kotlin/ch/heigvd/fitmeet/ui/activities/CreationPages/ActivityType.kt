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
fun ActivityType() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Text(
                "Nouvelle Activité",
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(
                "Quel type d'activité voulez vous créer?",
                style = MaterialTheme.typography.headlineSmall,
            )

            Button(onClick = { currentScreen.value += 1 }) {
                Text("Click me!")
            }
        }
    }
}



