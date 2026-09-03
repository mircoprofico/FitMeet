package ch.heigvd.fitmeet.model

import ch.heigvd.fitmeet.ui.theme.Level
import ch.heigvd.fitmeet.ui.theme.Sport

// one activity as the ui needs it. no @Serializable here, the supabase
// dto and its @SerialName mapping come later with the repository.
// dateTime and place are already formatted strings for now.
data class Activity(
    val id: String,
    val title: String,
    val description: String = "",
    val sport: Sport,
    // iso 8601, sortable as plain text: year then month then day.
    // this is what starts_at holds in the events table.
    val startsAt: String,
    // already formatted for the card, "Aujourd'hui - 14h30"
    val dateTime: String,
    val place: String,
    /** A Google Maps search URL derived from the event's geographic point. */
    val mapUrl: String? = null,
    val level: Level,
    val participants: Int,
    val capacity: Int,
) {
    // handy for the card: hides the "3/10" formatting and the full check
    val attendance: String get() = "$participants/$capacity"
    val isFull: Boolean get() = participants >= capacity

    companion object {
        fun fromEvent(
            id: String,
            title: String,
            sportSlug: String,
            startsAt: String,
            locationName: String,
            location: String? = null,
            description: String? = null,
            levelSlug: String,
            participants: Int,
            capacity: Int,
        ) = Activity(
            id = id,
            title = title,
            description = description.orEmpty(),
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
            level = when (levelSlug) {
                "beginner" -> Level.BEGINNER
                "intermediate" -> Level.INTERMEDIATE
                "advanced" -> Level.ADVANCED
                else -> Level.ALL
            },
            participants = participants,
            capacity = capacity,
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
