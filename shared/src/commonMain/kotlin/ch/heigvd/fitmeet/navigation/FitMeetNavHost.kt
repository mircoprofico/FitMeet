package ch.heigvd.fitmeet.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import ch.heigvd.fitmeet.data.auth.AuthRepository
import ch.heigvd.fitmeet.data.profile.OnboardingState
import ch.heigvd.fitmeet.data.profile.ProfileRepository
import ch.heigvd.fitmeet.data.messages.ConversationRepository
import ch.heigvd.fitmeet.ui.activities.ActivityDetailScreen
import ch.heigvd.fitmeet.ui.activities.ActivityListScreen
import ch.heigvd.fitmeet.ui.activities.CreateActivityScreen
import ch.heigvd.fitmeet.ui.auth.LoginScreen
import ch.heigvd.fitmeet.ui.auth.OnboardingScreen
import ch.heigvd.fitmeet.ui.auth.RegisterScreen
import ch.heigvd.fitmeet.ui.map.MapScreen
import ch.heigvd.fitmeet.ui.messages.ConversationListScreen
import ch.heigvd.fitmeet.ui.messages.ConversationScreen
import ch.heigvd.fitmeet.ui.profile.EditProfileScreen
import ch.heigvd.fitmeet.ui.profile.ProfileScreen
import ch.heigvd.fitmeet.ui.onboarding.onboarding_2_sports
import ch.heigvd.fitmeet.ui.profile.ProfileViewModel

/**
 * Maps every route to its screen.
 * The only place in the app that knows about navigation: no screen file
 * was modified, so nobody has a conflict to resolve on their own screen.
 */
@Composable
fun FitMeetNavHost(
    navController: NavHostController,
    authRepository: AuthRepository,
    profileRepository: ProfileRepository,
    conversationRepository: ConversationRepository,
    onboardingState: OnboardingState = OnboardingState(),
    onOnboardingStateChanged: (OnboardingState) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AuthGraph,
        modifier = modifier,
    ) {
        navigation<AuthGraph>(startDestination = Login) {
            composable<Login> {
                LoginScreen(
                    onCreateAccount = { navController.navigate(Register) },
                    onLogin = { email, password ->
                        authRepository.signIn(email, password).also { result ->
                            if (result.isSuccess) {
                                val state = profileRepository.getOnboardingState()
                                onOnboardingStateChanged(state)
                                navController.openAfterAuthentication(state)
                            }
                        }
                    },
                    onForgotPassword = authRepository::requestPasswordReset,
                )
            }
            composable<Register> {
                RegisterScreen(onRegister = authRepository::signUp)
            }
            composable<Onboarding> {
                OnboardingScreen(
                    state = onboardingState,
                    onNext = { name, birthdate ->
                        onOnboardingStateChanged(
                            onboardingState.copy(name = name, birthdate = birthdate),
                        )
                        navController.navigate(OnboardingSports)
                    },
                )
            }
            composable<OnboardingSports> {
                onboarding_2_sports(
                    initialSelectedSports = onboardingState.selectedSports,
                    initialName = onboardingState.name,
                    initialBirthdate = onboardingState.birthdate,
                    onFinish = { name, birthdate, sports ->
                        profileRepository.completeOnboarding(name, birthdate, sports)
                    },
                    onSaved = {
                        onOnboardingStateChanged(onboardingState.copy(complete = true))
                        navController.enterApp()
                    },
                )
            }
        }

        navigation<MainGraph>(startDestination = ActivityList) {
            composable<ActivityList> {
                TemporaryNav(
                    "Ouvrir une activite" to { navController.navigate(ActivityDetail("demo-1")) },
                ) { ActivityListScreen() }
            }
            composable<MapTab> { MapScreen() }
            composable<CreateActivity> { CreateActivityScreen() }
            composable<Messages> {
                TemporaryNav(
                    "Ouvrir une conversation" to { navController.navigate(Conversation("demo-1")) },
                ) {
                    ConversationListScreen(
                        navController = navController,
                        conversationRepository = conversationRepository,
                    )
                }
            }
            composable<Profile> { entry ->
                val parentEntry = remember(entry) { navController.getBackStackEntry<MainGraph>() }
                val viewModel: ProfileViewModel = viewModel(parentEntry)
                ProfileScreen(
                    viewModel = viewModel,
                    onEditProfile = { navController.navigate(EditProfile) },
                    onLogout = {
                        authRepository.signOut().also { result ->
                            if (result.isSuccess) {
                                onOnboardingStateChanged(OnboardingState())
                                navController.leaveApp()
                            }
                        }
                    },
                )
            }
            composable<EditProfile> { entry ->
                val parentEntry = remember(entry) { navController.getBackStackEntry<MainGraph>() }
                val viewModel: ProfileViewModel = viewModel(parentEntry)
                EditProfileScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }

            composable<ActivityDetail> { entry ->
                ActivityDetailScreen(activityId = entry.toRoute<ActivityDetail>().activityId)
            }
            composable<Conversation> { entry ->
                ConversationScreen(
                    conversationId = entry.toRoute<Conversation>().conversationId,
                    navController = navController,
                    conversationRepository = conversationRepository,
                )
            }
        }
    }
}

/**
 * Shows a screen plus temporary navigation buttons underneath.
 *
 * The buttons live here and NOT in the screen files, so nobody has to
 * resolve a conflict on their own screen.
 *
 * When you write your real screen: give it a callback parameter
 * (e.g. onLoginSuccess: () -> Unit), wire it here, and drop TemporaryNav.
 */
@Composable
private fun TemporaryNav(
    vararg buttons: Pair<String, () -> Unit>,
    screen: @Composable () -> Unit,
) {
    Column {
        screen()
        buttons.forEach { (label, action) ->
            Button(onClick = action) { Text(label) }
        }
    }
}

/**
 * Enters the app after sign in.
 * Clears the auth screens from the back stack, so pressing back closes
 * the app instead of returning to the login screen.
 */
private fun NavHostController.enterApp() {
    navigate(MainGraph) {
        popUpTo(AuthGraph) { inclusive = true }
    }
}

private fun NavHostController.openAfterAuthentication(state: OnboardingState) {
    if (state.complete) {
        enterApp()
        return
    }

    navigate(Onboarding) {
        popUpTo(Login) { inclusive = true }
        launchSingleTop = true
    }
}

private fun NavHostController.leaveApp() {
    navigate(Login) {
        popUpTo(MainGraph) { inclusive = true }
        launchSingleTop = true
    }
}
