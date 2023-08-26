package no.nav.cache.utkast

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.cache.utkast.UtkastValidator.validateIdent
import no.nav.cache.utkast.UtkastValidator.validateLink
import no.nav.cache.utkast.UtkastValidator.validateUtkastId

data class Utkast private constructor(
    private var utkastId: String? = null,
    private var ident: String? = null,
    private var link: String? = null,
    private var eventName: EventName? = null,
    private var origin: String = Utkast::class.qualifiedName!!,
    private var defaultTittel: String? = null,
    private val tittelByLanguage: MutableMap<String, String> = mutableMapOf(),
    private var metrics: MutableMap<String, String> = mutableMapOf(),
) {

    fun serializeToJson(): String {
        val mapper = utkastMapper()

        val tittelObject = tittelByLanguage.toJsonObject(mapper)
        val metricsObject = metrics.toJsonObject(mapper)

        val fields: MutableMap<String, Any?> = mutableMapOf(
            "utkastId" to utkastId,
            "@event_name" to eventName?.name,
            "@origin" to origin,
            "ident" to ident,
            "tittel" to defaultTittel,
            "tittel_i18n" to tittelObject,
            "link" to link,
            "metrics" to metricsObject
        )

        return mapper.writeValueAsString(fields)
    }

    private fun utkastMapper() = ObjectMapper().registerKotlinModule().apply {
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
    }


    private fun Map<String, String>.toJsonObject(mapper: ObjectMapper) = mapper.writeValueAsString(this)


    class Builder {
        private var utkastId: String? = null
        private var ident: String? = null
        private var link: String? = null
        private var eventName: EventName? = null
        private var origin: String = Utkast::class.qualifiedName!!
        private var defaultTittel: String? = null
        private val tittelByLanguage = mutableMapOf<String, String>()
        private var metrics = mutableMapOf<String, String>()

        fun utkastId(utkastId: String) = apply { this.utkastId = validateUtkastId(utkastId) }
        fun ident(ident: String) = apply { this.ident = validateIdent(ident) }
        fun link(link: String) = apply { this.link = validateLink(link) }
        fun eventName(eventName: EventName) = apply { this.eventName = eventName }
        fun origin(origin: String) = apply { this.origin = origin }
        fun defaultTittel(defaultTittel: String) = apply { this.defaultTittel = defaultTittel }
        fun addTittelByLanguage(language: String, title: String) = apply { this.tittelByLanguage[language] = title }
        fun addMetric(skjemnavn: String, skjemakode: String) =
            apply { this.metrics = mutableMapOf("skjemanavn" to skjemnavn, "skjemakode" to skjemakode) }

        fun build() = Utkast(
            utkastId,
            ident,
            link,
            eventName,
            origin,
            defaultTittel,
            tittelByLanguage,
            metrics
        )

        fun create(): Utkast {
            requireNotNull(utkastId)
            requireNotNull(ident)
            requireNotNull(link)
            requireNotNull(defaultTittel)

            this.eventName = EventName.created
            return build()
        }

        fun delete(): Utkast {
            requireNotNull(utkastId)

            this.eventName = EventName.deleted
            return build()
        }
    }
}


enum class EventName {
    created, updated, deleted;
}
