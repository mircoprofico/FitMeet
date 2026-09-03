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

    // true only while a pull to refresh is in flight, so the spinner the
    // gesture shows knows when to retract. the first load does not use it:
    // that one already has the full screen loader.
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    // names per activity, filled the first time its sheet is opened. a map
    // and not a single list: reopening a sheet already visited shows the
    // faces straight away instead of blinking.
    private val _attendees = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val attendees: StateFlow<Map<String, List<String>>> = _attendees

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

    // what the pull gesture calls: no full screen loader, and the little
    // spinner stays until supabase has actually answered
    fun refreshFromPull() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                reload(showLoading = false)
            } finally {
                // finally, so a cancelled scope never leaves the spinner on
                _isRefreshing.value = false
            }
        }
    }

    fun loadAttendees(activityId: String) {
        if (activityId in _attendees.value) return
        viewModelScope.launch {
            repository.attendeeNames(activityId).onSuccess { names ->
                _attendees.value = _attendees.value + (activityId to names)
            }
        }
    }

    fun refresh(showLoading: Boolean = true) {
        viewModelScope.launch { reload(showLoading) }
    }

    private suspend fun reload(showLoading: Boolean) {
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
