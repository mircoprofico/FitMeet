package ch.heigvd.fitmeet.data.auth

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.parseFragmentAndImportSession
import io.github.jan.supabase.annotations.SupabaseInternal
import ch.heigvd.fitmeet.data.supabase.createFitMeetSupabaseClient

data class AuthActionResult(val isSuccess: Boolean, val message: String)

interface AuthRepository {
    suspend fun signIn(email: String, password: String): AuthActionResult
    suspend fun signUp(email: String, password: String): AuthActionResult
    suspend fun signOut(): AuthActionResult
    suspend fun requestPasswordReset(email: String): AuthActionResult
    suspend fun handleAuthenticationCallback(url: String): AuthActionResult
}

class SupabaseAuthRepository internal constructor(
    private val supabase: io.github.jan.supabase.SupabaseClient,
) : AuthRepository {
    override suspend fun signIn(email: String, password: String): AuthActionResult = runCatching {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        AuthActionResult(true, "Connexion réussie.")
    }.getOrElse(::authFailure)

    override suspend fun signUp(email: String, password: String): AuthActionResult = runCatching {
        supabase.auth.signUpWith(Email, redirectUrl = "fitmeet://auth") {
            this.email = email
            this.password = password
        }
        AuthActionResult(true, "Compte créé. Vérifiez votre e-mail avant de vous connecter.")
    }.getOrElse(::authFailure)

    override suspend fun signOut(): AuthActionResult = runCatching {
        supabase.auth.signOut()
        AuthActionResult(true, "Déconnexion réussie.")
    }.getOrElse(::authFailure)

    override suspend fun requestPasswordReset(email: String): AuthActionResult = runCatching {
        supabase.auth.resetPasswordForEmail(email)
        AuthActionResult(true, "Un e-mail de réinitialisation a été envoyé.")
    }.getOrElse(::authFailure)

    @OptIn(SupabaseInternal::class)
    override suspend fun handleAuthenticationCallback(url: String): AuthActionResult = runCatching {
        supabase.auth.parseFragmentAndImportSession(url) { }
        AuthActionResult(true, "Votre e-mail est confirmé.")
    }.getOrElse(::authFailure)

    private fun authFailure(error: Throwable) = AuthActionResult(
        isSuccess = false,
        message = error.message ?: "Une erreur est survenue. Réessayez.",
    )
}

fun createSupabaseAuthRepository(
    supabaseUrl: String,
    publishableKey: String,
): AuthRepository = SupabaseAuthRepository(createFitMeetSupabaseClient(supabaseUrl, publishableKey))

object PreviewAuthRepository : AuthRepository {
    override suspend fun signIn(email: String, password: String) = AuthActionResult(true, "Aperçu : connexion simulée.")
    override suspend fun signUp(email: String, password: String) = AuthActionResult(true, "Aperçu : compte simulé.")
    override suspend fun signOut() = AuthActionResult(true, "Aperçu : déconnexion simulée.")
    override suspend fun requestPasswordReset(email: String) = AuthActionResult(true, "Aperçu : e-mail simulé.")
    override suspend fun handleAuthenticationCallback(url: String) = AuthActionResult(true, "Aperçu : e-mail confirmé.")
}

object UnconfiguredAuthRepository : AuthRepository {
    private const val message = "Supabase n'est pas configuré. Ajoutez les clés dans local.properties."
    override suspend fun signIn(email: String, password: String) = AuthActionResult(false, message)
    override suspend fun signUp(email: String, password: String) = AuthActionResult(false, message)
    override suspend fun signOut() = AuthActionResult(true, "Déconnexion (hors ligne).")
    override suspend fun requestPasswordReset(email: String) = AuthActionResult(false, message)
    override suspend fun handleAuthenticationCallback(url: String) = AuthActionResult(false, message)
}
