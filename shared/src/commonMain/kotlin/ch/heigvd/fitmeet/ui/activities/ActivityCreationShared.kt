package ch.heigvd.fitmeet.ui.activities

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

var currentScreen = mutableStateOf(0)
val activitiesList = listOf(
    "Foot",
    "Basket",
    "Volley",
    "Tennis",
    "Badminton",
    "Course",
    "Velo",
    "Rando",
    "Autre"
)

class ActivityData {
    var type by mutableStateOf("None")
    var date by mutableStateOf("")
    var time by mutableStateOf("")
    var duration by mutableStateOf(15)

    var position by mutableStateOf("")
    var name by mutableStateOf("")
    var description by mutableStateOf("")
    var difficulty by mutableStateOf("any")
}

var activityData = ActivityData()




