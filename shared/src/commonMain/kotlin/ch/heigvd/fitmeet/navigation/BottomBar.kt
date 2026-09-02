package ch.heigvd.fitmeet.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import fitmeet.shared.generated.resources.Res
import fitmeet.shared.generated.resources.nav_create
import fitmeet.shared.generated.resources.nav_list
import fitmeet.shared.generated.resources.nav_map
import fitmeet.shared.generated.resources.nav_messages
import fitmeet.shared.generated.resources.nav_profile
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import ch.heigvd.fitmeet.ui.messages.ConversationScreen

private data class BottomTab(val route: Any, val label: String, val icon: DrawableResource)

private val tabs = listOf(
    BottomTab(ActivityList, "Activites", Res.drawable.nav_list),
    BottomTab(MapTab, "Carte", Res.drawable.nav_map),
    BottomTab(CreateActivity, "Creer", Res.drawable.nav_create),
    BottomTab(Messages, "Messages", Res.drawable.nav_messages),
    BottomTab(Profile, "Profil", Res.drawable.nav_profile),
)

// material symbols, converted to vector drawables and bundled in
// composeResources: same rendering on android and ios.
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
                icon = {
                    Icon(
                        painter = painterResource(tab.icon),
                        contentDescription = null, // the label below says it
                        modifier = Modifier.size(22.dp),
                    )
                },
                label = { Text(tab.label, fontSize = 10.sp) },
                alwaysShowLabel = true,
            )
        }
    }
}

@Preview
@Composable
private fun BottomBarPreview() {
    // no current destination, so no tab is highlighted: this is only here
    // to check the icons and the labels
    BottomBar(rememberNavController(), null)
}
