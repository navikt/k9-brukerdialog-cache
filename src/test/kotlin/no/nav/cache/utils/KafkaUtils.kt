package no.nav.cache.utils

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.utils.KafkaTestUtils
import java.time.Duration

fun EmbeddedKafkaBroker.opprettKafkaProducer(): Producer<String, Any> {
    return DefaultKafkaProducerFactory<String, Any>(HashMap(KafkaTestUtils.producerProps(this))).createProducer()
}

fun <T> Producer<String, Any>.leggPåTopic(data: T, topic: String, mapper: ObjectMapper) {
    requireNotNull(data)
    this.send(ProducerRecord(topic, data.somJson(mapper)))
    this.flush()
}

fun <T> T.somJson(mapper: ObjectMapper) = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)

fun <K, V> EmbeddedKafkaBroker.opprettKafkaConsumer(groupId: String, topicName: String): Consumer<K, V> {

    val consumerProps = KafkaTestUtils.consumerProps(groupId, "true", this)
    consumerProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = "org.apache.kafka.common.serialization.StringDeserializer"
    consumerProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = "org.apache.kafka.common.serialization.StringDeserializer"

    val consumer = DefaultKafkaConsumerFactory<K, V>(HashMap(consumerProps)).createConsumer()
    consumer.subscribe(listOf(topicName))
    return consumer
}

fun <K, V> Consumer<K, V>.hentMelding(
    topic: String,
    keyPredicate: (K) -> Boolean
): ConsumerRecord<K, V>? {
    val end = System.currentTimeMillis() + Duration.ofSeconds(10).toMillis()
    seekToBeginning(assignment())
    while (System.currentTimeMillis() < end) {
        val entries: List<ConsumerRecord<K, V>> = poll(Duration.ofSeconds(5))
            .records(topic)
            .filter { keyPredicate(it.key()) }

        if (entries.isNotEmpty()) {
            assertEquals(1, entries.size)
            return entries.first()
        }
    }
    return null
}
