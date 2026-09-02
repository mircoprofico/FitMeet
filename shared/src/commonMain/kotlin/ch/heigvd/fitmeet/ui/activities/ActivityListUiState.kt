package ch.heigvd.fitmeet.ui.activities

import ch.heigvd.fitmeet.model.Activity

// the three states the screen can be in, and nothing else.
// same shape as ProfileUiState so both screens read alike.
sealed class ActivityListUiState {
    data object Loading : ActivityListUiState()
    data class Success(val activities: List<Activity>) : ActivityListUiState()
    data class Error(val message: String) : ActivityListUiState()
}
