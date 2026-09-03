package ch.heigvd.fitmeet

import androidx.compose.ui.window.ComposeUIViewController
import ch.heigvd.fitmeet.data.createFitMeetRepositories
import ch.heigvd.fitmeet.data.createUnconfiguredFitMeetRepositories

fun MainViewController(
    supabaseUrl: String,
    publishableKey: String,
) = if (supabaseUrl.isBlank() || publishableKey.isBlank()) {
    createUnconfiguredFitMeetRepositories()
} else {
    createFitMeetRepositories(supabaseUrl, publishableKey)
}.let { repositories ->
    ComposeUIViewController {
        App(
            authRepository = repositories.authRepository,
            profileRepository = repositories.profileRepository,
            conversationRepository = repositories.conversationRepository,
            activityRepository = repositories.activityRepository,
            eventRepository = repositories.eventRepository,
        )
    }
}
