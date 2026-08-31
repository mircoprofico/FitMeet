package ch.heigvd.fitmeet.ui.profile

import androidx.lifecycle.ViewModel
import ch.heigvd.fitmeet.model.UserProfile
import ch.heigvd.fitmeet.model.UserSport
import ch.heigvd.fitmeet.ui.theme.Level
import ch.heigvd.fitmeet.ui.theme.Sport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Success(mockProfile))
    val uiState: StateFlow<ProfileUiState> = _uiState

    fun updateProfile(updatedProfile: UserProfile) {
        _uiState.value = ProfileUiState.Success(updatedProfile)
    }
}

sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(val profile: UserProfile) : ProfileUiState()
}

internal val mockProfile = UserProfile(
    id = "user-1",
    firstName = "Roger",
    lastName = "Federer",
    age = 45,
    city = "Lausanne",
    bio = "Je débute en tennis. Mais je suis partant pour taper des balles avec n'importe qui. ",
    sports = listOf(
        UserSport(Sport.TENNIS, Level.BEGINNER),
        UserSport(Sport.RUNNING, Level.INTERMEDIATE),
    ),
    activitiesCreated = 12,
    activitiesJoined = 34,
)
