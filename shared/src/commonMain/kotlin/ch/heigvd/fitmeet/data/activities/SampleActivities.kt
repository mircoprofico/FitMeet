package ch.heigvd.fitmeet.data.activities

import ch.heigvd.fitmeet.model.Activity
import ch.heigvd.fitmeet.ui.theme.Level
import ch.heigvd.fitmeet.ui.theme.Sport

// TEMPORARY. fake data so the list screen can be built before the
// tables exist. delete once the repository talks to supabase.
val sampleActivities = listOf(
    Activity(
        id = "1",
        title = "Match de Foot",
        sport = Sport.FOOTBALL,
        startsAt = "2026-09-01T14:30:00",
        dateTime = "Aujourd'hui - 14h30",
        place = "Morges, FC Forward",
        level = Level.ADVANCED,
        participants = 3,
        capacity = 10,
    ),
    Activity(
        id = "2",
        title = "Tennis 1 contre 1",
        sport = Sport.TENNIS,
        startsAt = "2026-09-02T13:30:00",
        dateTime = "Demain - 13h30",
        place = "Etoy, Terrain de Tennis",
        level = Level.INTERMEDIATE,
        participants = 1,
        capacity = 2,
    ),
    Activity(
        id = "3",
        title = "Tournoi de badminton en salle du mercredi soir",
        sport = Sport.BADMINTON,
        startsAt = "2026-09-03T19:00:00",
        dateTime = "Mercredi - 19h00",
        place = "Yverdon, Salle des Isles",
        level = Level.ALL,
        participants = 12,
        capacity = 12,
    ),
    Activity(
        id = "4",
        title = "Sortie vélo du lac",
        sport = Sport.CYCLING,
        startsAt = "2026-09-05T09:00:00",
        dateTime = "Samedi - 09h00",
        place = "Lausanne, Ouchy",
        level = Level.ALL,
        participants = 5,
        capacity = 12,
    ),
    Activity(
        id = "5",
        title = "Course matinale",
        sport = Sport.RUNNING,
        startsAt = "2026-09-06T07:30:00",
        dateTime = "Dimanche - 07h30",
        place = "Nyon, Bord du lac",
        level = Level.INTERMEDIATE,
        participants = 2,
        capacity = 8,
    ),
)
