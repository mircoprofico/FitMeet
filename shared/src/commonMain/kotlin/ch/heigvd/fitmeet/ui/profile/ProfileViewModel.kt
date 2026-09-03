package ch.heigvd.fitmeet.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.heigvd.fitmeet.data.profile.PreviewProfileRepository
import ch.heigvd.fitmeet.data.profile.ProfileRepository
import ch.heigvd.fitmeet.model.UserProfile
import ch.heigvd.fitmeet.model.UserSport
import ch.heigvd.fitmeet.ui.theme.Level
import ch.heigvd.fitmeet.ui.theme.Sport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository = PreviewProfileRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = repository.fetchProfile().fold(
                onSuccess = { ProfileUiState.Success(it) },
                onFailure = { ProfileUiState.Error(it.message ?: "Impossible de charger le profil.") },
            )
        }
    }

    fun updateProfile(updatedProfile: UserProfile) {
        _uiState.value = ProfileUiState.Success(updatedProfile)
        viewModelScope.launch {
            repository.updateProfile(updatedProfile)
        }
    }
}

sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(val profile: UserProfile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

// Only used by PreviewProfileRepository and Compose previews — never shown in production.
internal val previewProfile = UserProfile(
    id = "preview",
    firstName = "Roger",
    lastName = "Federer",
    age = 45,
    city = "Lausanne",
    bio = "Aperçu : profil simulé.",
    sports = listOf(
        UserSport(Sport.TENNIS, Level.BEGINNER),
        UserSport(Sport.RUNNING, Level.INTERMEDIATE),
    ),
    activitiesCreated = 12,
    activitiesJoined = 34,
)
