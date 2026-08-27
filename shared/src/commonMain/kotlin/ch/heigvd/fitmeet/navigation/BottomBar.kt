package ch.heigvd.fitmeet.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

private data class Onglet(val route: Any, val libelle: String)

/* Barre en bas de l'écran avec les 5 onglets */

private val onglets = listOf(
    Onglet(ActivityList, "Activites"),
    Onglet(MapTab, "Carte"),
    Onglet(CreateActivity, "Creer"),
    Onglet(Messages, "Messages"),
    Onglet(Profile, "Profil"),
)

@Composable
fun BottomBar(
    navController: NavHostController,
    destinationCourante: NavDestination?,
) {
    NavigationBar {
        onglets.forEach { onglet ->
            val selectionne = destinationCourante
                ?.hierarchy
                ?.any { it.hasRoute(onglet.route::class) } == true

            NavigationBarItem(
                selected = selectionne,
                onClick = {
                    navController.navigate(onglet.route) {
                        // On ne empile pas les onglets les uns sur les autres
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Text(onglet.libelle) },
            )
        }
    }
}
