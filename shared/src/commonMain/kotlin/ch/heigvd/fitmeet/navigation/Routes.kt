package ch.heigvd.fitmeet.navigation

import kotlinx.serialization.Serializable


// AuthGraph - Login, Register, Onboarding (before sign in)
@Serializable object AuthGraph
// MainGraph - List, Map, Create, Messages, Profile, Detail... (after sign in)
@Serializable object MainGraph


// --- Login, Register and Onboarding (François) ---
@Serializable object Login
@Serializable object Register
@Serializable object Onboarding
@Serializable object OnboardingSports

// --- Bottom bar tabs ---
@Serializable object ActivityList      // Mirco
@Serializable object MapTab            // Antoine  (MapTab and not Map, it would shadow kotlin.collections.Map)
@Serializable object CreateActivity    // Pierre
@Serializable object Messages          // Pierre
@Serializable object Profile           // Antoine

// --- Pages opened from a tab ---
@Serializable data class ActivityDetail(val activityId: String)   // Mirco
@Serializable data class Conversation(val conversationId: String, val conversationTitle: String) // Pierre

// --- Profile editing ---
@Serializable object EditProfile
