package ch.heigvd.fitmeet

import ch.heigvd.fitmeet.data.activities.sampleActivities
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
