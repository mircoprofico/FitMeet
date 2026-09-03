package ch.heigvd.fitmeet.ui.map

import androidx.compose.runtime.Composable

/**
 * A point turned back into something a person can read.
 *
 * [city] is what the card shows, [address] the fuller line the detail sheet
 * shows underneath. Both come from the phone's own geocoder, so there is no
 * service to sign up for and no key to keep.
 */
data class PlaceName(
    val city: String,
    val address: String,
)

/**
 * Resolves a point in the background and returns null until it has an answer,
 * or forever when the device cannot resolve it: no network, no geocoder on
 * this build, or a spot in the middle of a lake. Callers fall back on what
 * they already have rather than showing an empty line.
 *
 * Composable and not a plain suspend function because Android's geocoder
 * needs a Context, which is only reachable from the composition here.
 */
@Composable
expect fun rememberPlaceName(latitude: Double?, longitude: Double?): PlaceName?

// what the creation screen used to store as the place of every activity.
// rows written before the geocoder landed still carry it, so the screens
// treat it as "no name" and fall back on the resolved one.
const val UNNAMED_PLACE = "Position choisie"

fun String.isNamedPlace(): Boolean = isNotBlank() && this != UNNAMED_PLACE
