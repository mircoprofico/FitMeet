package ch.heigvd.fitmeet.ui.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.heigvd.fitmeet.data.activities.ActivityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// holds the state of the list screen and talks to the repository.
// the screen itself stays dumb: it only draws whatever state it gets.
class ActivityListViewModel(
    private val repository: ActivityRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ActivityListUiState>(ActivityListUiState.Loading)
    val uiState: StateFlow<ActivityListUiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        // launch opens a coroutine, the only place a suspend call is allowed.
        // viewModelScope cancels it if the user leaves the screen.
        viewModelScope.launch {
            _uiState.value = ActivityListUiState.Loading
            _uiState.value = repository.nearbyActivities().fold(
                onSuccess = { ActivityListUiState.Success(it) },
                onFailure = { ActivityListUiState.Error("Impossible de charger les activités") },
            )
        }
    }
}
