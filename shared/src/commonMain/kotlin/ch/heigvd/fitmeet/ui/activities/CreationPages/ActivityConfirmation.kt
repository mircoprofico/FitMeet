package ch.heigvd.fitmeet.ui.activities.CreationPages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import ch.heigvd.fitmeet.data.activityCreation.EventRepository
import ch.heigvd.fitmeet.ui.activities.activityData
import ch.heigvd.fitmeet.ui.activities.currentScreen
import ch.heigvd.fitmeet.ui.activities.reset
import kotlinx.coroutines.launch

@Composable
fun ActivityConfirmation(
    eventRepository: EventRepository,
    navController: NavController
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Nom de l'activité: ${activityData.name}")
            Text("Description de l'activité: ${activityData.description}")
            Text("Date de l'activité: ${activityData.date}")
            Text("Heure de l'activité: ${activityData.time}")
            Text("Durée de l'activité: ${activityData.duration} min")
            Text("Position de l'activité: ${activityData.position}")
            Text("Type de l'activité: ${activityData.type}")
            Text("Difficulté de l'activité: ${activityData.difficulty.label}")

            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    scope.launch {
                        isSaving = true
                        eventRepository.createEvent(
                            type = activityData.type,
                            date = activityData.date,
                            time = activityData.time,
                            duration = activityData.duration,
                            position = activityData.position,
                            name = activityData.name,
                            description = activityData.description,
                            difficulty = activityData.difficulty.label,
                            capacity = activityData.capacity
                        ).onSuccess {
                            errorMessage = null
                            activityData.reset()
                            navController.navigate("activities") {
                                popUpTo("createActivity") {
                                    inclusive = true
                                }
                            }
                        }.onFailure {
                            errorMessage = it.message ?: "L'activité n'a pas pu être créée."
                        }
                        isSaving = false
                    }
                },
                enabled = !isSaving,
            ) {
                Text(if (isSaving) "Création..." else "Terminer la création")
            }
        }
    }
}
