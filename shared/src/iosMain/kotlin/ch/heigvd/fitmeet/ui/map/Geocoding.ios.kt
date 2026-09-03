package ch.heigvd.fitmeet.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLGeocoder
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLPlacemark
import kotlin.coroutines.resume

@Composable
actual fun rememberPlaceName(latitude: Double?, longitude: Double?): PlaceName? {
    var place by remember(latitude, longitude) { mutableStateOf<PlaceName?>(null) }

    LaunchedEffect(latitude, longitude) {
        if (latitude == null || longitude == null) return@LaunchedEffect
        place = reverseGeocode(latitude, longitude)
    }

    return place
}

// CLGeocoder answers on a callback; suspendCancellableCoroutine turns that
// into something we can await, and cancels the lookup if the sheet closes
// before the answer comes back.
private suspend fun reverseGeocode(latitude: Double, longitude: Double): PlaceName? =
    suspendCancellableCoroutine { continuation ->
        val geocoder = CLGeocoder()
        geocoder.reverseGeocodeLocation(CLLocation(latitude, longitude)) { placemarks, _ ->
            val placemark = placemarks?.firstOrNull() as? CLPlacemark
            continuation.resume(placemark?.toPlaceName())
        }
        continuation.invokeOnCancellation { geocoder.cancelGeocode() }
    }

private fun CLPlacemark.toPlaceName(): PlaceName? {
    // same walk outwards as android: a spot outside any town still gets named
    val city = locality
        ?: subAdministrativeArea
        ?: administrativeArea
        ?: country
        ?: return null

    val street = listOfNotNull(thoroughfare, subThoroughfare)
        .takeIf { it.isNotEmpty() }
        ?.joinToString(" ")

    return PlaceName(
        city = city,
        address = listOfNotNull(street, city).joinToString(", "),
    )
}
