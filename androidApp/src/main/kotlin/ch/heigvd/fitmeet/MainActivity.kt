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
import ch.heigvd.fitmeet.data.FitMeetRepositories
import ch.heigvd.fitmeet.data.createFitMeetRepositories
import ch.heigvd.fitmeet.data.createUnconfiguredFitMeetRepositories
import ch.heigvd.fitmeet.data.auth.PreviewAuthRepository

class MainActivity : ComponentActivity() {
    private var authenticationCallbackUrl by mutableStateOf<String?>(null)
    private val repositories: FitMeetRepositories by lazy {
        if (BuildConfig.SUPABASE_URL.isBlank() || BuildConfig.SUPABASE_PUBLISHABLE_KEY.isBlank()) {
            createUnconfiguredFitMeetRepositories()
        } else {
            createFitMeetRepositories(
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
                authRepository = repositories.authRepository,
                profileRepository = repositories.profileRepository,
                conversationRepository = repositories.conversationRepository,
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
