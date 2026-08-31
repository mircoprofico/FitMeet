package ch.heigvd.fitmeet.ui.auth

import androidx.compose.runtime.Composable
import ch.heigvd.fitmeet.data.profile.OnboardingState
import ch.heigvd.fitmeet.ui.onboarding.onboarding_1_name_birthdate

@Composable
fun OnboardingScreen(
    state: OnboardingState = OnboardingState(),
    onNext: (String, String) -> Unit = { _, _ -> },
) {
    onboarding_1_name_birthdate(
        initialName = state.name,
        initialBirthdate = state.birthdate,
        onNext = onNext,
    )
}
