package ch.heigvd.fitmeet

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ch.heigvd.fitmeet.navigation.BottomBar
import ch.heigvd.fitmeet.navigation.FitMeetNavHost
import ch.heigvd.fitmeet.navigation.MainGraph

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = backStackEntry?.destination

        // The bottom bar only exists once signed in
        val showBottomBar = currentDestination
            ?.hierarchy
            ?.any { it.hasRoute(MainGraph::class) } == true

        Scaffold(
            bottomBar = {
                if (showBottomBar) BottomBar(navController, currentDestination)
            },
        ) { padding ->
            FitMeetNavHost(
                navController = navController,
                modifier = Modifier.padding(padding),
            )
        }
    }
}
