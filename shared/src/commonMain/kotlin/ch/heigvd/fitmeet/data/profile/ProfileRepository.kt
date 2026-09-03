package ch.heigvd.fitmeet.data.profile

import ch.heigvd.fitmeet.data.auth.AuthActionResult
import ch.heigvd.fitmeet.model.UserProfile
import ch.heigvd.fitmeet.model.UserSport
import ch.heigvd.fitmeet.ui.theme.Level
import ch.heigvd.fitmeet.ui.theme.Sport
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface ProfileRepository {
    suspend fun getOnboardingState(): OnboardingState
    suspend fun completeOnboarding(
        name: String,
        birthdate: String,
        selectedSports: Set<String>,
    ): AuthActionResult
    suspend fun fetchProfile(): UserProfile?
    suspend fun updateProfile(profile: UserProfile): AuthActionResult
}

@Serializable
private data class ProfileOnboardingDto(
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("birth_date") val birthDate: String? = null,
    @SerialName("preferred_sports") val preferredSports: List<String> = emptyList(),
    @SerialName("onboarding_complete") val onboardingComplete: Boolean = false,
)

@Serializable
private data class CompletedOnboardingUpdate(
    @SerialName("display_name") val displayName: String,
    @SerialName("birth_date") val birthDate: String,
    @SerialName("preferred_sports") val preferredSports: List<String>,
    @SerialName("onboarding_complete") val onboardingComplete: Boolean = true,
)

@Serializable
private data class SportLevelDto(val slug: String, val level: String)

@Serializable
private data class EventIdDto(val id: String)

@Serializable
private data class ParticipantIdDto(@SerialName("event_id") val eventId: String)

@Serializable
private data class ProfileDto(
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("birth_date") val birthDate: String? = null,
    @SerialName("preferred_sports") val preferredSports: List<String> = emptyList(),
    val city: String? = null,
    val bio: String? = null,
    @SerialName("sport_levels") val sportLevels: List<SportLevelDto> = emptyList(),
)

@Serializable
private data class ProfileUpdate(
    @SerialName("display_name") val displayName: String,
    @SerialName("preferred_sports") val preferredSports: List<String>,
    val city: String,
    val bio: String,
    @SerialName("sport_levels") val sportLevels: List<SportLevelDto>,
)

class SupabaseProfileRepository internal constructor(
    private val supabase: SupabaseClient,
) : ProfileRepository {
    override suspend fun getOnboardingState(): OnboardingState {
        val userId = supabase.auth.currentUserOrNull()?.id ?: return OnboardingState()
        val profile = runCatching {
            supabase.from("profiles").select(
                columns = Columns.list(
                    "id",
                    "display_name",
                    "birth_date",
                    "preferred_sports",
                    "onboarding_complete",
                ),
            ) {
                filter { eq("id", userId) }
            }.decodeSingleOrNull<ProfileOnboardingDto>()
        }.getOrNull()

        return profile?.let {
            OnboardingState(
                name = it.displayName.orEmpty(),
                birthdate = isoBirthdateToDisplay(it.birthDate),
                selectedSports = it.preferredSports.toSet(),
                complete = it.onboardingComplete,
            )
        } ?: OnboardingState()
    }

    override suspend fun completeOnboarding(
        name: String,
        birthdate: String,
        selectedSports: Set<String>,
    ): AuthActionResult {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: return AuthActionResult(false, "Votre session a expiré. Reconnectez-vous.")
        val isoBirthdate = birthdateToIso(birthdate)
            ?: return AuthActionResult(false, "Saisissez une date valide au format jj/mm/aaaa.")
        if (name.isBlank()) return AuthActionResult(false, "Saisissez votre nom.")
        if (selectedSports.isEmpty()) return AuthActionResult(false, "Sélectionnez au moins un sport.")

        return runCatching {
            supabase.from("profiles").update(
                CompletedOnboardingUpdate(name.trim(), isoBirthdate, selectedSports.toList()),
            ) {
                filter { eq("id", userId) }
            }
            AuthActionResult(true, "Profil terminé.")
        }.getOrElse { error ->
            AuthActionResult(false, error.message ?: "Une erreur est survenue. Réessayez.")
        }
    }

    override suspend fun fetchProfile(): UserProfile? {
        val userId = supabase.auth.currentUserOrNull()?.id ?: return null
        return runCatching {
            val dto = supabase.from("profiles").select(
                columns = Columns.list(
                    "display_name", "birth_date", "preferred_sports",
                    "city", "bio", "sport_levels",
                ),
            ) {
                filter { eq("id", userId) }
            }.decodeSingleOrNull<ProfileDto>() ?: return@runCatching null

            val createdCount = supabase.from("events")
                .select(columns = Columns.list("id")) { filter { eq("organizer_id", userId) } }
                .decodeList<EventIdDto>().size

            val joinedCount = supabase.from("event_participants")
                .select(columns = Columns.list("event_id")) { filter { eq("user_id", userId) } }
                .decodeList<ParticipantIdDto>().size

            dto.toUserProfile(userId, createdCount, joinedCount)
        }.getOrNull()
    }

    override suspend fun updateProfile(profile: UserProfile): AuthActionResult {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: return AuthActionResult(false, "Session expirée. Reconnectez-vous.")
        return runCatching {
            supabase.from("profiles").update(
                ProfileUpdate(
                    displayName = "${profile.firstName} ${profile.lastName}".trim(),
                    preferredSports = profile.sports.map { it.sport.toSlug() },
                    city = profile.city,
                    bio = profile.bio,
                    sportLevels = profile.sports.map {
                        SportLevelDto(it.sport.toSlug(), it.level.toSlug())
                    },
                ),
            ) {
                filter { eq("id", userId) }
            }
            AuthActionResult(true, "Profil mis à jour.")
        }.getOrElse { AuthActionResult(false, it.message ?: "Erreur lors de la mise à jour.") }
    }
}

