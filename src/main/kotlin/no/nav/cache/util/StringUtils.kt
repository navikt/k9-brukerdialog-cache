package no.nav.cache.util

import java.util.*

fun String.storForbokstav() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

fun String.asAuthoriationHeader() = "Bearer $this"
