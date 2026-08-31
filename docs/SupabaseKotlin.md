# CRUD Supabase en Kotlin

Le client Supabase est déjà configuré dans `shared/.../data/supabase/FitMeetSupabaseClient.kt`. Créer les appels à la base dans un repository, pas directement dans un composable.

```kotlin
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

class SportRepository(private val supabase: SupabaseClient) {
    // appels Supabase ici
}
```

Chaque objet lu ou envoyé doit avoir `@Serializable`.

```kotlin
@Serializable
data class SportDto(
    val id: Int,
    val name: String,
    val icon: String? = null,
)
```

## Read

```kotlin
val sports = supabase
    .from("sports")
    .select()
    .decodeList<SportDto>()
```

Lister les activités rejointes avec leur conversation :

```kotlin
@Serializable
data class ActivityConversationDto(
    val conversation_id: String,
    val event_id: String,
    val title: String,
    val starts_at: String,
    val is_organizer: Boolean,
    val last_message_at: String? = null,
)

val conversations = supabase
    .postgrest
    .rpc("my_event_conversations")
    .decodeList<ActivityConversationDto>()
```

Lire une ligne avec un filtre :

```kotlin
val profile = supabase
    .from("profiles")
    .select {
        filter { eq("id", userId) }
    }
    .decodeSingle<ProfileDto>()
```

## Insert

```kotlin
supabase.from("events").insert(newEvent)
```

`newEvent` doit être une data class `@Serializable` dont les propriétés correspondent aux colonnes à créer.

## Update

```kotlin
supabase.from("profiles").update({
    set("display_name", "Alice")
}) {
    filter { eq("id", userId) }
}
```

## Delete

```kotlin
supabase.from("events").delete {
    filter { eq("id", eventId) }
}
```

Les règles RLS restent appliquées : même si le code compile, Supabase refuse une opération que l'utilisateur connecté n'a pas le droit de faire. Ne jamais utiliser de clé `service_role` dans l'app.
