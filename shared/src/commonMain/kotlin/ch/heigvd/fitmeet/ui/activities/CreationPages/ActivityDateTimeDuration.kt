package ch.heigvd.fitmeet.ui.activities
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ActivityDateTimeDuration() {
MaterialTheme {
    var selectDate by remember { mutableStateOf(false) }
    var selectTime by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        // current page title
        Text(
            "Quel jour?",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFFAAAAAA),
            fontWeight = FontWeight.W500,
            fontSize = 32.sp
        )
        Button(
            onClick = {
                selectDate = true
            }
        ){
            Text(
                text = if(activityData.date == null) {
                    "Choisir une date"
                } else {
                    datePickerState.selectedDateMillis.toString()
                }
            )
        }
        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            "À Quelle heure?",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFFAAAAAA),
            fontWeight = FontWeight.W500,
            fontSize = 32.sp
        )
        Button(
            onClick = {
                selectTime = true
            }
        ){
            Text(
                text = if(activityData.time == "") {
                    "Choisir une heure"
                } else {
                    activityData.time
                }
            )
        }
    }
    // Selection of the date
    if(selectDate){
        DatePickerDialog(
            onDismissRequest = {selectDate = false},
            confirmButton = {
                TextButton(
                    onClick = {
                        selectDate = false
                        activityData.date = datePickerState.selectedDateMillis
                    }
                ){
                    Text("Ok")
                }
            }
        ){
            DatePicker(
                state = datePickerState
            )
        }
    }

    if (selectTime) {
        TimePickerDialog(
            onDismissRequest = {
                selectTime = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // heure sous forme XX:XX
                        activityData.time =
                            "${timePickerState.hour.toString().padStart(2, '0')}:" +
                             timePickerState.minute.toString().padStart(2, '0')
                        selectTime = false
                    }
                ) {
                    Text("Ok")
                }
            },
            title = {
                Text("Choisir une heure")
            }
        ) {
            TimePicker(
                state = timePickerState
            )
        }
    }
}
}



