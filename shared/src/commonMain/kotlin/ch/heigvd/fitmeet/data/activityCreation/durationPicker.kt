package ch.heigvd.fitmeet.data.activityCreation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DurationPickerDialog(
    initialDuration: Int = 30,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    val durations = (0..180 step 30).toList()

    var selectedDuration by remember {
        mutableStateOf(
            durations.minByOrNull {
                kotlin.math.abs(it - initialDuration)
            } ?: 60
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Choisir une durée")
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(durations) { duration ->
                        TextButton(
                            onClick = {
                                selectedDuration = duration
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (duration == 0) "Non spécifiée" else "$duration min",
                                fontSize = if (duration == selectedDuration) {
                                    24.sp
                                } else {
                                    18.sp
                                },
                                fontWeight = if (duration == selectedDuration) {
                                    FontWeight.ExtraBold
                                } else {
                                    FontWeight.Normal
                                },
                                color = if (duration == selectedDuration) {
                                    Color(0xFF333333)
                                } else {
                                    Color(0xFF888888)
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(selectedDuration)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Annuler")
            }
        }
    )
}