package ch.heigvd.fitmeet.data.activities

import ch.heigvd.fitmeet.model.Activity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
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
    val location: String? = null,
    val description: String? = null,
    val level: String,
    val capacity: Int,
    @SerialName("participant_count") val participantCount: Int,
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
        supabase.postgrest
            .rpc("my_activities")
            .decodeList<EventRow>()
            .map { it.toActivity() }
    }
}

private fun EventRow.toActivity() = Activity.fromEvent(
    id = id,
    title = title,
    sportSlug = sportSlug,
    startsAt = startsAt,
    locationName = locationName,
    location = location,
    description = description,
    levelSlug = level,
    participants = participantCount,
    capacity = capacity,
)

// used by previews and when supabase is not configured: no network at all.
object PreviewActivityRepository : ActivityRepository {
    override suspend fun nearbyActivities(): Result<List<Activity>> =
        Result.success(sampleActivities)
}