object PreviewProfileRepository : ProfileRepository {
    private var onboardingState = OnboardingState()

    override suspend fun getOnboardingState() = onboardingState

    override suspend fun completeOnboarding(
        name: String,
        birthdate: String,
        selectedSports: Set<String>,
    ): AuthActionResult {
        onboardingState = onboardingState.copy(
            name = name,
            birthdate = birthdate,
            selectedSports = selectedSports,
            complete = true,
        )
        return AuthActionResult(true, "Aperçu : profil terminé.")
    }

    override suspend fun fetchProfile() = UserProfile(
        id = "preview",
        firstName = "Aperçu",
        lastName = "",
        age = 0,
        city = "",
        bio = "",
        sports = emptyList(),
        activitiesCreated = 0,
        activitiesJoined = 0,
    )

    override suspend fun updateProfile(profile: UserProfile) =
        AuthActionResult(true, "Aperçu : profil mis à jour.")
}

object UnconfiguredProfileRepository : ProfileRepository {
    private const val message = "Supabase n'est pas configuré. Ajoutez les clés dans local.properties."
    override suspend fun getOnboardingState() = OnboardingState()
    override suspend fun completeOnboarding(name: String, birthdate: String, selectedSports: Set<String>) =
        AuthActionResult(false, message)
    override suspend fun fetchProfile() = null
    override suspend fun updateProfile(profile: UserProfile) = AuthActionResult(false, message)
}

private fun ProfileDto.toUserProfile(id: String, createdCount: Int = 0, joinedCount: Int = 0): UserProfile {
    val nameParts = displayName.orEmpty().trim().split(" ", limit = 2)
    val levelsBySlug = sportLevels.associate { it.slug to levelOf(it.level) }
    return UserProfile(
        id = id,
        firstName = nameParts.getOrElse(0) { "" },
        lastName = nameParts.getOrElse(1) { "" },
        age = ageFromIsoBirthdate(birthDate),
        city = city.orEmpty(),
        bio = bio.orEmpty(),
        sports = preferredSports.map { slug ->
            UserSport(sportOf(slug), levelsBySlug[slug] ?: Level.BEGINNER)
        },
        activitiesCreated = createdCount,
        activitiesJoined = joinedCount,
    )
}

private fun sportOf(slug: String) = when (slug) {
    "football" -> Sport.FOOTBALL
    "basketball" -> Sport.BASKETBALL
    "volleyball" -> Sport.VOLLEYBALL
    "tennis" -> Sport.TENNIS
    "badminton" -> Sport.BADMINTON
    "running" -> Sport.RUNNING
    "cycling" -> Sport.CYCLING
    "hiking" -> Sport.HIKING
    else -> Sport.OTHER
}

private fun Sport.toSlug() = when (this) {
    Sport.FOOTBALL -> "football"
    Sport.BASKETBALL -> "basketball"
    Sport.VOLLEYBALL -> "volleyball"
    Sport.TENNIS -> "tennis"
    Sport.BADMINTON -> "badminton"
    Sport.RUNNING -> "running"
    Sport.CYCLING -> "cycling"
    Sport.HIKING -> "hiking"
    Sport.OTHER -> "other"
}

private fun levelOf(slug: String): Level = when (slug) {
    "intermediate" -> Level.INTERMEDIATE
    "advanced" -> Level.ADVANCED
    else -> Level.BEGINNER
}

private fun Level.toSlug(): String = when (this) {
    Level.INTERMEDIATE -> "intermediate"
    Level.ADVANCED -> "advanced"
    else -> "beginner"
}

private fun ageFromIsoBirthdate(iso: String?): Int {
    if (iso.isNullOrBlank()) return 0
    val birthYear = iso.split('-').firstOrNull()?.toIntOrNull() ?: return 0
    return maxOf(0, 2026 - birthYear)
}
