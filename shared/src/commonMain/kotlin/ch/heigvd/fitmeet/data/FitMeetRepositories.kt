package ch.heigvd.fitmeet.data

import ch.heigvd.fitmeet.data.auth.AuthRepository
import ch.heigvd.fitmeet.data.auth.SupabaseAuthRepository
import ch.heigvd.fitmeet.data.auth.UnconfiguredAuthRepository
import ch.heigvd.fitmeet.data.profile.ProfileRepository
import ch.heigvd.fitmeet.data.profile.SupabaseProfileRepository
import ch.heigvd.fitmeet.data.profile.UnconfiguredProfileRepository
import ch.heigvd.fitmeet.data.supabase.createFitMeetSupabaseClient

data class FitMeetRepositories(
    val authRepository: AuthRepository,
    val profileRepository: ProfileRepository,
)

fun createFitMeetRepositories(
    supabaseUrl: String,
    publishableKey: String,
): FitMeetRepositories {
    val supabase = createFitMeetSupabaseClient(supabaseUrl, publishableKey)
    return FitMeetRepositories(
        authRepository = SupabaseAuthRepository(supabase),
        profileRepository = SupabaseProfileRepository(supabase),
    )
}

fun createUnconfiguredFitMeetRepositories() = FitMeetRepositories(
    authRepository = UnconfiguredAuthRepository,
    profileRepository = UnconfiguredProfileRepository,
)
