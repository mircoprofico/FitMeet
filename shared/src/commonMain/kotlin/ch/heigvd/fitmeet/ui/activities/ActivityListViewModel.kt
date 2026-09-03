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

    init {
        refresh()
    }

    // optimistic: the button flips straight away, then we call supabase.
    // if it refuses, the previous state comes back and a refresh resyncs.
    fun toggleJoin(activityId: String) {
        val current = _uiState.value as? ActivityListUiState.Success ?: return
        val target = current.activities.firstOrNull { it.id == activityId } ?: return
        if (target.isOrganizer) return
        if (!target.isJoined && target.isFull) return

        val joining = !target.isJoined
        _uiState.value = current.copy(
            activities = current.activities.map {
                if (it.id != activityId) it
                else it.copy(
                    isJoined = joining,
                    participants = it.participants + if (joining) 1 else -1,
                )
            },
        )

        viewModelScope.launch {
            val result = if (joining) repository.join(activityId)
                         else repository.leave(activityId)
            if (result.isFailure) _uiState.value = current
            // The optimistic state is already on screen. Refresh it quietly
            // so tapping the action never replaces the list with a loader.
            refresh(showLoading = false)
        }
    }

    fun refresh(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) _uiState.value = ActivityListUiState.Loading
            _uiState.value = repository.nearbyActivities().fold(
                onSuccess = { ActivityListUiState.Success(it) },
                onFailure = {
                    // While an existing list is visible, keep it on a
                    // transient refresh failure. Initial loading still gets
                    // the normal retryable error screen.
                    if (showLoading) ActivityListUiState.Error("Impossible de charger les activités")
                    else _uiState.value
                },
            )
        }
    }
}
