package ch.heigvd.fitmeet.data.profile

import ch.heigvd.fitmeet.data.auth.AuthActionResult
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
}

object UnconfiguredProfileRepository : ProfileRepository {
    private const val message = "Supabase n'est pas configuré. Ajoutez les clés dans local.properties."
    override suspend fun getOnboardingState() = OnboardingState()
    override suspend fun completeOnboarding(name: String, birthdate: String, selectedSports: Set<String>) =
        AuthActionResult(false, message)
}
