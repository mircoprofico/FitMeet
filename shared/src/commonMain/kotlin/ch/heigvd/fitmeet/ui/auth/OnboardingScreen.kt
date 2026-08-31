package ch.heigvd.fitmeet.ui.auth

import androidx.compose.runtime.Composable
import ch.heigvd.fitmeet.ui.onboarding.onboarding_1_name_birthdate

@Composable
fun OnboardingScreen(onNext: () -> Unit = {}) {
    onboarding_1_name_birthdate(onNext = onNext)
}
