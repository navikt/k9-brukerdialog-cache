package no.nav.cache.utkast

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.cache.kafka.Topics.K9_DITTNAV_VARSEL_UTKAST
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service

@Service
class MineSiderService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(MineSiderService::class.java)
    }

    fun sendUtkast(utkastId: String, utkast: K9Utkast) {
        log.info("Sender utkast på kafka med id: {}", utkastId)
        kafkaTemplate.send(ProducerRecord(K9_DITTNAV_VARSEL_UTKAST, utkastId, objectMapper.writeValueAsString(utkast)))
            .exceptionally { ex: Throwable ->
                log.warn("Kunne ikke sende utkast med id {} på {}", utkastId, K9_DITTNAV_VARSEL_UTKAST, ex)
                throw ex
            }
            .thenAccept { result: SendResult<String, String> ->
                log.info("DEBUG: Utkast: {}", result.producerRecord.value()) // TODO: Fjern før produksjon
                log.info(
                    "Sendte melding med offset {} på {}",
                    result.recordMetadata.offset(),
                    result.producerRecord.topic()
                )
            }
    }
}