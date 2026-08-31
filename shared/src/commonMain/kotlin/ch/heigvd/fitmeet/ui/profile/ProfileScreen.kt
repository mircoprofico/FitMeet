package ch.heigvd.fitmeet.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import ch.heigvd.fitmeet.data.auth.AuthActionResult
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.heigvd.fitmeet.model.UserProfile
import ch.heigvd.fitmeet.model.UserSport
import ch.heigvd.fitmeet.ui.components.LevelChip
import ch.heigvd.fitmeet.ui.components.SportChip

private val ColorPrimary = Color(0xFF16B0D7)
private val ColorDanger = Color(0xFFCF3838)
private val ColorTextSecondary = Color(0xFF888888)
private val ColorTextBody = Color(0xFF444444)
private val ColorCardBackground = Color(0xFFF5F5F5)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onEditProfile: () -> Unit = {},
    onLogout: suspend () -> AuthActionResult = { AuthActionResult(true, "Aperçu") },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var logoutMessage by remember { mutableStateOf<AuthActionResult?>(null) }
    val scope = rememberCoroutineScope()

    when (val state = uiState) {
        is ProfileUiState.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { CircularProgressIndicator(color = ColorPrimary) }
        is ProfileUiState.Success -> ProfileContent(
            profile = state.profile,
            onEditProfile = onEditProfile,
            onLogout = { scope.launch { logoutMessage = onLogout() } },
            logoutMessage = logoutMessage,
        )
    }
}

@Composable
private fun ProfileContent(
    profile: UserProfile,
    onEditProfile: () -> Unit = {},
    onLogout: () -> Unit = {},
    logoutMessage: AuthActionResult? = null,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Spacer(Modifier.height(32.dp))
            ProfileHeader(profile)
            HorizontalDivider()
            SportsSection(profile.sports)
            HorizontalDivider()
            StatsSection(profile.activitiesCreated, profile.activitiesJoined)
            SignOutButton(onClick = onLogout)
            logoutMessage?.let { Text(it.message, color = ColorDanger) }
            Spacer(Modifier.height(16.dp))
        }
        androidx.compose.material3.TextButton(
            onClick = onEditProfile,
            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
        ) {
            Text("✏️ Modifier", color = ColorPrimary, fontSize = 13.sp)
        }
    }
}

@Composable
private fun ProfileHeader(profile: UserProfile) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(ColorPrimary),
        ) {
            Text(
                text = profile.initials,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = profile.fullName + ", " + profile.age,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "📍 ${profile.city}",
            fontSize = 14.sp,
            color = ColorTextSecondary,
        )
        if (profile.bio.isNotBlank()) {
            Text(
                text = profile.bio,
                fontSize = 14.sp,
                color = ColorTextBody,
                lineHeight = 20.sp,
            )
        }
    }
}

@Composable
private fun SportsSection(sports: List<UserSport>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Mes sports",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
        sports.forEach { userSport ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SportChip(userSport.sport)
                LevelChip(userSport.level)
            }
        }
    }
}

@Composable
private fun StatsSection(activitiesCreated: Int, activitiesJoined: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Statistiques",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(label = "créées", value = activitiesCreated, modifier = Modifier.weight(1f))
            StatCard(label = "rejointes", value = activitiesJoined, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatCard(label: String, value: Int, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ColorCardBackground)
            .padding(vertical = 16.dp),
    ) {
        Text(
            text = value.toString(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = ColorPrimary,
        )
        Text(
            text = "activités $label",
            fontSize = 12.sp,
            color = ColorTextSecondary,
        )
    }
}

@Composable
private fun SignOutButton(onClick: () -> Unit = {}) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorDanger),
        border = BorderStroke(1.dp, ColorDanger),
    ) {
        Text("Se déconnecter")
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    ProfileContent(profile = mockProfile)
}
