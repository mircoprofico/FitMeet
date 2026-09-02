package ch.heigvd.fitmeet.data

import ch.heigvd.fitmeet.data.activities.ActivityRepository
import ch.heigvd.fitmeet.data.activities.PreviewActivityRepository
import ch.heigvd.fitmeet.data.activities.SupabaseActivityRepository
import ch.heigvd.fitmeet.data.auth.AuthRepository
import ch.heigvd.fitmeet.data.auth.SupabaseAuthRepository
import ch.heigvd.fitmeet.data.auth.UnconfiguredAuthRepository
import ch.heigvd.fitmeet.data.activityCreation.EventRepository
import ch.heigvd.fitmeet.data.activityCreation.SupabaseEventRepository
import ch.heigvd.fitmeet.data.activityCreation.UnconfiguredEventRepository
import ch.heigvd.fitmeet.data.messages.ConversationRepository
import ch.heigvd.fitmeet.data.messages.SupabaseConversationRepository
import ch.heigvd.fitmeet.data.messages.UnconfiguredConversationRepository
import ch.heigvd.fitmeet.data.profile.ProfileRepository
import ch.heigvd.fitmeet.data.profile.SupabaseProfileRepository
import ch.heigvd.fitmeet.data.profile.UnconfiguredProfileRepository
import ch.heigvd.fitmeet.data.supabase.createFitMeetSupabaseClient

data class FitMeetRepositories(
    val authRepository: AuthRepository,
    val profileRepository: ProfileRepository,
    val conversationRepository: ConversationRepository,
    val eventRepository: EventRepository,
    val activityRepository: ActivityRepository,
)

fun createFitMeetRepositories(
    supabaseUrl: String,
    publishableKey: String,
): FitMeetRepositories {
    val supabase = createFitMeetSupabaseClient(supabaseUrl, publishableKey)
    return FitMeetRepositories(
        authRepository = SupabaseAuthRepository(supabase),
        profileRepository = SupabaseProfileRepository(supabase),
        conversationRepository = SupabaseConversationRepository(supabase),
        eventRepository = SupabaseEventRepository(supabase),
        activityRepository = SupabaseActivityRepository(supabase),
    )
}

fun createUnconfiguredFitMeetRepositories() = FitMeetRepositories(
    authRepository = UnconfiguredAuthRepository,
    profileRepository = UnconfiguredProfileRepository,
    conversationRepository = UnconfiguredConversationRepository,
    eventRepository = UnconfiguredEventRepository,
    // no network configured: fall back on the sample list
    activityRepository = PreviewActivityRepository,
)
