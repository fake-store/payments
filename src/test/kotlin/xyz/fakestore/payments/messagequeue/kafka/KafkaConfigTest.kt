package xyz.fakestore.payments.messagequeue.kafka

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.ContainerProperties

@Suppress("UNCHECKED_CAST")
class KafkaConfigTest {

    private val kafkaTemplate = mock(KafkaTemplate::class.java) as KafkaTemplate<String, String>
    private val consumerFactory = mock(ConsumerFactory::class.java) as ConsumerFactory<String, String>
    private val config = KafkaConfig(kafkaTemplate)

    @Test
    fun `kafkaListenerContainerFactory sets AckMode MANUAL`() {
        val factory = config.kafkaListenerContainerFactory(consumerFactory)
        assertEquals(ContainerProperties.AckMode.MANUAL, factory.containerProperties.ackMode)
    }
}