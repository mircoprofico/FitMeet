package ch.heigvd.fitmeet.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
actual fun PlatformMap(
    latitude: Double,
    longitude: Double,
    modifier: Modifier,
    onMapClick: ((Double, Double) -> Unit)?,
    selectedLat: Double?,
    selectedLng: Double?,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("Carte iOS à implémenter (MapLibre iOS pod requis)")
    }
}

@Composable
actual fun LocationEffect(onLocation: (Double, Double) -> Unit) {
    // Lausanne par défaut jusqu'à l'implémentation de CLLocationManager
    LaunchedEffect(Unit) {
        onLocation(46.5197, 6.6323)
    }
}
