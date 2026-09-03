package ch.heigvd.fitmeet.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.heigvd.fitmeet.data.profile.birthdateToIso
import fitmeet.shared.generated.resources.Res
import fitmeet.shared.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

private val Navy = Color(0xFF0B2545)
private val Green = Color(0xFF429A72)
private val LightText = Color(0xFFE6E7EA)

@Preview
@Composable
fun onboarding_1_name_birthdate(
    initialName: String = "",
    initialBirthdate: String = "",
    onNext: (String, String) -> Unit = { _, _ -> },
) {
    var name by remember(initialName) { mutableStateOf(initialName) }
    var birthdateDigits by remember(initialBirthdate) {
        mutableStateOf(initialBirthdate.filter(Char::isDigit).take(8))
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val birthdate = formatBirthdate(birthdateDigits)

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize().background(Navy).padding(horizontal = 28.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Image(painterResource(Res.drawable.logo), "FitMeet", modifier = Modifier.height(84.dp))
            Spacer(modifier = Modifier.height(48.dp))

            Column(modifier = Modifier.widthIn(max = 420.dp)) {
                Text(
                    text = "Entrez votre nom",
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth(),
                    color = LightText,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                OnboardingTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nom",
                    keyboardType = KeyboardType.Text,
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Entrez date de naissance",
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth(),
                    color = LightText,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                OnboardingTextField(
                    value = birthdateDigits,
                    onValueChange = {
                        birthdateDigits = it.filter(Char::isDigit).take(8)
                    },
                    label = "Date de naissance : jj/mm/aaaa",
                    keyboardType = KeyboardType.Number,
                    visualTransformation = BirthdateVisualTransformation,
                )

                errorMessage?.let {
                    Text(it, color = Color(0xFFFFB4AB))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    when {
                        name.isBlank() -> errorMessage = "Saisissez votre nom."
                        birthdateToIso(birthdate) == null -> {
                            errorMessage = "Saisissez une date valide au format jj/mm/aaaa."
                        }
                        else -> {
                            errorMessage = null
                            onNext(name.trim(), birthdate)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Color.White),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.align(Alignment.End),
            ) {
                Text("Suivant", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun OnboardingTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(label, color = LightText) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Green,
            unfocusedBorderColor = LightText.copy(alpha = 0.7f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Green,
        ),
    )
}

private fun formatBirthdate(digits: String): String = buildString {
    digits.take(2).forEach(::append)
    if (digits.length > 2) {
        append('/')
        digits.drop(2).take(2).forEach(::append)
    }
    if (digits.length > 4) {
        append('/')
        digits.drop(4).take(4).forEach(::append)
    }
}

private object BirthdateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(AnnotatedString(formatBirthdate(text.text)), BirthdateOffsetMapping)
    }
}

private object BirthdateOffsetMapping : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int = when {
        offset <= 2 -> offset
        offset <= 4 -> offset + 1
        else -> offset + 2
    }.coerceAtMost(10)

    override fun transformedToOriginal(offset: Int): Int = when {
        offset <= 2 -> offset
        offset <= 5 -> offset - 1
        else -> offset - 2
    }.coerceIn(0, 8)
}
