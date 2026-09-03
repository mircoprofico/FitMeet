package ch.heigvd.fitmeet.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ch.heigvd.fitmeet.model.Activity

private const val DEFAULT_LAT = 46.5197
private const val DEFAULT_LNG = 6.6323

@Composable
fun MapScreen(
    onMapClick: ((Double, Double) -> Unit)? = null,
    selectedLat: Double? = null,
    selectedLng: Double? = null,
    activities: List<Activity> = emptyList(),
    onActivityClick: ((Activity) -> Unit)? = null,
) {
    // when a location is already chosen (e.g. confirmation screen), start the
    // camera there and skip LocationEffect so it does not override the pin.
    var latitude by remember { mutableStateOf(selectedLat ?: DEFAULT_LAT) }
    var longitude by remember { mutableStateOf(selectedLng ?: DEFAULT_LNG) }

    if (selectedLat == null) {
        LocationEffect { lat, lng ->
            latitude = lat
            longitude = lng
        }
    }

    PlatformMap(
        latitude = latitude,
        longitude = longitude,
        modifier = Modifier.fillMaxSize(),
        onMapClick = onMapClick,
        selectedLat = selectedLat,
        selectedLng = selectedLng,
        activities = activities,
        onActivityClick = onActivityClick,
    )
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
