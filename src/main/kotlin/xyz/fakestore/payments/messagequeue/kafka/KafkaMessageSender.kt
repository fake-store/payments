package xyz.fakestore.payments.messagequeue.kafka

import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import xyz.fakestore.payments.messagequeue.MessageSender
import xyz.fakestore.payments.enumz.Topic

@Service
class KafkaMessageSender(
    private val kafkaTemplate: KafkaTemplate<String, String>
) : MessageSender {

    private val log = LoggerFactory.getLogger(KafkaMessageSender::class.java)

    override fun sendMessage(
        key: String,
        headers: Map<String, String>,
        topic: Topic,
        payload: String
    ) {
        val record = ProducerRecord(
            /* topic = */ topic.topic,
            /* key = */ key,
            /* value = */ payload
        )
        headers.forEach { (k, v) ->
            record.headers().add(k, v.toByteArray())
        }

        try {
            val result = kafkaTemplate.send(record).get()
            val metadata = result.recordMetadata
            log.info("Sent to ${metadata.topic()} partition ${metadata.partition()} offset ${metadata.offset()}")
        } catch (e: Exception) {
            log.warn("Failed to send to ${topic.topic} key $key: ${e.message}", e)
        }
    }

    override fun close() {
        kafkaTemplate.destroy()
    }
}

