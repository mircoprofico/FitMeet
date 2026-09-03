package ch.heigvd.fitmeet.model

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private const val EARTH_RADIUS_KM = 6371.0

// haversine: distance between two points on a sphere. good enough at the
// scale of a canton, and it needs no library.
fun distanceKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val dLat = (lat2 - lat1).toRadians()
    val dLng = (lng2 - lng1).toRadians()
    val a = sin(dLat / 2).pow(2) +
        cos(lat1.toRadians()) * cos(lat2.toRadians()) * sin(dLng / 2).pow(2)
    return 2 * EARTH_RADIUS_KM * atan2(sqrt(a), sqrt(1 - a))
}

private fun Double.toRadians() = this * kotlin.math.PI / 180.0

// distance from the phone, or null when either side has no coordinates.
// sorting puts the nulls last so an activity without a spot never
// pretends to be the closest one.
fun Activity.distanceFrom(lat: Double?, lng: Double?): Double? {
    if (lat == null || lng == null || latitude == null || longitude == null) return null
    return distanceKm(lat, lng, latitude, longitude)
}
