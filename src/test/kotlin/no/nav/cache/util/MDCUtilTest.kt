package no.nav.cache.util

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test

class MDCUtilTest {
    @Test
    fun `callIdOrNew skal ikke kaate feil hvis det ikke eksisterer callId fra før`() {
        assertDoesNotThrow { MDCUtil.callIdOrNew() }
    }
}
