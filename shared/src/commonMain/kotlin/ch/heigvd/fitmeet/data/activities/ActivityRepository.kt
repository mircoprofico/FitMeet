package ch.heigvd.fitmeet.data.activities

import ch.heigvd.fitmeet.model.Activity
import ch.heigvd.fitmeet.ui.theme.Level
import ch.heigvd.fitmeet.ui.theme.Sport
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// what one row of the events table looks like. field names match the
// columns, @SerialName maps snake_case to camelCase.
// private: nobody outside this file needs to know the table shape.
@Serializable
private data class EventRow(
    val id: String,
    val title: String,
    @SerialName("sport_slug") val sportSlug: String,
    @SerialName("starts_at") val startsAt: String,
    @SerialName("location_name") val locationName: String,
    val level: String,
    val capacity: Int,
)

// who can read activities. the screen depends on this interface, not on
// supabase, so previews can be given a fake one instead.
interface ActivityRepository {
    // suspend: it waits for the network without freezing the ui.
    // Result holds either the list or the error, no exception thrown.
    suspend fun nearbyActivities(): Result<List<Activity>>
}

class SupabaseActivityRepository(
    private val supabase: SupabaseClient,
) : ActivityRepository {

    override suspend fun nearbyActivities(): Result<List<Activity>> = runCatching {
        supabase.from("events")
            .select()
            .decodeList<EventRow>()
            .map { it.toActivity() }
    }
}

// the slugs are the ones SupabaseEventRepository writes when creating an
// event. unknown values fall back instead of crashing the whole list.
private fun sportOf(slug: String) = when (slug) {
    "football" -> Sport.FOOTBALL
    "basketball" -> Sport.BASKETBALL
    "volleyball" -> Sport.VOLLEYBALL
    "tennis" -> Sport.TENNIS
    "badminton" -> Sport.BADMINTON
    "running" -> Sport.RUNNING
    "cycling" -> Sport.CYCLING
    "hiking" -> Sport.HIKING
    else -> Sport.OTHER
}

private fun levelOf(slug: String) = when (slug) {
    "intermediate" -> Level.INTERMEDIATE
    "advanced" -> Level.ADVANCED
    else -> Level.ALL
}

// "2026-09-02T14:30:00+00:00" -> "02.09 - 14h30".
// crude on purpose: proper relative dates ("Aujourd'hui") need a clock
// and a timezone, which is more than the list needs right now.
private fun displayDate(startsAt: String): String {
    val date = startsAt.substringBefore('T')
    val time = startsAt.substringAfter('T').take(5)
    val parts = date.split('-')
    val day = parts.getOrNull(2) ?: return startsAt
    val month = parts.getOrNull(1) ?: return startsAt
    return "$day.$month - ${time.replace(':', 'h')}"
}

private fun EventRow.toActivity() = Activity(
    id = id,
    title = title,
    sport = sportOf(sportSlug),
    startsAt = startsAt,
    dateTime = displayDate(startsAt),
    place = locationName,
    level = levelOf(level),
    // TODO: real count from event_participants, needs a join or a view
    participants = 0,
    capacity = capacity,
)

// used by previews and when supabase is not configured: no network at all.
object PreviewActivityRepository : ActivityRepository {
    override suspend fun nearbyActivities(): Result<List<Activity>> =
        Result.success(sampleActivities)
}
