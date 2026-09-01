package ch.heigvd.fitmeet.data.activityCreation

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface EventRepository {
    suspend fun createEvent(
        type: String,
        date: String,
        time: String,
        duration: Int,
        position: String,
        name: String,
        description: String,
        difficulty: String,
    ): Result<Unit>
}

@Serializable
private data class NewEvent(
    @SerialName("organizer_id") val organizerId: String,
    @SerialName("sport_slug") val sportSlug: String,
    val title: String,
    val description: String? = null,
    @SerialName("starts_at") val startsAt: String,
    @SerialName("ends_at") val endsAt: String,
    @SerialName("location_name") val locationName: String,
    val location: String,
    val level: String,
    val capacity: Int,
    @SerialName("price_chf") val priceChf: Double = 0.0,
)

class SupabaseEventRepository(
    private val supabase: SupabaseClient,
) : EventRepository {
    override suspend fun createEvent(
        type: String,
        date: String,
        time: String,
        duration: Int,
        position: String,
        name: String,
        description: String,
        difficulty: String,
    ): Result<Unit> = runCatching {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: error("Votre session a expiré. Reconnectez-vous.")
        require(name.isNotBlank()) { "Le nom de l'activité est obligatoire." }
        require(type.isNotBlank() && type != "None") {
            "Le type de l'activité est obligatoire."
        }
        require(position.startsWith("POINT(")) { "La position de l'activité est invalide." }

        val startsAt = parseDateTime(date, time)
        val endsAt = startsAt.plus(duration.coerceAtLeast(0).toLong(), DateTimeUnit.MINUTE)

        supabase.from("events").insert(
            NewEvent(
                organizerId = userId,
                sportSlug = sportSlug(type),
                title = name.trim(),
                description = description.trim().ifEmpty { null },
                startsAt = startsAt.toString(),
                endsAt = endsAt.toString(),
                locationName = "Position choisie",
                location = position,
                level = levelSlug(difficulty),
                capacity = 12,
            ),
        )
    }

    private fun parseDateTime(date: String, time: String): kotlinx.datetime.Instant {
        val dateParts = date.split("/")
        val timeParts = time.split(":")
        require(dateParts.size == 3 && timeParts.size == 2) {
            "La date ou l'heure de l'activité est invalide."
        }

        return LocalDateTime(
            LocalDate(dateParts[2].toInt(), dateParts[1].toInt(), dateParts[0].toInt()),
            LocalTime(timeParts[0].toInt(), timeParts[1].toInt()),
        ).toInstant(TimeZone.currentSystemDefault())
    }

    private fun sportSlug(type: String) = when (type) {
        "Football" -> "football"
        "Basketball" -> "basketball"
        "Volleyball" -> "volleyball"
        "Tennis" -> "tennis"
        "Badminton" -> "badminton"
        "Course" -> "running"
        "Vélo" -> "cycling"
        "Randonnée" -> "hiking"
        "Autre" -> "other"
        else -> error("Sport inconnu.")
    }

    private fun levelSlug(difficulty: String) = when (difficulty) {
        "Débutant" -> "beginner"
        "Intermédiaire" -> "intermediate"
        "Confirmé" -> "advanced"
        else -> "all_levels"
    }
}

object PreviewEventRepository : EventRepository {
    override suspend fun createEvent(
        type: String,
        date: String,
        time: String,
        duration: Int,
        position: String,
        name: String,
        description: String,
        difficulty: String,
    ) = Result.success(Unit)
}

object UnconfiguredEventRepository : EventRepository {
    override suspend fun createEvent(
        type: String,
        date: String,
        time: String,
        duration: Int,
        position: String,
        name: String,
        description: String,
        difficulty: String,
    ) = Result.failure<Unit>(IllegalStateException("Supabase n'est pas configuré."))
}
