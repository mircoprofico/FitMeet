package ch.heigvd.fitmeet.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import ch.heigvd.fitmeet.ui.messages.ConversationScreen

private data class BottomTab(val route: Any, val label: String)

private val tabs = listOf(
    BottomTab(ActivityList, "Activites"),
    BottomTab(MapTab, "Carte"),
    BottomTab(CreateActivity, "Creer"),
    BottomTab(Messages, "Messages"),
    BottomTab(Profile, "Profil"),
)

/**
 * Text labels for now: Material icons are not published for
 * Compose Multiplatform 1.11. Icons are decided in #44.
 */
@Composable
fun BottomBar(
    navController: NavHostController,
    currentDestination: NavDestination?,
) {

    // We shouldn't see the navBar if we are creating a new activity
    if (currentDestination?.hierarchy?.any { it.hasRoute(CreateActivity::class) } == true) return

    // We shouldn't see the navBar if we are in a discussion
    if (currentDestination?.hierarchy?.any { it.hasRoute(Conversation::class) } == true) return

    NavigationBar {
        tabs.forEach { tab ->
            val selected = currentDestination
                ?.hierarchy
                ?.any { it.hasRoute(tab.route::class) } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(tab.route) {
                        // Do not stack the tabs on top of each other
                        popUpTo<MainGraph> { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Text(tab.label) },
            )
        }
    }
}
