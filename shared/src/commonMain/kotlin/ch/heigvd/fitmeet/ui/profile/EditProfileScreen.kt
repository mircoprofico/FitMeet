package ch.heigvd.fitmeet.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.heigvd.fitmeet.model.UserSport
import ch.heigvd.fitmeet.ui.components.SportChip
import ch.heigvd.fitmeet.ui.theme.Level
import ch.heigvd.fitmeet.ui.theme.Sport

private val ColorPrimary = Color(0xFF16B0D7)
private val ColorDanger = Color(0xFFCF3838)
private val editableLevels = listOf(Level.BEGINNER, Level.INTERMEDIATE, Level.ADVANCED)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onBack: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profile = (uiState as? ProfileUiState.Success)?.profile ?: return

    var firstName by remember { mutableStateOf(profile.firstName) }
    var lastName by remember { mutableStateOf(profile.lastName) }
    var age by remember { mutableStateOf(profile.age.toString()) }
    var city by remember { mutableStateOf(profile.city) }
    var bio by remember { mutableStateOf(profile.bio) }
    var sports by remember { mutableStateOf(profile.sports) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modifier le profil", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("← Retour") }
                },
                actions = {
                    TextButton(onClick = {
                        viewModel.updateProfile(
                            profile.copy(
                                firstName = firstName.trim(),
                                lastName = lastName.trim(),
                                age = age.toIntOrNull() ?: profile.age,
                                city = city.trim(),
                                bio = bio.trim(),
                                sports = sports,
                            )
                        )
                        onBack()
                    }) {
                        Text("Sauvegarder", color = ColorPrimary, fontWeight = FontWeight.SemiBold)
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(Modifier.height(4.dp))

            SectionTitle("Informations")
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Prénom") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Nom") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Âge") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Ville") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            SectionTitle("Mes sports")
            SportsEditor(sports = sports, onSportsChanged = { sports = it })

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SportsEditor(
    sports: List<UserSport>,
    onSportsChanged: (List<UserSport>) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        sports.forEach { userSport ->
            SportEditRow(
                userSport = userSport,
                onLevelChanged = { newLevel ->
                    onSportsChanged(sports.map { if (it.sport == userSport.sport) it.copy(level = newLevel) else it })
                },
                onRemove = {
                    onSportsChanged(sports.filter { it.sport != userSport.sport })
                },
            )
        }

        val available = Sport.entries.filter { sport -> sports.none { it.sport == sport } }
        if (available.isNotEmpty()) {
            AddSportButton(
                available = available,
                onAdd = { sport -> onSportsChanged(sports + UserSport(sport, Level.BEGINNER)) },
            )
        }
    }
}

@Composable
private fun SportEditRow(
    userSport: UserSport,
    onLevelChanged: (Level) -> Unit,
    onRemove: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5))
            .padding(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SportChip(userSport.sport)
            Text(
                text = "✕",
                color = ColorDanger,
                fontSize = 16.sp,
                modifier = Modifier.clickable { onRemove() }.padding(4.dp),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            editableLevels.forEach { level ->
                val selected = userSport.level == level
                Text(
                    text = level.label,
                    color = if (selected) Color.White else level.color,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (selected) level.color else level.color.copy(alpha = 0.15f))
                        .clickable { onLevelChanged(level) }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun AddSportButton(available: List<Sport>, onAdd: (Sport) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text("+ Ajouter un sport", color = ColorPrimary)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            available.forEach { sport ->
                DropdownMenuItem(
                    text = { SportChip(sport) },
                    onClick = { onAdd(sport); expanded = false },
                )
            }
        }
    }
}

@Preview
@Composable
private fun EditProfileScreenPreview() {
    EditProfileScreen()
}
