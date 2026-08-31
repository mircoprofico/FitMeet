package ch.heigvd.fitmeet.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

private const val DEFAULT_LAT = 46.5197
private const val DEFAULT_LNG = 6.6323

@Composable
fun MapScreen() {
    var latitude by remember { mutableStateOf(DEFAULT_LAT) }
    var longitude by remember { mutableStateOf(DEFAULT_LNG) }

    LocationEffect { lat, lng ->
        latitude = lat
        longitude = lng
    }

    PlatformMap(
        latitude = latitude,
        longitude = longitude,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
expect fun PlatformMap(latitude: Double, longitude: Double, modifier: Modifier)

@Composable
expect fun LocationEffect(onLocation: (Double, Double) -> Unit)
