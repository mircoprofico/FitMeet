package ch.heigvd.fitmeet.ui.activities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
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
import androidx.compose.ui.unit.FontScaling
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.heigvd.fitmeet.data.activityCreation.DurationPickerDialog
import ch.heigvd.fitmeet.data.activityCreation.formatDate
import ch.heigvd.fitmeet.data.activityCreation.parseDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.Instant


private val Navy = Color(0xFF0B2545)
private val Green = Color(0xFF429A72)
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ActivityDateTimeDuration() {
    MaterialTheme {
        var selectDate by remember { mutableStateOf(false) }
        var selectTime by remember { mutableStateOf(false) }
        var selectDuration by remember { mutableStateOf(false) }
        var invalidTime by remember { mutableStateOf(false) }



        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val datePickerState = rememberDatePickerState(
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val date = Instant
                        .fromEpochMilliseconds(utcTimeMillis)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date

                    return date >= today
                }
            }
        )
        val timePickerState = rememberTimePickerState()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(
                modifier = Modifier.height(30.dp)
            )
            // Day
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
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green
                )
            ) {
                Text(
                    text = if (activityData.date == "") {
                        "Choisir une date"
                    } else {
                        activityData.date
                    },
                    fontWeight = FontWeight.W700,
                    fontSize = 20.sp
                )
            }
            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // Time
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
                    invalidTime = false
                },
                enabled = activityData.date != "", // if we didn't chose the date, we can't check for the time
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green,
                    disabledContainerColor = Color(0xFF888888)
                )
            ) {
                Text(
                    text = if (activityData.time == "") {
                        "Choisir une heure"
                    } else {
                        activityData.time
                    },
                    fontWeight = FontWeight.W700,
                    fontSize = 20.sp
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // Duration
            Text(
                "Durée de l'activité?",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFFAAAAAA),
                fontWeight = FontWeight.W500,
                fontSize = 32.sp
            )
            Button(
                enabled = activityData.time != "",
                onClick = {
                    selectDuration = true
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green,
                    disabledContainerColor = Color(0xFF888888)

                )
            ) {
                Text(
                    text = if (activityData.duration == -1) {
                        "Choisir une durée"
                    } else if(activityData.duration == 0){
                        "Non spécifiée"
                    } else {
                        "${activityData.duration} min"
                    },
                    fontWeight = FontWeight.W700,
                    fontSize = 20.sp
                )
            }


        }
        // Selection of the date
        if (selectDate) {
            DatePickerDialog(
                onDismissRequest = { selectDate = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectDate = false

                            datePickerState.selectedDateMillis?.let { millis ->
                                activityData.date = formatDate(millis)
                        }
                        }
                    ) {
                        Text("Ok")
                    }
                }
            ) {
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
                            val selectedDate = activityData.date

                            val selectedTime = LocalTime(
                                hour = timePickerState.hour,
                                minute = timePickerState.minute
                            )

                            val now = Clock.System.now()
                                .toLocalDateTime(TimeZone.currentSystemDefault())

                            val selectedDateTime = LocalDateTime(
                                date = parseDate(selectedDate),
                                time = selectedTime
                            )

                            if (selectedDateTime >= now) {
                                activityData.time =
                                    "${timePickerState.hour.toString().padStart(2, '0')}:" +
                                            timePickerState.minute.toString().padStart(2, '0')

                                selectTime = false
                                invalidTime = false
                            } else {
                                invalidTime = true
                            }
                        }
                    ) {
                        Text("Ok")
                    }
                },
                title = {
                    Text("Choisir une heure")
                }
            ) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                if (invalidTime) {
                    Text(
                        text = "Cette heure est déjà passée.",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }


                    TimePicker(
                        state = timePickerState
                    )
                }
            }
        }

        if (selectDuration) {
            DurationPickerDialog(
                initialDuration = activityData.duration ?: 60,
                onDismiss = {
                    selectDuration = false
                },
                onConfirm = { duration ->
                    activityData.duration = duration
                    selectDuration = false
                }
            )
        }
    }
}



