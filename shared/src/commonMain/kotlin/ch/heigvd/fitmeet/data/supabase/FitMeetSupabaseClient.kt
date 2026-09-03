package ch.heigvd.fitmeet.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

fun createFitMeetSupabaseClient(supabaseUrl: String, publishableKey: String): SupabaseClient =
    createSupabaseClient(supabaseUrl = supabaseUrl, supabaseKey = publishableKey) {
        install(Auth) {
            scheme = "fitmeet"
            host = "auth"
        }
        install(Postgrest)
        install(Realtime)
    }
