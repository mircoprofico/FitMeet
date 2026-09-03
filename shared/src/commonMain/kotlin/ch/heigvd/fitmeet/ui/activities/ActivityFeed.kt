package ch.heigvd.fitmeet.ui.activities

import ch.heigvd.fitmeet.model.Activity
import ch.heigvd.fitmeet.model.distanceFrom
import ch.heigvd.fitmeet.ui.components.DateRange
import ch.heigvd.fitmeet.ui.theme.Sport

// everything the pills and the funnel can narrow the list with, in one
// object: adding a filter means adding a field here and nothing else.
data class ActivityFilters(
    val sports: Set<Sport> = emptySet(),
    val dateRange: DateRange = DateRange.ALL,
    val onlyWithSpots: Boolean = false,
) {
    // what the badge on the funnel counts: every active filter, not just sports
    val activeCount: Int
        get() = sports.size +
            (if (dateRange != DateRange.ALL) 1 else 0) +
            (if (onlyWithSpots) 1 else 0)
}

/**
 * The order and the filtering the list screen shows.
 *
 * A plain function and not code inside the composable: the screen calls it
 * and so does the test, so what is tested is what is displayed.
 *
 * [today] and [inAWeek] are iso days ("yyyy-mm-dd"); they are passed in
 * rather than read from the clock so the test stays deterministic.
 */
fun List<Activity>.feed(
    filters: ActivityFilters,
    today: String,
    inAWeek: String,
    myLat: Double? = null,
    myLng: Double? = null,
): List<Activity> = this
    .filter { filters.sports.isEmpty() || it.sport in filters.sports }
    .filter { !filters.onlyWithSpots || !it.isFull }
    .filter {
        // startsAt is iso, so comparing the first ten characters is enough:
        // same day means the same "yyyy-mm-dd" prefix, and iso days compare
        // as plain text without any parsing
        val day = it.startsAt.take(10)
        when (filters.dateRange) {
            DateRange.ALL -> true
            DateRange.TODAY -> day == today
            // a range, not just an upper bound: without the lower one a
            // past activity would still count as "this week"
            DateRange.THIS_WEEK -> day in today..inAWeek
        }
    }
    // distance first, then date, like the issue asks.
    // nullsLast matters: compareBy alone puts nulls FIRST, so an activity
    // without coordinates would look like the closest one.
    .sortedWith(
        compareBy<Activity, Double?>(nullsLast()) { it.distanceFrom(myLat, myLng) }
            .thenBy { it.startsAt },
    )
