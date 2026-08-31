package ch.heigvd.fitmeet.ui.profile

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import ch.heigvd.fitmeet.data.auth.AuthActionResult
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onLogout: suspend () -> AuthActionResult = { AuthActionResult(true, "Aperçu") },
) {
    var message by remember { mutableStateOf<AuthActionResult?>(null) }
    val scope = rememberCoroutineScope()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Profil")
        Button(onClick = { scope.launch { message = onLogout() } }) {
            Text("Se déconnecter")
        }
        message?.let { Text(it.message) }
    }
}
