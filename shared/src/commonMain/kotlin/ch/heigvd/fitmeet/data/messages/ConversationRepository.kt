package ch.heigvd.fitmeet.data.messages

import ch.heigvd.fitmeet.data.auth.AuthActionResult
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonArray

@Serializable
data class ConversationSummary(
    @SerialName("conversation_id") val conversationId: String,
    @SerialName("event_id") val eventId: String,
    val title: String,
    @SerialName("starts_at") val startsAt: String,
    @SerialName("ends_at") val endsAt: String,
    @SerialName("location_name") val locationName: String,
    @SerialName("is_organizer") val isOrganizer: Boolean,
    @SerialName("last_message_at") val lastMessageAt: String? = null,
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
    suspend fun getMessages(conversationId: String): Result<List<ConversationMessage>>
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
    override suspend fun getAccessibleConversations(): Result<List<ConversationSummary>> = runCatching {
        supabase.postgrest
            .rpc("my_event_conversations")
            .decodeList<ConversationSummary>()
    }

    override suspend fun getMessages(conversationId: String): Result<List<ConversationMessage>> = runCatching {
        val messages = supabase.from("conversation_messages").select(
            columns = Columns.list("id", "conversation_id", "sender_id", "content", "created_at"),
        ) {
            filter { eq("conversation_id", conversationId) }
        }.decodeList<ConversationMessage>().sortedBy(ConversationMessage::createdAt)

        val senderIds = messages.map(ConversationMessage::senderId).distinct()
        val namesById = if (senderIds.isEmpty()) {
            emptyMap()
        } else {
            supabase.postgrest
                .rpc("public_profile_names", buildJsonObject {
                    putJsonArray("p_ids") {
                        senderIds.forEach { add(JsonPrimitive(it)) }
                    }
                })
                .decodeList<PublicProfileName>()
                .associate { it.id to it.displayName }
        }

        messages.map { message ->
            message.copy(senderName = namesById[message.senderId])
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
}

object PreviewConversationRepository : ConversationRepository {
    private val conversations = listOf(
        ConversationSummary(
            conversationId = "preview-conversation",
            eventId = "preview-event",
            title = "Course au bord du lac",
            startsAt = "2026-09-15T18:30:00Z",
            endsAt = "2026-09-15T19:30:00Z",
            locationName = "Lausanne",
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

    override suspend fun getMessages(conversationId: String) = Result.success(
        messages.filter { it.conversationId == conversationId },
    )

    override suspend fun sendMessage(senderUserId: String, conversationId: String, content: String) =
        AuthActionResult(true, "Aperçu : message envoyé.")

    override fun currentUserId() = "preview-user"
}

object UnconfiguredConversationRepository : ConversationRepository {
    private const val message = "Supabase n'est pas configuré. Ajoutez les clés dans local.properties."

    override suspend fun getAccessibleConversations() = Result.failure<List<ConversationSummary>>(
        IllegalStateException(message),
    )

    override suspend fun getMessages(conversationId: String) = Result.failure<List<ConversationMessage>>(
        IllegalStateException(message),
    )

    override suspend fun sendMessage(senderUserId: String, conversationId: String, content: String) =
        AuthActionResult(false, message)

    override fun currentUserId() = null
}
