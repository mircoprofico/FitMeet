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

    // optimistic: the button flips straight away, then we call supabase.
    // if it refuses, the previous state comes back and a resync fixes it.
    fun toggleJoin(activityId: String) {
        val before = _uiState.value as? ActivityListUiState.Success ?: return
        val target = before.activities.firstOrNull { it.id == activityId } ?: return
        if (target.isOrganizer) return
        if (!target.isJoined && target.isFull) return

        val joining = !target.isJoined
        _uiState.value = before.copy(
            activities = before.activities.map {
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
            // the server has the truth, so we go and read it again. on a
            // failure the optimistic guess is dropped first, otherwise a
            // failing resync would leave the wrong button showing.
            if (result.isFailure) _uiState.value = before
            // silent: no Loading here, the list must not blink and the
            // detail sheet must not lose the activity it is showing
            load(showLoading = false)
        }
    }

    fun refresh() {
        // launch opens a coroutine, the only place a suspend call is allowed.
        // viewModelScope cancels it if the user leaves the screen.
        viewModelScope.launch { load(showLoading = true) }
    }

    private suspend fun load(showLoading: Boolean) {
        if (showLoading) _uiState.value = ActivityListUiState.Loading
        _uiState.value = repository.nearbyActivities().fold(
            onSuccess = { ActivityListUiState.Success(it) },
            // a failed background resync must not wipe a list that is
            // already on screen: the optimistic state stays instead
            onFailure = {
                if (showLoading) ActivityListUiState.Error("Impossible de charger les activités")
                else _uiState.value
            },
        )
    }
}
