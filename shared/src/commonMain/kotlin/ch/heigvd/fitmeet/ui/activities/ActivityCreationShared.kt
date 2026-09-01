package ch.heigvd.fitmeet.ui.activities

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.heigvd.fitmeet.ui.theme.Level

var currentScreen = mutableStateOf(0)

class ActivityData {
    var type by mutableStateOf("None")
    var date by mutableStateOf("")
    var time by mutableStateOf("")
    var duration by mutableStateOf(0)

    var position by mutableStateOf("POINT(0.0000, 0.0000)")
    var name by mutableStateOf("")
    var description by mutableStateOf("")
    var difficulty by mutableStateOf(Level.ALL)
}

var activityData = ActivityData()




