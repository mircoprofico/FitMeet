package ch.heigvd.fitmeet.ui.map

import android.location.Address
import android.location.Geocoder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
actual fun rememberPlaceName(latitude: Double?, longitude: Double?): PlaceName? {
    val context = LocalContext.current
    var place by remember(latitude, longitude) { mutableStateOf<PlaceName?>(null) }

    LaunchedEffect(latitude, longitude) {
        if (latitude == null || longitude == null) return@LaunchedEffect
        if (!Geocoder.isPresent()) return@LaunchedEffect
        place = withContext(Dispatchers.IO) {
            runCatching {
                // the blocking overload: deprecated on api 33 but still the
                // only one that works below it, and we are off the main
                // thread here so it blocks nothing the user can see
                @Suppress("DEPRECATION")
                Geocoder(context, Locale.getDefault())
                    .getFromLocation(latitude, longitude, 1)
                    ?.firstOrNull()
                    ?.toPlaceName()
            }.getOrNull() // offline, or no service behind the geocoder
        }
    }

    return place
}

private fun Address.toPlaceName(): PlaceName? {
    // locality is the city. a spot outside any town has none, so we walk
    // outwards until something names it.
    val city = locality
        ?: subAdminArea
        ?: adminArea
        ?: countryName
        ?: return null

    // the street line, when there is one, plus the city. not the full
    // postal address: the card has one line and a postcode helps nobody.
    val street = listOfNotNull(thoroughfare, subThoroughfare)
        .takeIf { it.isNotEmpty() }
        ?.joinToString(" ")

    return PlaceName(
        city = city,
        address = listOfNotNull(street, city).joinToString(", "),
    )
}
