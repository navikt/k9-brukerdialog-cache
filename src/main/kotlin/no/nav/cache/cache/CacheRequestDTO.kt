package no.nav.cache.cache

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.ZonedDateTime

data class CacheRequestDTO(
    val nøkkelPrefiks: String,
    val verdi: String,
    val ytelse: Ytelse? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC") val utløpsdato: ZonedDateTime,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC") val opprettet: ZonedDateTime? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC") val endret: ZonedDateTime? = null
)

data class CacheResponseDTO(
    val nøkkel: String,
    val verdi: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC") val utløpsdato: ZonedDateTime,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC") val opprettet: ZonedDateTime? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC") val endret: ZonedDateTime? = null
)
