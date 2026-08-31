package ch.heigvd.fitmeet.ui.messages

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController


@Composable
fun ConversationScreen(
    activityId: String,
    navController : NavHostController
) {
    Button(
        onClick = {
            navController.popBackStack()
        }
    ) {
        Text("Retour")
    }
    Text(activityId)
}
