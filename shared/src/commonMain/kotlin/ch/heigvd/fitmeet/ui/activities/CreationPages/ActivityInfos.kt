package ch.heigvd.fitmeet.ui.activities
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun ActivityInfos() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Text(
                "Nom de l'activité",
                style = MaterialTheme.typography.headlineLarge,
            )
            TextField(
                value = activityData.name,
                    onValueChange = { activityData.name = it },
                    label = { Text("Nom de l'activité") }
            )

            Text(
                "Description de l'activité",
                style = MaterialTheme.typography.headlineLarge,
            )
            Button(onClick = { currentScreen.value += 1 }) {
                Text("Terminer la création")
            }

        }
    }
}



