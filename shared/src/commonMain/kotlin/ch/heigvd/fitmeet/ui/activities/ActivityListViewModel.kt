package ch.heigvd.fitmeet.ui.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.heigvd.fitmeet.data.activities.ActivityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActivityListViewModel(
    private val repository: ActivityRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ActivityListUiState>(ActivityListUiState.Loading)
    val uiState: StateFlow<ActivityListUiState> = _uiState

    private val _joinedEventIds = MutableStateFlow<Set<String>>(emptySet())
    val joinedEventIds: StateFlow<Set<String>> = _joinedEventIds

    init {
        refresh()
        viewModelScope.launch {
            repository.participatingEventIds().onSuccess { ids ->
                _joinedEventIds.value = ids
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = ActivityListUiState.Loading
            _uiState.value = repository.nearbyActivities().fold(
                onSuccess = { ActivityListUiState.Success(it) },
                onFailure = { ActivityListUiState.Error("Impossible de charger les activités") },
            )
        }
    }

    fun joinEvent(eventId: String) {
        viewModelScope.launch {
            repository.joinEvent(eventId).onSuccess {
                _joinedEventIds.value = _joinedEventIds.value + eventId
            }
        }
    }
}
