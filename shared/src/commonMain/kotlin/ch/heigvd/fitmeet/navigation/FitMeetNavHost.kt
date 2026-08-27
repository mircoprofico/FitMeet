package ch.heigvd.fitmeet.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import ch.heigvd.fitmeet.ui.activities.ActivityDetailScreen
import ch.heigvd.fitmeet.ui.activities.ActivityListScreen
import ch.heigvd.fitmeet.ui.activities.CreateActivityScreen
import ch.heigvd.fitmeet.ui.auth.LoginScreen
import ch.heigvd.fitmeet.ui.auth.OnboardingScreen
import ch.heigvd.fitmeet.ui.auth.RegisterScreen
import ch.heigvd.fitmeet.ui.map.MapScreen
import ch.heigvd.fitmeet.ui.messages.ConversationListScreen
import ch.heigvd.fitmeet.ui.messages.ConversationScreen
import ch.heigvd.fitmeet.ui.profile.ProfileScreen

/**
 * Relie chaque route a son écran.
 * Seul endroit de l'app qui connait la navigation
 */
@Composable
fun FitMeetNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AuthGraph,
        modifier = modifier,
    ) {
        navigation<AuthGraph>(startDestination = Login) {
            composable<Login> {
                Temporaire(
                    "Se connecter" to { navController.entrerDansApp() },
                    "Creer un compte" to { navController.navigate(Register) },
                ) { LoginScreen() }
            }
            composable<Register> {
                Temporaire(
                    "Valider" to { navController.navigate(Onboarding) },
                ) { RegisterScreen() }
            }
            composable<Onboarding> {
                Temporaire(
                    "Terminer" to { navController.entrerDansApp() },
                ) { OnboardingScreen() }
            }
        }

        navigation<MainGraph>(startDestination = ActivityList) {
            composable<ActivityList> {
                Temporaire(
                    "Ouvrir une activite" to { navController.navigate(ActivityDetail("demo-1")) },
                ) { ActivityListScreen() }
            }
            composable<MapTab> { MapScreen() }
            composable<CreateActivity> { CreateActivityScreen() }
            composable<Messages> {
                Temporaire(
                    "Ouvrir une conversation" to { navController.navigate(Conversation("demo-1")) },
                ) { ConversationListScreen() }
            }
            composable<Profile> { ProfileScreen() }

            composable<ActivityDetail> { entry ->
                ActivityDetailScreen(activityId = entry.toRoute<ActivityDetail>().activityId)
            }
            composable<Conversation> { entry ->
                ConversationScreen(activityId = entry.toRoute<Conversation>().activityId)
            }
        }
    }
}

/**
 * Affiche un ecran, plus des boutons de navigation provisoires en dessous.
 */
@Composable
private fun Temporaire(
    vararg boutons: Pair<String, () -> Unit>,
    ecran: @Composable () -> Unit,
) {
    Column {
        ecran()
        boutons.forEach { (libelle, action) ->
            Button(onClick = action) { Text(libelle) }
        }
    }
}

/**
 * Entre dans l'app après connexion.
 *
 * popUpTo(AuthGraph, inclusive) vide la pile des ecrans de connexion.
 * Sans ça : retour depuis Activites ramenerait sur le questionnaire,
 * puis l'inscription, puis le login.
 */
private fun NavHostController.entrerDansApp() {
    navigate(MainGraph) {
        popUpTo(AuthGraph) { inclusive = true }
    }
}
