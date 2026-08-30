package ch.heigvd.fitmeet.navigation

import kotlinx.serialization.Serializable


// AuthGraph - Login, Register, Onboarding (avant connexion)
@Serializable object AuthGraph
// MainGraph - Liste, Carte, Créer, Messages, Profil, Détail… (après connexion)
@Serializable object MainGraph


// --- Login, Register and Onboarding (François) ---
@Serializable object Login
@Serializable object Register
@Serializable object Onboarding
@Serializable object OnboardingSports

// --- Bottom bar tabs ---
@Serializable object ActivityList      // Mirco
@Serializable object MapTab            // Antoine  (nomme MapTab : "Map" masquerait kotlin.collections.Map)
@Serializable object CreateActivity    // Pierre
@Serializable object Messages          // Pierre
@Serializable object Profile           // Antoine

// --- Pages opened from a tab ---
@Serializable data class ActivityDetail(val activityId: String)   // Mirco
@Serializable data class Conversation(val activityId: String)     // Pierre
