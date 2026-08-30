package ch.heigvd.fitmeet

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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

@Composable
@Preview
fun App(
    authRepository: AuthRepository = PreviewAuthRepository,
    authenticationCallbackUrl: String? = null,
) {
    MaterialTheme {
        val navController = rememberNavController()

        LaunchedEffect(authenticationCallbackUrl) {
            authenticationCallbackUrl?.let { callbackUrl ->
                val result = authRepository.handleAuthenticationCallback(callbackUrl)
                if (result.isSuccess) {
                    navController.navigate(Onboarding) {
                        popUpTo(Login) { inclusive = true }
                    }
                }
            }
        }

        NavHost(
            navController = navController,
            startDestination = Login,
        ) {
            composable<Login> {
                LoginScreen(
                    onCreateAccount = { navController.navigate(Register) },
                    onLogin = authRepository::signIn,
                    onForgotPassword = authRepository::requestPasswordReset,
                )
            }

            composable<Register> {
                RegisterScreen(
                    onRegister = authRepository::signUp,
                )
            }

            composable<Onboarding> {
                OnboardingScreen(
                    onNext = { navController.navigate(OnboardingSports) },
                )
            }

            composable<OnboardingSports> {
                onboarding_2_sports()
            }
        }
    }
}
