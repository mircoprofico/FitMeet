package ch.heigvd.fitmeet

import ch.heigvd.fitmeet.data.profile.birthdateToIso
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class OnboardingStateTest {
    @Test
    fun birthdateIsConvertedToIso() {
        assertEquals("1994-02-28", birthdateToIso("28/02/1994"))
        assertEquals("2000-02-29", birthdateToIso("29/02/2000"))
    }

    @Test
    fun invalidBirthdateIsRejected() {
        assertNull(birthdateToIso("31/02/1994"))
        assertNull(birthdateToIso("1/2/1994"))
    }
}
