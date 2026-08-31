package ch.heigvd.fitmeet.ui.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ch.heigvd.fitmeet.navigation.BottomBar
import ch.heigvd.fitmeet.navigation.FitMeetNavHost
import ch.heigvd.fitmeet.navigation.MainGraph

@Preview
@Composable
fun ConversationListScreen() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(Color(0xFFDDDDDD))
                .safeContentPadding()
                .fillMaxSize()
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        color = Color(0xFF102E53),
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Messagerie",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.W800,
                    color = Color.White,
                    textAlign = TextAlign.Left
                )
            }
        }
    }
}
