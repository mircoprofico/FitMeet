package ch.heigvd.fitmeet.data.activities

import ch.heigvd.fitmeet.model.Activity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class EventRow(
    val id: String,
    val title: String,
    @SerialName("sport_slug") val sportSlug: String,
    @SerialName("starts_at") val startsAt: String,
    @SerialName("location_name") val locationName: String,
    val location: String? = null,
    val level: String,
    val capacity: Int,
    @SerialName("participant_count") val participantCount: Int,
)

@Serializable
private data class EventParticipantInsert(
    @SerialName("event_id") val eventId: String,
    @SerialName("user_id") val userId: String,
)

@Serializable
private data class ParticipantEventIdRow(@SerialName("event_id") val eventId: String)

interface ActivityRepository {
    suspend fun nearbyActivities(): Result<List<Activity>>
    suspend fun joinEvent(eventId: String): Result<Unit>
    suspend fun participatingEventIds(): Result<Set<String>>
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

    override suspend fun joinEvent(eventId: String): Result<Unit> = runCatching {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: error("Non authentifié")
        supabase.from("event_participants").insert(
            EventParticipantInsert(eventId = eventId, userId = userId),
        )
    }

    override suspend fun participatingEventIds(): Result<Set<String>> = runCatching {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: return@runCatching emptySet()
        supabase.from("event_participants")
            .select(columns = Columns.list("event_id")) {
                filter { eq("user_id", userId) }
            }
            .decodeList<ParticipantEventIdRow>()
            .map { it.eventId }
            .toSet()
    }
}

private fun EventRow.toActivity() = Activity.fromEvent(
    id = id,
    title = title,
    sportSlug = sportSlug,
    startsAt = startsAt,
    locationName = locationName,
    location = location,
    levelSlug = level,
    participants = participantCount,
    capacity = capacity,
)

object PreviewActivityRepository : ActivityRepository {
    override suspend fun nearbyActivities(): Result<List<Activity>> =
        Result.success(sampleActivities)
    override suspend fun joinEvent(eventId: String): Result<Unit> = Result.success(Unit)
    override suspend fun participatingEventIds(): Result<Set<String>> = Result.success(emptySet())
}
