package ch.heigvd.fitmeet

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ch.heigvd.fitmeet.navigation.BottomBar
import ch.heigvd.fitmeet.navigation.FitMeetNavHost
import ch.heigvd.fitmeet.navigation.Login
import ch.heigvd.fitmeet.navigation.MainGraph
import ch.heigvd.fitmeet.navigation.Onboarding
import ch.heigvd.fitmeet.data.auth.AuthRepository
import ch.heigvd.fitmeet.data.auth.PreviewAuthRepository

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
          // only one NavHost here, it holds both AuthGraph and MainGraph
          FitMeetNavHost(
              navController = navController,
              modifier = Modifier.padding(padding),
              authRepository = authRepository,
          )
      }
  }
}
