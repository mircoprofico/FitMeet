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
    // coordinates of the spot, null when the row has none.
    // used to sort by distance from the phone.
    val latitude: Double? = null,
    val longitude: Double? = null,
    val level: Level,
    val participants: Int,
    val capacity: Int,
) {
    // handy for the card: hides the "3/10" formatting and the full check
    val attendance: String get() = "$participants/$capacity"
    val isFull: Boolean get() = participants >= capacity
}
