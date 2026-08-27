package ch.heigvd.fitmeet.ui.activities

import androidx.compose.runtime.Composable



val activities: Array<@Composable () -> Unit> = arrayOf(
    { ActivityType() },
    { ActivityDateTimeDuration() }
)

@Composable
fun CreateActivityScreen() {
    activities[currentScreen.value]()
}



