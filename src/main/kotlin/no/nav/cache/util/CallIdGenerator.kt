package no.nav.cache.util

import org.springframework.stereotype.Component
import java.util.*

@Component
class CallIdGenerator {
    fun create(): String {
        return UUID.randomUUID().toString()
    }
}
