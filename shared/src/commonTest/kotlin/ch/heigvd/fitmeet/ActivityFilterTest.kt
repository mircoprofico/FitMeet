package ch.heigvd.fitmeet

import ch.heigvd.fitmeet.data.activities.sampleActivities
import ch.heigvd.fitmeet.model.Activity
import ch.heigvd.fitmeet.model.distanceFrom
import ch.heigvd.fitmeet.model.distanceKm
import ch.heigvd.fitmeet.ui.theme.Sport
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// same logic as the list screen, kept in one place so the test and the
// screen cannot drift apart by accident
private fun filter(selected: Set<Sport>) =
    if (selected.isEmpty()) sampleActivities
    else sampleActivities.filter { it.sport in selected }

class ActivityFilterTest {

    @Test
    fun noFilterKeepsEverything() {
        assertEquals(sampleActivities.size, filter(emptySet()).size)
    }

    @Test
    fun oneSportKeepsOnlyThatSport() {
        val result = filter(setOf(Sport.FOOTBALL))
        assertEquals(1, result.size)
        assertTrue(result.all { it.sport == Sport.FOOTBALL })
    }

    @Test
    fun twoSportsKeepBoth() {
        val result = filter(setOf(Sport.FOOTBALL, Sport.TENNIS))
        assertEquals(2, result.size)
        assertTrue(result.all { it.sport == Sport.FOOTBALL || it.sport == Sport.TENNIS })
    }

    @Test
    fun aSportWithNoActivityGivesAnEmptyList() {
        assertTrue(filter(setOf(Sport.VOLLEYBALL)).isEmpty())
    }

    @Test
    fun sortingIsChronological() {
        val dates = sampleActivities.sortedBy { it.startsAt }.map { it.startsAt }
        assertEquals(dates.sorted(), dates)
    }
}

class DistanceTest {

    private val lausanne = 46.5197 to 6.6323
    private val morges = 46.5094 to 6.4980   // about 10 km away
    private val geneve = 46.2044 to 6.1432   // about 50 km away

    @Test
    fun sameSpotIsZero() {
        val d = distanceKm(lausanne.first, lausanne.second, lausanne.first, lausanne.second)
        assertTrue(d < 0.001)
    }

    @Test
    fun morgesIsAroundTenKilometres() {
        val d = distanceKm(lausanne.first, lausanne.second, morges.first, morges.second)
        assertTrue(d in 8.0..13.0, "expected around 10 km, got $d")
    }

    @Test
    fun closerActivityComesFirst() {
        val far = sampleActivities.first().copy(
            id = "far", latitude = geneve.first, longitude = geneve.second,
        )
        val near = sampleActivities.first().copy(
            id = "near", latitude = morges.first, longitude = morges.second,
        )
        val sorted = listOf(far, near).sortedWith(
            compareBy<Activity, Double?>(nullsLast()) {
                it.distanceFrom(lausanne.first, lausanne.second)
            }.thenBy { it.startsAt },
        )
        assertEquals("near", sorted.first().id)
    }

    @Test
    fun activityWithoutCoordinatesGoesLast() {
        val withCoords = sampleActivities.first().copy(
            id = "here", latitude = morges.first, longitude = morges.second,
        )
        val without = sampleActivities.first().copy(id = "nowhere")
        val sorted = listOf(without, withCoords).sortedWith(
            compareBy<Activity, Double?>(nullsLast()) {
                it.distanceFrom(lausanne.first, lausanne.second)
            }.thenBy { it.startsAt },
        )
        assertEquals("nowhere", sorted.last().id)
    }
}
