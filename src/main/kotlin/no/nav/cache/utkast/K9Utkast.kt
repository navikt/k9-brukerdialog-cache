package no.nav.cache.utkast

import no.nav.cache.cache.Ytelse
import no.nav.cache.kafka.Metadata
import no.nav.cache.util.MDCUtil

data class K9Utkast(
    val metadata: Metadata,
    val ytelse: Ytelse,
    val utkast: MutableMap<String, Any>
)


fun Utkast.byggK9Utkast(ytelse: Ytelse) = K9Utkast(
    metadata = Metadata(version = 1, correlationId = MDCUtil.callIdOrNew()),
    ytelse = ytelse,
    utkast = serializeToJson()
)
