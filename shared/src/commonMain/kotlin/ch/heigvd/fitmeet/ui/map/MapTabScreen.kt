package ch.heigvd.fitmeet.ui.map

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ch.heigvd.fitmeet.data.activities.ActivityRepository
import ch.heigvd.fitmeet.model.Activity
import ch.heigvd.fitmeet.ui.activities.ActivityDetailScreen
import ch.heigvd.fitmeet.ui.activities.ActivityListUiState
import ch.heigvd.fitmeet.ui.activities.ActivityListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapTabScreen(activityRepository: ActivityRepository) {
    val viewModel = remember { ActivityListViewModel(activityRepository) }
    val state by viewModel.uiState.collectAsState()
    val activities = (state as? ActivityListUiState.Success)?.activities ?: emptyList()
    var selectedActivity by remember { mutableStateOf<Activity?>(null) }

    MapScreen(
        activities = activities,
        onActivityClick = { selectedActivity = it },
    )

    selectedActivity?.let { activity ->
        ModalBottomSheet(
            onDismissRequest = { selectedActivity = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            val live = (state as? ActivityListUiState.Success)
                ?.activities?.firstOrNull { it.id == activity.id } ?: activity
            ActivityDetailScreen(
                activity = live,
                isJoined = live.isJoined,
                onJoin = { viewModel.toggleJoin(live.id) },
            )
        }
    }
}
