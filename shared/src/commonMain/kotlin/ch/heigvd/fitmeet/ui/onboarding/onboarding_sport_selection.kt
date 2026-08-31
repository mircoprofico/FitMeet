package ch.heigvd.fitmeet.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.heigvd.fitmeet.data.auth.AuthActionResult
import fitmeet.shared.generated.resources.Res
import fitmeet.shared.generated.resources.logo
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

private val Navy = Color(0xFF102E53)
private val Green = Color(0xFF429A72)
private val LightText = Color(0xFFE6E7EA)

@Preview
@Composable
fun onboarding_2_sports(
    initialSelectedSports: Set<String> = emptySet(),
    initialName: String = "",
    initialBirthdate: String = "",
    onFinish: suspend (String, String, Set<String>) -> AuthActionResult = { _, _, _ -> AuthActionResult(true, "Aperçu") },
    onSaved: () -> Unit = {},
) {
    val activities = listOf("Football", "Basket", "Volley", "Tennis", "Badminton", "Course", "Vélo", "Randonnée")
    var selectedSports by remember(initialSelectedSports) { mutableStateOf(initialSelectedSports) }
    var message by remember { mutableStateOf<AuthActionResult?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Navy)
                .padding(horizontal = 28.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = "FitMeet",
                modifier = Modifier.height(84.dp),
            )

            Spacer(modifier = Modifier.height(48.dp))

            Column(
                modifier = Modifier.widthIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "Quels sports pratiquez-vous ?",
                    color = LightText,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "Vous pourrez modifier ces choix plus tard.",
                    color = LightText.copy(alpha = 0.8f),
                )

                Spacer(modifier = Modifier.height(6.dp))

                activities.forEach { activity ->
                    val isSelected = activity in selectedSports
                    OutlinedButton(
                        onClick = {
                            selectedSports = if (isSelected) selectedSports - activity else selectedSports + activity
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isSelected) Color.White else LightText,
                            containerColor = if (isSelected) Green else Color.Transparent,
                        ),
                    ) {
                        Text(if (isSelected) "✓  $activity" else activity)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            message?.let {
                Text(it.message, color = if (it.isSuccess) Green else Color(0xFFFFB4AB))
            }

            Button(
                onClick = {
                    if (selectedSports.isEmpty()) {
                        message = AuthActionResult(false, "Sélectionnez au moins un sport.")
                    } else if (!isSaving) {
                        scope.launch {
                            isSaving = true
                            val result = onFinish(initialName, initialBirthdate, selectedSports)
                            message = result
                            isSaving = false
                            if (result.isSuccess) onSaved()
                        }
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Color.White),
            ) {
                Text(if (isSaving) "Enregistrement…" else "Terminer", fontWeight = FontWeight.Bold)
            }
        }
    }
}
