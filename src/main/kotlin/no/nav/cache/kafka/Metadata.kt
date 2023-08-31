package no.nav.cache.kafka

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Metadata @JsonCreator constructor(
    @JsonProperty("version") val version : Int,
    @JsonProperty("correlationId") val correlationId : String
)
