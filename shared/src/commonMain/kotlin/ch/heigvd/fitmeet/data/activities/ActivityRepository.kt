package ch.heigvd.fitmeet.data.activities

import ch.heigvd.fitmeet.model.Activity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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
    @SerialName("is_joined") val isJoined: Boolean = false,
    @SerialName("is_organizer") val isOrganizer: Boolean = false,
)

@Serializable
private data class EventParticipantInsert(
    @SerialName("event_id") val eventId: String,
    @SerialName("user_id") val userId: String,
)

interface ActivityRepository {
    suspend fun nearbyActivities(): Result<List<Activity>>

    // the capacity check lives in the join_event function, inside the same
    // transaction as the insert. two people taking the last spot at the same
    // time cannot both get in, which a check in the ui could never guarantee.
    suspend fun join(activityId: String): Result<Unit>

    suspend fun leave(activityId: String): Result<Unit>
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

    override suspend fun join(activityId: String): Result<Unit> = runCatching {
        supabase.postgrest.rpc("join_event", buildJsonObject {
            put("p_event_id", activityId)
        })
        Unit
    }

    override suspend fun leave(activityId: String): Result<Unit> = runCatching {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: error("Votre session a expiré. Reconnectez-vous.")
        // plain delete: the rls policy already refuses to let an organizer
        // leave their own event, no function needed
        supabase.from("event_participants").delete {
            filter {
                eq("event_id", activityId)
                eq("user_id", userId)
            }
        }
        Unit
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
    isJoined = isJoined,
    isOrganizer = isOrganizer,
    capacity = capacity,
)

object PreviewActivityRepository : ActivityRepository {
    override suspend fun nearbyActivities(): Result<List<Activity>> =
        Result.success(sampleActivities)

    override suspend fun join(activityId: String) = Result.success(Unit)

    override suspend fun leave(activityId: String) = Result.success(Unit)
}
