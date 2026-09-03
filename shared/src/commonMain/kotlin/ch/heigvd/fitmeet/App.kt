package ch.heigvd.fitmeet

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ch.heigvd.fitmeet.navigation.BottomBar
import ch.heigvd.fitmeet.navigation.FitMeetNavHost
import ch.heigvd.fitmeet.navigation.AuthGraph
import ch.heigvd.fitmeet.navigation.Login
import ch.heigvd.fitmeet.navigation.MainGraph
import ch.heigvd.fitmeet.navigation.Onboarding
import ch.heigvd.fitmeet.navigation.PasswordReset
import ch.heigvd.fitmeet.data.auth.AuthCallback
import ch.heigvd.fitmeet.data.activities.ActivityRepository
import ch.heigvd.fitmeet.data.activities.PreviewActivityRepository
import ch.heigvd.fitmeet.data.auth.AuthRepository
import ch.heigvd.fitmeet.data.auth.PreviewAuthRepository
import ch.heigvd.fitmeet.data.activityCreation.EventRepository
import ch.heigvd.fitmeet.data.activityCreation.PreviewEventRepository
import ch.heigvd.fitmeet.data.messages.ConversationRepository
import ch.heigvd.fitmeet.data.messages.PreviewConversationRepository
import ch.heigvd.fitmeet.data.profile.OnboardingState
import ch.heigvd.fitmeet.data.profile.PreviewProfileRepository
import ch.heigvd.fitmeet.data.profile.ProfileRepository

@Composable
@Preview
fun App(
    authRepository: AuthRepository = PreviewAuthRepository,
    profileRepository: ProfileRepository = PreviewProfileRepository,
    conversationRepository: ConversationRepository = PreviewConversationRepository,
    activityRepository: ActivityRepository = PreviewActivityRepository,
    eventRepository: EventRepository = PreviewEventRepository,
    authenticationCallbackUrl: String? = null,
) {
      MaterialTheme {
      val navController = rememberNavController()
      var onboardingState by remember { mutableStateOf(OnboardingState()) }

      LaunchedEffect(authRepository, profileRepository, authenticationCallbackUrl) {
          val state = if (authenticationCallbackUrl != null) {
              val result = authRepository.handleAuthenticationCallback(authenticationCallbackUrl)
              when (result.getOrNull()) {
                  AuthCallback.PasswordRecovery -> {
                      navController.navigate(PasswordReset) {
                          popUpTo(Login) { inclusive = true }
                      }
                      null
                  }
                  AuthCallback.EmailConfirmation -> profileRepository.getOnboardingState()
                  null -> null
              }
          } else {
              val restored = authRepository.restoreSession()
              if (restored.isAuthenticated) profileRepository.getOnboardingState() else null
          }

          state?.let {
              onboardingState = it
              if (it.complete) {
                  navController.navigate(MainGraph) {
                      popUpTo(AuthGraph) { inclusive = true }
                      launchSingleTop = true
                  }
              } else {
                  navController.navigate(Onboarding) {
                      popUpTo(Login) { inclusive = true }
                      launchSingleTop = true
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
              profileRepository = profileRepository,
              conversationRepository = conversationRepository,
              eventRepository = eventRepository,
              activityRepository = activityRepository,
              onboardingState = onboardingState,
              onOnboardingStateChanged = { onboardingState = it },
          )
      }
  }
}
