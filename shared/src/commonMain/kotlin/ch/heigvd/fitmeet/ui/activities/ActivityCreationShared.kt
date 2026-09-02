package ch.heigvd.fitmeet.ui.activities

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.heigvd.fitmeet.ui.theme.Level
import ch.heigvd.fitmeet.ui.theme.Sport

var currentScreen = mutableStateOf(0)

class ActivityData {
    var type by mutableStateOf<Sport?>(null)
    var date by mutableStateOf("")
    var time by mutableStateOf("")
    var duration by mutableStateOf(-1)

    var position by mutableStateOf("")
    var name by mutableStateOf("")
    var description by mutableStateOf("")
    var difficulty by mutableStateOf(Level.ALL)

    var capacity by mutableStateOf(2)
}

var activityData = ActivityData()

fun ActivityData.reset() {
    type = null
    date = ""
    time = ""
    duration = -1
    position = ""
    name = ""
    description = ""
    difficulty = Level.ALL
    capacity = 2
}

fun parsePosition(position: String): Pair<Double, Double> {
    val coordinates = position
        .removePrefix("POINT(")
        .removeSuffix(")")
        .split(" ")

    val lng = coordinates[0].toDouble()
    val lat = coordinates[1].toDouble()

    return Pair(lat, lng)
}

