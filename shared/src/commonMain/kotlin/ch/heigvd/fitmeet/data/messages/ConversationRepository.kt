package ch.heigvd.fitmeet.data.messages

import ch.heigvd.fitmeet.data.auth.AuthActionResult
import ch.heigvd.fitmeet.model.Activity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonArray
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

data class ConversationSummary(
    val conversationId: String,
    val eventId: String,
    val activity: Activity,
    val isOrganizer: Boolean,
    val lastMessageAt: String? = null,
)

@Serializable
private data class ConversationRow(
    @SerialName("conversation_id") val conversationId: String,
    @SerialName("event_id") val eventId: String,
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
    @SerialName("is_organizer") val isOrganizer: Boolean,
    @SerialName("last_message_at") val lastMessageAt: String? = null,
)

private fun ConversationRow.toSummary() = ConversationSummary(
    conversationId = conversationId,
    eventId = eventId,
    activity = Activity.fromEvent(
        id = eventId,
        title = title,
        sportSlug = sportSlug,
        startsAt = startsAt,
        locationName = locationName,
        location = location,
        description = description,
        levelSlug = level,
        participants = participantCount,
        capacity = capacity,
        // without these the sheet opened from a conversation could only ever
        // offer "Rejoindre", to people who are already in the event
        isJoined = isJoined,
        isOrganizer = isOrganizer,
    ),
    isOrganizer = isOrganizer,
    lastMessageAt = lastMessageAt,
)

@Serializable
data class ConversationMessage(
    val id: String,
    @SerialName("conversation_id") val conversationId: String,
    @SerialName("sender_id") val senderId: String,
    val senderName: String? = null,
    val content: String,
    @SerialName("created_at") val createdAt: String,
)

@Serializable
private data class PublicProfileName(
    val id: String,
    @SerialName("display_name") val displayName: String,
)

@Serializable
private data class NewConversationMessage(
    @SerialName("conversation_id") val conversationId: String,
    @SerialName("sender_id") val senderId: String,
    val content: String,
)

interface ConversationRepository {
    suspend fun getAccessibleConversations(): Result<List<ConversationSummary>>
    suspend fun getConversationSummary(conversationId: String): Result<ConversationSummary>
    suspend fun getMessages(conversationId: String): Result<List<ConversationMessage>>
    fun observeMessages(conversationId: String): Flow<ConversationMessage>
    suspend fun sendMessage(
        senderUserId: String,
        conversationId: String,
        content: String,
    ): AuthActionResult

    fun currentUserId(): String?
}

