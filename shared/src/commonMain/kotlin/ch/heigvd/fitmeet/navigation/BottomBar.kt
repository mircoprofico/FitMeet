package ch.heigvd.fitmeet.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
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

private val Green = Color(0xFF3E8E68)
private val Inactive = Color(0xFF9AA5A0)
private val Line = Color(0xFFE4E9E5)

private data class BottomTab(val route: Any, val label: String, val icon: DrawableResource)

// create sits in the middle on purpose, raised, like most social apps.
// the four others are around it.
private val leftTabs = listOf(
    BottomTab(ActivityList, "Activites", Res.drawable.nav_list),
    BottomTab(MapTab, "Carte", Res.drawable.nav_map),
)
private val rightTabs = listOf(
    BottomTab(Messages, "Messages", Res.drawable.nav_messages),
    BottomTab(Profile, "Profil", Res.drawable.nav_profile),
)

// custom bar and not NavigationBar: material does not let an item stick out
// above the bar, which is what the raised create button needs.
@Composable
fun BottomBar(
    navController: NavHostController,
    currentDestination: NavDestination?,
) {
    // We shouldn't see the navBar if we are creating a new activity
    if (currentDestination?.hierarchy?.any { it.hasRoute(CreateActivity::class) } == true) return

    // We shouldn't see the navBar if we are in a discussion
    if (currentDestination?.hierarchy?.any { it.hasRoute(Conversation::class) } == true) return
    Column(
        modifier = Modifier.navigationBarsPadding()
    ) {
        HorizontalDivider(color = Line)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .height(62.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leftTabs.forEach { Tab(it, currentDestination, navController, Modifier.weight(1f)) }

            CreateButton(
                selected = currentDestination.isOn(CreateActivity),
                onClick = { navController.goTo(CreateActivity) },
                modifier = Modifier.weight(1f),
            )

            rightTabs.forEach { Tab(it, currentDestination, navController, Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun Tab(
    tab: BottomTab,
    currentDestination: NavDestination?,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val selected = currentDestination.isOn(tab.route)
    val tint = if (selected) Green else Inactive
    Column(
        modifier = modifier
            // no ripple: a full width rectangle flashing under a small icon
            // looks clumsy on a bar this thin
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { navController.goTo(tab.route) }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Icon(
            painter = painterResource(tab.icon),
            contentDescription = null, // the label right below says it
            tint = tint,
            modifier = Modifier.size(23.dp),
        )
        Text(
            text = tab.label,
            fontSize = 10.sp,
            color = tint,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

@Composable
private fun CreateButton(selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                // offset moves it after measuring, so it overlaps the bar
                // instead of making it taller
                .offset(y = (-12).dp)
                .size(52.dp)
                .clip(CircleShape)
                .background(if (selected) Color(0xFF2E6E50) else Green)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(Res.drawable.nav_create),
                contentDescription = "Creer une activite",
                tint = Color.White,
                modifier = Modifier.size(30.dp),
            )
        }
    }
}

private fun NavDestination?.isOn(route: Any) =
    this?.hierarchy?.any { it.hasRoute(route::class) } == true

private fun NavHostController.goTo(route: Any) {
    navigate(route) {
        // do not stack the tabs on top of each other
        popUpTo<MainGraph> { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Preview
@Composable
private fun BottomBarPreview() {
    // no current destination, so nothing is highlighted: this is only here
    // to check the icons and the layout
    BottomBar(rememberNavController(), null)
}
