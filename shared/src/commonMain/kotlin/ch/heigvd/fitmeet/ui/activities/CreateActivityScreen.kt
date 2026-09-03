package ch.heigvd.fitmeet.ui.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ch.heigvd.fitmeet.data.activityCreation.EventRepository
import ch.heigvd.fitmeet.navigation.ActivityList
import ch.heigvd.fitmeet.navigation.MainGraph
import ch.heigvd.fitmeet.ui.activities.CreationPages.ActivityConfirmation


val activities: Array<@Composable () -> Unit> = arrayOf(
    { ActivityType() },
    { ActivityDateTimeDuration() },
    { ActivityLocation() },
    { ActivityInfos() },
)

@Composable
fun CreateActivityScreen(
    eventRepository: EventRepository,
    navController: NavController
) {
    val focusManager = LocalFocusManager.current

    if (currentScreen.value >= activities.size) {
        ActivityConfirmation(
            eventRepository = eventRepository,
            navController = navController
        )
        return
    }

    Scaffold(
        containerColor = Color(0xFF102E53),
        bottomBar = {
            Button(
                onClick = { currentScreen.value += 1 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3E8E68),
                    disabledContainerColor = Color(0xFF888888)
                ),
                enabled =
                    if(currentScreen.value == 0){
                        activityData.type != null
                    }else if (currentScreen.value == 1) {
                        activityData.date != ""
                                && activityData.time != ""
                                && activityData.duration != -1
                    }else if (currentScreen.value == 2) {
                        activityData.position != ""
                    }else if (currentScreen.value == 3) {
                        activityData.name != ""
                    } else
                        false
                ,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {

                Text(
                    "Suivant",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.W600
                )
            }
        },
        topBar = {
            IconButton(
                onClick = {
                    activityData.reset()
                    navController.navigate(ActivityList) {
                        launchSingleTop = true
                        popUpTo(MainGraph) {
                            inclusive = false
                        }
                    }
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .size(40.dp)
                    .background(Color(0xFFFF0000))
            ) {
                Text(
                    "X",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.W900,
                    fontSize = 18.sp,
                    color = Color(0xFFFFFFFF)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 28.dp)
                .clickable { focusManager.clearFocus() },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Nouvelle Activité",
                color = Color.White,
                fontWeight = FontWeight.W900,
                fontSize = 40.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Page-specific content
            activities[currentScreen.value]()
        }
    }
}
