package ch.heigvd.fitmeet.model

import ch.heigvd.fitmeet.ui.theme.Level
import ch.heigvd.fitmeet.ui.theme.Sport

// one activity as the ui needs it. no @Serializable here, the supabase
// dto and its @SerialName mapping come later with the repository.
// dateTime and place are already formatted strings for now.
data class Activity(
    val id: String,
    val title: String,
    val sport: Sport,
    // iso 8601, sortable as plain text: year then month then day.
    // this is what starts_at holds in the events table.
    val startsAt: String,
    // already formatted for the card, "Aujourd'hui - 14h30"
    val dateTime: String,
    val place: String,
    /** A Google Maps search URL derived from the event's geographic point. */
    val mapUrl: String? = null,
    // same point, kept as numbers so the list can sort by distance
    val latitude: Double? = null,
    val longitude: Double? = null,
    val level: Level,
    val participants: Int,
    val capacity: Int,
    // whether the signed in user attends, and whether they organise it.
    // an organizer attends by definition and cannot leave.
    val isJoined: Boolean = false,
    val isOrganizer: Boolean = false,
) {
    // handy for the card: hides the "3/10" formatting and the full check
    val attendance: String get() = "$participants/$capacity"
    val isFull: Boolean get() = participants >= capacity

    // an organizer is stuck with their own event, everyone else can leave
    val canLeave: Boolean get() = isJoined && !isOrganizer

    companion object {
        fun fromEvent(
            id: String,
            title: String,
            sportSlug: String,
            startsAt: String,
            locationName: String,
            location: String? = null,
            levelSlug: String,
            participants: Int,
            capacity: Int,
            isJoined: Boolean = false,
            isOrganizer: Boolean = false,
        ) = Activity(
            id = id,
            title = title,
            sport = when (sportSlug) {
                "football" -> Sport.FOOTBALL
                "basketball" -> Sport.BASKETBALL
                "volleyball" -> Sport.VOLLEYBALL
                "tennis" -> Sport.TENNIS
                "badminton" -> Sport.BADMINTON
                "running" -> Sport.RUNNING
                "cycling" -> Sport.CYCLING
                "hiking" -> Sport.HIKING
                else -> Sport.OTHER
            },
            startsAt = startsAt,
            dateTime = displayDate(startsAt),
            place = locationName,
            mapUrl = googleMapsUrl(location),
            latitude = pointOf(location)?.first,
            longitude = pointOf(location)?.second,
            level = when (levelSlug) {
                "beginner" -> Level.BEGINNER
                "intermediate" -> Level.INTERMEDIATE
                "advanced" -> Level.ADVANCED
                else -> Level.ALL
            },
            participants = participants,
            capacity = capacity,
            isJoined = isJoined || isOrganizer,
            isOrganizer = isOrganizer,
        )

        private fun displayDate(startsAt: String): String {
            val date = startsAt.substringBefore('T')
            val time = startsAt.substringAfter('T').take(5)
            val parts = date.split('-')
            val day = parts.getOrNull(2) ?: return startsAt
            val month = parts.getOrNull(1) ?: return startsAt
            return "$day.$month - ${time.replace(':', 'h')}"
        }

        // PostGIS points use longitude first: POINT(longitude latitude).
        private fun pointOf(location: String?): Pair<Double, Double>? {
            val g = location?.trim()?.let { POINT_PATTERN.matchEntire(it) }?.groupValues
                ?: return null
            val lat = g[2].toDoubleOrNull() ?: return null
            val lng = g[1].toDoubleOrNull() ?: return null
            return lat to lng
        }

        private fun googleMapsUrl(location: String?): String? {
            val coordinates = location
                ?.trim()
                ?.let { POINT_PATTERN.matchEntire(it) }
                ?.groupValues
                ?: return null
            val longitude = coordinates[1]
            val latitude = coordinates[2]
            return "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
        }

        private val POINT_PATTERN = Regex(
            """POINT\s*\(\s*(-?\d+(?:\.\d+)?)\s+(-?\d+(?:\.\d+)?)\s*\)""",
            RegexOption.IGNORE_CASE,
        )
    }
}
