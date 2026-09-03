package ch.heigvd.fitmeet.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ch.heigvd.fitmeet.model.Activity
import kotlinx.coroutines.delay

private const val FALLBACK_LAT = 46.7785  // Yverdon-les-Bains
private const val FALLBACK_LNG = 6.6411

@Composable
fun MapScreen(
    onMapClick: ((Double, Double) -> Unit)? = null,
    selectedLat: Double? = null,
    selectedLng: Double? = null,
    activities: List<Activity> = emptyList(),
    onActivityClick: ((Activity) -> Unit)? = null,
) {
    // When a position is pre-selected (confirmation screen), use it immediately.
    // Otherwise wait for LocationEffect — show a spinner until the first real
    // position arrives so we never show a hardcoded fallback point.
    var cameraLat by remember { mutableStateOf(selectedLat) }
    var cameraLng by remember { mutableStateOf(selectedLng) }

    if (selectedLat == null) {
        LocationEffect { lat, lng ->
            if (cameraLat == null) {
                cameraLat = lat
                cameraLng = lng
            }
        }
        LaunchedEffect(Unit) {
            delay(5_000)
            if (cameraLat == null) {
                cameraLat = FALLBACK_LAT
                cameraLng = FALLBACK_LNG
            }
        }
    }

    val lat = cameraLat
    val lng = cameraLng
    if (lat != null && lng != null) {
        PlatformMap(
            latitude = lat,
            longitude = lng,
            modifier = Modifier.fillMaxSize(),
            onMapClick = onMapClick,
            selectedLat = selectedLat,
            selectedLng = selectedLng,
            activities = activities,
            onActivityClick = onActivityClick,
        )
    } else {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
expect fun PlatformMap(
    latitude: Double,
    longitude: Double,
    modifier: Modifier,
    onMapClick: ((Double, Double) -> Unit)? = null,
    selectedLat: Double? = null,
    selectedLng: Double? = null,
    activities: List<Activity> = emptyList(),
    onActivityClick: ((Activity) -> Unit)? = null,
)

@Composable
expect fun LocationEffect(onLocation: (Double, Double) -> Unit)
