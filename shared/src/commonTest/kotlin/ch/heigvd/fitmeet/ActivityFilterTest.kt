package ch.heigvd.fitmeet

import ch.heigvd.fitmeet.data.activities.sampleActivities
import ch.heigvd.fitmeet.model.Activity
import ch.heigvd.fitmeet.model.distanceFrom
import ch.heigvd.fitmeet.model.distanceKm
import ch.heigvd.fitmeet.ui.activities.ActivityFilters
import ch.heigvd.fitmeet.ui.activities.feed
import ch.heigvd.fitmeet.ui.components.DateRange
import ch.heigvd.fitmeet.ui.theme.Sport
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// the days the sample activities live in, fixed so the tests do not depend
// on when they run
private const val TODAY = "2026-09-01"
private const val IN_A_WEEK = "2026-09-08"

// feed() is the function the list screen itself calls, so what is asserted
// here is what the screen displays: the two cannot drift apart any more
private fun filter(
    sports: Set<Sport> = emptySet(),
    dateRange: DateRange = DateRange.ALL,
    onlyWithSpots: Boolean = false,
) = sampleActivities.feed(
    filters = ActivityFilters(sports, dateRange, onlyWithSpots),
    today = TODAY,
    inAWeek = IN_A_WEEK,
)

class ActivityFilterTest {

    @Test
    fun noFilterKeepsEverything() {
        assertEquals(sampleActivities.size, filter().size)
    }

    @Test
    fun oneSportKeepsOnlyThatSport() {
        val result = filter(sports = setOf(Sport.FOOTBALL))
        assertEquals(1, result.size)
        assertTrue(result.all { it.sport == Sport.FOOTBALL })
    }

    @Test
    fun twoSportsKeepBoth() {
        val result = filter(sports = setOf(Sport.FOOTBALL, Sport.TENNIS))
        assertEquals(2, result.size)
        assertTrue(result.all { it.sport == Sport.FOOTBALL || it.sport == Sport.TENNIS })
    }

    @Test
    fun aSportWithNoActivityGivesAnEmptyList() {
        assertTrue(filter(sports = setOf(Sport.VOLLEYBALL)).isEmpty())
    }

    @Test
    fun todayKeepsOnlyTodaysActivities() {
        val result = filter(dateRange = DateRange.TODAY)
        assertTrue(result.isNotEmpty())
        assertTrue(result.all { it.startsAt.take(10) == TODAY })
    }

    @Test
    fun thisWeekDropsWhatIsAlreadyPast() {
        val yesterday = sampleActivities.first().copy(id = "past", startsAt = "2026-08-30T10:00:00")
        val result = (sampleActivities + yesterday).feed(
            filters = ActivityFilters(dateRange = DateRange.THIS_WEEK),
            today = TODAY,
            inAWeek = IN_A_WEEK,
        )
        assertTrue(result.none { it.id == "past" }, "a past activity is not part of this week")
    }

    @Test
    fun onlyWithSpotsHidesTheFullOnes() {
        val result = filter(onlyWithSpots = true)
        assertTrue(result.none { it.isFull })
        assertTrue(sampleActivities.any { it.isFull }, "the sample needs a full activity to test")
    }

    @Test
    fun withoutAPositionTheOrderIsChronological() {
        val dates = filter().map { it.startsAt }
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
        val sorted = listOf(far, near).feed(
            filters = ActivityFilters(),
            today = TODAY,
            inAWeek = IN_A_WEEK,
            myLat = lausanne.first,
            myLng = lausanne.second,
        )
        assertEquals("near", sorted.first().id)
    }

    @Test
    fun activityWithoutCoordinatesGoesLast() {
        val withCoords = sampleActivities.first().copy(
            id = "here", latitude = morges.first, longitude = morges.second,
        )
        val without = sampleActivities.first().copy(id = "nowhere")
        val sorted = listOf(without, withCoords).feed(
            filters = ActivityFilters(),
            today = TODAY,
            inAWeek = IN_A_WEEK,
            myLat = lausanne.first,
            myLng = lausanne.second,
        )
        assertEquals("nowhere", sorted.last().id)
    }

    @Test
    fun distanceIsNullWhenEitherSideHasNoCoordinates() {
        val activity: Activity = sampleActivities.first()
        assertEquals(null, activity.distanceFrom(lausanne.first, lausanne.second))
    }
}
