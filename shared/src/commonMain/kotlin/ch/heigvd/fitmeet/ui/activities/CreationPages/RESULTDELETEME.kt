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
fun RESULTDELETEME() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Text(
                "Nom de l'activité: "+ activityData.name,
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(
                "Description de l'activité: " + activityData.description,
                style = MaterialTheme.typography.headlineSmall,
            )

            Text(
                "Date de l'activité : " + activityData.date,
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                "Heure de l'activité : " + activityData.time,
                style = MaterialTheme.typography.headlineSmall,
            )

            Text(
                "Durée de l'activité : " + activityData.duration,
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                "position de l'activité : " + activityData.position,
                style = MaterialTheme.typography.headlineSmall,
            )

            Text(
                "Type de l'activité : " + activityData.type,
                style = MaterialTheme.typography.headlineSmall,
            )

            Text(
                "Difficulté de l'activité : " + activityData.difficulty,
                style = MaterialTheme.typography.headlineSmall,
            )





            Button(onClick = { currentScreen.value = 0 }) {
                Text("Terminer la création")
            }

        }
    }
}



