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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import fitmeet.shared.generated.resources.Res
import fitmeet.shared.generated.resources.logo
import androidx.compose.foundation.shape.RoundedCornerShape

private val Navy = Color(0xFF102E53)
private val Green = Color(0xFF429A72)
private val LightText = Color(0xFFE6E7EA)

@Preview
@Composable
fun onboarding_1_name_birthdate (
    onNext: () -> Unit = {},
) {
    var name by remember { mutableStateOf("") }
    var birthdate by remember { mutableStateOf("") }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Navy)
                .padding(horizontal = 28.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = "FitMeet",
                modifier = Modifier.height(84.dp),
            )

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

                textField(
                    value = name,
                    onValueChange = { name = it },
                    label = "nom",
                    keyboardType = KeyboardType.Email,
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

                textField(
                    value = birthdate,
                    onValueChange = { birthdate = formatBirthdate(it) },
                    label = "Date de naissance : jj/mm/aaaa",
                    keyboardType = KeyboardType.Number,
                )

                Spacer(modifier = Modifier.height(24.dp))


            }

            Button(onClick = onNext,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3E8E68)
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.align(Alignment.End)
            )
            {
                Text("Suivant",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.W600
                )
            }



        }
    }
}

@Composable
private fun textField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(label, color = LightText) },
        singleLine = true,

        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Green,
            unfocusedBorderColor = LightText.copy(alpha = 0.7f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Green,
        ),
    )
}

private fun formatBirthdate(input: String): String {
    val digits = input.filter { it.isDigit() }.take(8)

    return buildString {
        digits.take(2).forEach(::append)

        if (digits.length > 2) {
            append("/")
            digits.drop(2).take(2).forEach(::append)
        }

        if (digits.length > 4) {
            append("/")
            digits.drop(4).take(4).forEach(::append)
        }
    }
}
