# CRUD Supabase en Kotlin

Le client est déjà configuré dans `shared/.../data/supabase/FitMeetSupabaseClient.kt`. Les appels à la base vont dans un repository (`suspend`), jamais directement dans un composable.

```kotlin
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
```

Les classes envoyées ou lues doivent être `@Serializable`. Les noms de colonnes Supabase sont en `snake_case` : utiliser `@SerialName` quand une propriété Kotlin est en `camelCase`.

```kotlin
@Serializable
data class ProfileDto(
    val id: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("account_type") val accountType: String,
    val bio: String? = null,
    val city: String? = null,
    @SerialName("avatar_path") val avatarPath: String? = null,
)

@Serializable
data class SportDto(
    val slug: String,
    val name: String,
    val color: String,
)
```

## Lire

```kotlin
val sports = supabase
    .from("sports")
    .select()
    .decodeList<SportDto>()
```

Lire le profil d'un utilisateur :

```kotlin
val profile = supabase
    .from("profiles")
    .select {
        filter { eq("id", userId) }
    }
    .decodeSingle<ProfileDto>()
```

Lister les activités rejointes avec leur conversation :

```kotlin
@Serializable
data class ActivityConversationDto(
    @SerialName("conversation_id") val conversationId: String,
    @SerialName("event_id") val eventId: String,
    val title: String,
    @SerialName("starts_at") val startsAt: String,
    @SerialName("is_organizer") val isOrganizer: Boolean,
    @SerialName("last_message_at") val lastMessageAt: String? = null,
)

val conversations = supabase
    .postgrest
    .rpc("my_event_conversations")
    .decodeList<ActivityConversationDto>()
```

## Créer une activité

Le format ci-dessous correspond à `public.events`. `organizerId` doit être l'ID de l'utilisateur actuellement connecté : la règle RLS refuse la création si ce n'est pas le cas. Les dates sont ISO-8601 en UTC et `location` est une géographie PostGIS au format WKT (`longitude latitude`).

```kotlin
@Serializable
data class NewEventDto(
    @SerialName("organizer_id") val organizerId: String,
    @SerialName("sport_slug") val sportSlug: String,
    val title: String,
    val description: String? = null,
    @SerialName("starts_at") val startsAt: String,
    @SerialName("ends_at") val endsAt: String,
    @SerialName("location_name") val locationName: String,
    val location: String,
    val level: String = "all_levels",
    val capacity: Int,
    @SerialName("price_chf") val priceChf: Double = 0.0,
)

val newEvent = NewEventDto(
    organizerId = userId,
    sportSlug = "running", // doit exister dans sports.slug
    title = "Course au bord du lac",
    description = "Rythme tranquille, environ 8 km.",
    startsAt = "2026-09-15T18:30:00Z",
    endsAt = "2026-09-15T19:30:00Z",
    locationName = "Musée Olympique, Ouchy",
    location = "POINT(6.631 46.5071)",
    level = "beginner", // beginner, intermediate, advanced ou all_levels
    capacity = 12,
    priceChf = 0.0,
)

supabase.from("events").insert(newEvent)
```

Une conversation est créée automatiquement pour chaque nouvelle activité : ne pas insérer directement dans `event_conversations`.

## Mettre à jour le profil connecté

`profiles` est créé à l'inscription. Un utilisateur ne peut modifier que sa propre ligne.

```kotlin
@Serializable
data class ProfileUpdateDto(
    @SerialName("display_name") val displayName: String,
    val bio: String? = null,
    val city: String? = null,
    @SerialName("avatar_path") val avatarPath: String? = null,
)

val update = ProfileUpdateDto(
    displayName = "Alice Martin",
    bio = "Course à pied et escalade.",
    city = "Lausanne",
)

supabase.from("profiles").update(update) {
    filter { eq("id", userId) }
}
```

## Mettre à jour ou supprimer une activité

Seul l'organisateur peut modifier ou supprimer son activité.

```kotlin
supabase.from("events").update({
    set("title", "Course au bord du lac - 8 km")
    set("capacity", 16)
}) {
    filter { eq("id", eventId) }
}

supabase.from("events").delete {
    filter { eq("id", eventId) }
}
```