class SupabaseConversationRepository internal constructor(
    private val supabase: SupabaseClient,
) : ConversationRepository {
    private val profileNamesCache = mutableMapOf<String, String>()

    override suspend fun getAccessibleConversations(): Result<List<ConversationSummary>> = runCatching {
        supabase.postgrest
            .rpc("my_event_conversations")
            .decodeList<ConversationRow>()
            .map(ConversationRow::toSummary)
    }

    override suspend fun getConversationSummary(conversationId: String): Result<ConversationSummary> =
        getAccessibleConversations().mapCatching { conversations ->
            conversations.firstOrNull { it.conversationId == conversationId }
                ?: error("Conversation introuvable.")
        }

    override suspend fun getMessages(conversationId: String): Result<List<ConversationMessage>> = runCatching {
        val messages = supabase.from("conversation_messages").select(
            columns = Columns.list("id", "conversation_id", "sender_id", "content", "created_at"),
        ) {
            filter { eq("conversation_id", conversationId) }
        }.decodeList<ConversationMessage>().sortedBy(ConversationMessage::createdAt)

        loadMissingProfileNames(messages.map(ConversationMessage::senderId))

        messages.map { message ->
            message.copy(senderName = profileNamesCache[message.senderId])
        }
    }

    override fun observeMessages(conversationId: String): Flow<ConversationMessage> = flow {
        val channel = supabase.channel("conversation-$conversationId")
        try {
            val changes = channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
                table = "conversation_messages"
                filter("conversation_id", FilterOperator.EQ, conversationId)
            }
            channel.subscribe()

            changes.collect { action ->
                val message = action.decodeRecord<ConversationMessage>()
                loadMissingProfileNames(listOf(message.senderId))
                emit(message.copy(senderName = profileNamesCache[message.senderId]))
            }
        } finally {
            supabase.realtime.removeChannel(channel)
        }
    }

    override suspend fun sendMessage(
        senderUserId: String,
        conversationId: String,
        content: String,
    ): AuthActionResult {
        val currentUserId = currentUserId()
            ?: return AuthActionResult(false, "Votre session a expiré. Reconnectez-vous.")
        val trimmedContent = content.trim()
        if (senderUserId != currentUserId) {
            return AuthActionResult(false, "L'expéditeur doit être l'utilisateur connecté.")
        }
        if (trimmedContent.isEmpty()) {
            return AuthActionResult(false, "Le message ne peut pas être vide.")
        }
        if (trimmedContent.length > 2000) {
            return AuthActionResult(false, "Le message ne peut pas dépasser 2000 caractères.")
        }

        return runCatching {
            supabase.from("conversation_messages").insert(
                NewConversationMessage(conversationId, senderUserId, trimmedContent),
            )
            AuthActionResult(true, "Message envoyé.")
        }.getOrElse { error ->
            AuthActionResult(false, error.message ?: "Impossible d'envoyer le message.")
        }
    }

    override fun currentUserId(): String? = supabase.auth.currentUserOrNull()?.id

    private suspend fun loadMissingProfileNames(senderIds: List<String>) {
        val missingSenderIds = senderIds.distinct().filterNot(profileNamesCache::containsKey)
        if (missingSenderIds.isEmpty()) return

        val fetchedNames = supabase.postgrest
            .rpc("public_profile_names", buildJsonObject {
                putJsonArray("p_ids") {
                    missingSenderIds.forEach { add(JsonPrimitive(it)) }
                }
            })
            .decodeList<PublicProfileName>()
            .associate { it.id to it.displayName }
        profileNamesCache.putAll(fetchedNames)
    }
}

object PreviewConversationRepository : ConversationRepository {
    private val conversations = listOf(
        ConversationSummary(
            conversationId = "preview-conversation",
            eventId = "preview-event",
            activity = Activity.fromEvent(
                id = "preview-event",
                title = "Course au bord du lac",
                sportSlug = "running",
                startsAt = "2026-09-15T18:30:00Z",
                locationName = "Lausanne",
                levelSlug = "all",
                participants = 3,
                capacity = 10,
            ),
            isOrganizer = false,
            lastMessageAt = null,
        ),
    )
    private val messages = listOf(
        ConversationMessage(
            id = "preview-message-1",
            conversationId = "preview-conversation",
            senderId = "preview-user",
            senderName = "Pierre",
            content = "Salut Pierre !",
            createdAt = "2026-09-15T17:30:00Z",
        ),
        ConversationMessage(
            id = "preview-message-2",
            conversationId = "preview-conversation",
            senderId = "preview-other-user",
            senderName = "John",
            content = "À tout à l'heure.",
            createdAt = "2026-09-15T17:31:00Z",
        ),
    )

    override suspend fun getAccessibleConversations() = Result.success(conversations)

    override suspend fun getConversationSummary(conversationId: String) = Result.success(
        conversations.first { it.conversationId == conversationId },
    )

    override suspend fun getMessages(conversationId: String) = Result.success(
        messages.filter { it.conversationId == conversationId },
    )

    override fun observeMessages(conversationId: String) = emptyFlow<ConversationMessage>()

    override suspend fun sendMessage(senderUserId: String, conversationId: String, content: String) =
        AuthActionResult(true, "Aperçu : message envoyé.")

    override fun currentUserId() = "preview-user"
}

object UnconfiguredConversationRepository : ConversationRepository {
    private const val message = "Supabase n'est pas configuré. Ajoutez les clés dans local.properties."

    override suspend fun getAccessibleConversations() = Result.failure<List<ConversationSummary>>(
        IllegalStateException(message),
    )

    override suspend fun getConversationSummary(conversationId: String) =
        Result.failure<ConversationSummary>(IllegalStateException(message))

    override suspend fun getMessages(conversationId: String) = Result.failure<List<ConversationMessage>>(
        IllegalStateException(message),
    )

    override fun observeMessages(conversationId: String) = emptyFlow<ConversationMessage>()

    override suspend fun sendMessage(senderUserId: String, conversationId: String, content: String) =
        AuthActionResult(false, message)

    override fun currentUserId() = null
}
