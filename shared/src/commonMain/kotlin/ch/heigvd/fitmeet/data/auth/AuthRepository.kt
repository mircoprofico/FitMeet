package ch.heigvd.fitmeet.data.auth

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.parseSessionFromUrl
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionSource

data class AuthActionResult(val isSuccess: Boolean, val message: String)

data class AuthRestoreResult(val isAuthenticated: Boolean)

sealed interface AuthCallback {
    data object PasswordRecovery : AuthCallback
    data object EmailConfirmation : AuthCallback
}

interface AuthRepository {
    suspend fun signIn(email: String, password: String): AuthActionResult
    suspend fun signUp(email: String, password: String): AuthActionResult
    suspend fun requestPasswordReset(email: String): AuthActionResult
    suspend fun updatePassword(password: String): AuthActionResult
    suspend fun handleAuthenticationCallback(url: String): Result<AuthCallback>
    suspend fun restoreSession(): AuthRestoreResult
    suspend fun signOut(): AuthActionResult
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

    override suspend fun requestPasswordReset(email: String): AuthActionResult = runCatching {
        supabase.auth.resetPasswordForEmail(
            email = email,
            redirectUrl = "fitmeet://auth/reset-password",
        )
        AuthActionResult(true, "Un e-mail de réinitialisation a été envoyé.")
    }.getOrElse(::authFailure)

    override suspend fun updatePassword(password: String): AuthActionResult = runCatching {
        require(password.length >= 6) { "Le mot de passe doit contenir au moins 6 caractères." }
        supabase.auth.updateUser {
            this.password = password
        }
        supabase.auth.signOut()
        AuthActionResult(true, "Mot de passe modifié. Connectez-vous à nouveau.")
    }.getOrElse(::authFailure)

    override suspend fun handleAuthenticationCallback(url: String): Result<AuthCallback> = runCatching {
        val session = supabase.auth.parseSessionFromUrl(url)
        val user = supabase.auth.retrieveUser(session.accessToken)
        supabase.auth.importSession(
            session.copy(user = user),
            source = SessionSource.External,
        )

        if (url.startsWith("fitmeet://auth/reset-password")) {
            AuthCallback.PasswordRecovery
        } else {
            AuthCallback.EmailConfirmation
        }
    }

    override suspend fun restoreSession(): AuthRestoreResult = runCatching {
        supabase.auth.awaitInitialization()
        AuthRestoreResult(supabase.auth.currentUserOrNull() != null)
    }.getOrElse { AuthRestoreResult(false) }

    override suspend fun signOut(): AuthActionResult = runCatching {
        supabase.auth.signOut()
        AuthActionResult(true, "Vous êtes déconnecté.")
    }.getOrElse(::authFailure)

    private fun authFailure(error: Throwable) = AuthActionResult(
        isSuccess = false,
        message = error.message ?: "Une erreur est survenue. Réessayez.",
    )
}

object PreviewAuthRepository : AuthRepository {
    override suspend fun signIn(email: String, password: String) = AuthActionResult(true, "Aperçu : connexion simulée.")
    override suspend fun signUp(email: String, password: String) = AuthActionResult(true, "Aperçu : compte simulé.")
    override suspend fun requestPasswordReset(email: String) = AuthActionResult(true, "Aperçu : e-mail simulé.")
    override suspend fun updatePassword(password: String) = AuthActionResult(true, "Aperçu : mot de passe modifié.")
    override suspend fun handleAuthenticationCallback(url: String) = Result.success(
        if (url.startsWith("fitmeet://auth/reset-password")) {
            AuthCallback.PasswordRecovery
        } else {
            AuthCallback.EmailConfirmation
        },
    )
    override suspend fun restoreSession() = AuthRestoreResult(false)
    override suspend fun signOut() = AuthActionResult(true, "Aperçu : déconnexion simulée.")
}

object UnconfiguredAuthRepository : AuthRepository {
    private const val message = "Supabase n'est pas configuré. Ajoutez les clés dans local.properties."
    override suspend fun signIn(email: String, password: String) = AuthActionResult(false, message)
    override suspend fun signUp(email: String, password: String) = AuthActionResult(false, message)
    override suspend fun requestPasswordReset(email: String) = AuthActionResult(false, message)
    override suspend fun updatePassword(password: String) = AuthActionResult(false, message)
    override suspend fun handleAuthenticationCallback(url: String) = Result.failure<AuthCallback>(
        IllegalStateException(message),
    )
    override suspend fun restoreSession() = AuthRestoreResult(false)
    override suspend fun signOut() = AuthActionResult(false, message)
}
