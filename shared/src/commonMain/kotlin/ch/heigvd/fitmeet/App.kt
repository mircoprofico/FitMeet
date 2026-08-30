package ch.heigvd.fitmeet

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ch.heigvd.fitmeet.data.auth.AuthRepository
import ch.heigvd.fitmeet.data.auth.PreviewAuthRepository
import ch.heigvd.fitmeet.navigation.Login
import ch.heigvd.fitmeet.navigation.Onboarding
import ch.heigvd.fitmeet.navigation.OnboardingSports
import ch.heigvd.fitmeet.navigation.Register
import ch.heigvd.fitmeet.ui.auth.LoginScreen
import ch.heigvd.fitmeet.ui.auth.OnboardingScreen
import ch.heigvd.fitmeet.ui.auth.RegisterScreen
import ch.heigvd.fitmeet.ui.onboarding.onboarding_2_sports
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
fun App(
    authRepository: AuthRepository = PreviewAuthRepository,
    authenticationCallbackUrl: String? = null,
) {
     MaterialTheme {
      val navController = rememberNavController()

      LaunchedEffect(authenticationCallbackUrl) {
          authenticationCallbackUrl?.let { url ->
              val result = authRepository.handleAuthenticationCallback(url)
              if (result.isSuccess) {
                  navController.navigate(Onboarding) {
                      popUpTo(Login) { inclusive = true }
                  }
              }
          }
      }

      val backStackEntry by navController.currentBackStackEntryAsState()
      val currentDestination = backStackEntry?.destination

      Scaffold(
          bottomBar = {
              if (currentDestination?.hierarchy?.any {
                  it.hasRoute(MainGraph::class)
              } == true) {
                  BottomBar(navController, currentDestination)
              }
          },
      ) { padding ->
          // Un seul NavHost ici :
          // il doit contenir AuthGraph ET MainGraph.
          FitMeetNavHost(
              navController = navController,
              modifier = Modifier.padding(padding),
              authRepository = authRepository,
          )
      }
  }
}
