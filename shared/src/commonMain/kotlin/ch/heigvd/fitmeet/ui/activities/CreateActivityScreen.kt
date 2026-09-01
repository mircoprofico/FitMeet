package ch.heigvd.fitmeet.ui.activities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.heigvd.fitmeet.ui.activities.CreationPages.ActivityConfirmation


val activities: Array<@Composable () -> Unit> = arrayOf(
    { ActivityType() },
    { ActivityDateTimeDuration() },
    { ActivityLocation() },
    { ActivityInfos() }
)

@Composable
fun CreateActivityScreen() {
    if (currentScreen.value >= activities.size - 1) {
        ActivityConfirmation()
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
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    "Suivant",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.W600
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .safeContentPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

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



