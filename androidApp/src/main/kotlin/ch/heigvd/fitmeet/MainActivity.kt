package ch.heigvd.fitmeet

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.heigvd.fitmeet.data.auth.AuthRepository
import ch.heigvd.fitmeet.data.auth.PreviewAuthRepository
import ch.heigvd.fitmeet.data.auth.UnconfiguredAuthRepository
import ch.heigvd.fitmeet.data.auth.createSupabaseAuthRepository

class MainActivity : ComponentActivity() {
    private var authenticationCallbackUrl by mutableStateOf<String?>(null)
    private val authRepository: AuthRepository by lazy {
        if (BuildConfig.SUPABASE_URL.isBlank() || BuildConfig.SUPABASE_PUBLISHABLE_KEY.isBlank()) {
            UnconfiguredAuthRepository
        } else {
            createSupabaseAuthRepository(
                supabaseUrl = BuildConfig.SUPABASE_URL,
                publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        authenticationCallbackUrl = intent?.dataString

        setContent {
            App(
                authRepository = authRepository,
                authenticationCallbackUrl = authenticationCallbackUrl,
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        authenticationCallbackUrl = intent.dataString
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(authRepository = PreviewAuthRepository)
}
