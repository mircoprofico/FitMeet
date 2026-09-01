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
    val dateTime: String,
    val place: String,
    val level: Level,
    val participants: Int,
    val capacity: Int,
) {
    // handy for the card: hides the "3/10" formatting and the full check
    val attendance: String get() = "$participants/$capacity"
    val isFull: Boolean get() = participants >= capacity
}
