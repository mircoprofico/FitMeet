package ch.heigvd.fitmeet.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.heigvd.fitmeet.data.auth.AuthActionResult
import fitmeet.shared.generated.resources.Res
import fitmeet.shared.generated.resources.logo
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

private val Navy = Color(0xFF0B2545)
private val Green = Color(0xFF429A72)
private val LightText = Color(0xFFE6E7EA)

@Preview
@Composable
fun RegisterScreen(
    onRegister: suspend (String, String) -> AuthActionResult = { _, _ -> AuthActionResult(true, "Aperçu") },
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<AuthActionResult?>(null) }
    val scope = rememberCoroutineScope()
    val passwordsMatch = password == confirmPassword
    val showConfirmationError = confirmPassword.isNotEmpty() && !passwordsMatch

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize().background(Navy).statusBarsPadding().padding(horizontal = 28.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Image(painterResource(Res.drawable.logo), "FitMeet", modifier = Modifier.height(84.dp))
            Spacer(modifier = Modifier.height(48.dp))
            Text("Créez votre compte", color = LightText, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(28.dp))

            Column(modifier = Modifier.widthIn(max = 420.dp)) {
                RegisterTextField(email, { email = it }, "Adresse e-mail", KeyboardType.Email)
                RegisterTextField(password, { password = it }, "Mot de passe", KeyboardType.Password, true, passwordVisible) {
                    passwordVisible = !passwordVisible
                }
                RegisterTextField(confirmPassword, { confirmPassword = it }, "Confirmation de mot passe", KeyboardType.Password, true, passwordVisible, showConfirmationError) {
                    passwordVisible = !passwordVisible
                }
                if (showConfirmationError) Text("Les mots de passe ne correspondent pas", color = Color(0xFFFFB4AB))
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    when {
                        email.isBlank() || password.isBlank() -> message = AuthActionResult(false, "Remplissez tous les champs.")
                        !passwordsMatch -> message = AuthActionResult(false, "Les mots de passe ne correspondent pas.")
                        else -> scope.launch { message = onRegister(email.trim(), password) }
                    }
                },
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth(0.5f).align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Color.White),
            ) { Text("Créer le compte", fontWeight = FontWeight.Bold) }

            message?.let { Text(it.message, color = if (it.isSuccess) Green else Color(0xFFFFB4AB)) }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    isError: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(label, color = LightText) },
        singleLine = true,
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            if (isPassword) TextButton(onClick = { onTogglePasswordVisibility?.invoke() }) {
                Text(if (showPassword) "Masquer" else "Afficher")
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Green,
            unfocusedBorderColor = LightText.copy(alpha = 0.7f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Green,
        ),
    )
}
