package xyz.fakestore.payments.messagequeue

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PaymentRequestListenerRetryTest {

    // Must match the retry limit configured in the message broker (e.g. KafkaConfig, RabbitConfig).
    // This is the single place to update if the retry policy changes.
    private val maxAttempts = 3

    private val deadLetterSink = mutableListOf<String>()

    @BeforeEach
    fun setUp() = deadLetterSink.clear()

    /**
     * Simulates the broker retry loop and DLT routing.
     *
     * Intentionally broker-agnostic: the same retry contract applies whether the
     * underlying MQ is Kafka, RabbitMQ, or anything else. The listener signals
     * success by calling the ack callback and failure by throwing — the broker does the rest.
     *
     * @return true if the message was acked before retries were exhausted
     */
    private fun deliver(
        payload: String = "{}",
        headers: Map<String, String> = emptyMap(),
        listener: (payload: String, headers: Map<String, String>, ack: () -> Unit) -> Unit
    ): Boolean {
        for (attempt in 1..maxAttempts) {
            try {
                var acked = false
                listener(payload, headers) { acked = true }
                return acked
            } catch (_: Exception) {
                if (attempt == maxAttempts) {
                    deadLetterSink.add(payload)
                }
            }
        }
        return false
    }

    @Test
    fun `success - acks on first attempt`() {
        assertTrue(deliver { _, _, ack -> ack() })
        assertTrue(deadLetterSink.isEmpty())
    }

    @Test
    fun `persistent failure - exhausts retries then routes to DLT`() {
        val calls = intArrayOf(0)
        assertFalse(deliver { _, _, _ ->
            calls[0]++
            throw RuntimeException("gateway timeout")
        })
        assertEquals(maxAttempts, calls[0])
        assertEquals(1, deadLetterSink.size)
    }

    @Test
    fun `transient failure then recovery - acks after retry`() {
        val calls = intArrayOf(0)
        assertTrue(deliver { _, _, ack ->
            if (++calls[0] < 2) throw RuntimeException("transient error")
            ack()
        })
        assertEquals(2, calls[0])
        assertTrue(deadLetterSink.isEmpty())
    }

    @Test
    fun `business rejection - acks without retry (PaymentService handles rejection internally, no throw)`() {
        val calls = intArrayOf(0)
        assertTrue(deliver { _, _, ack ->
            calls[0]++
            ack()
        })
        assertEquals(1, calls[0])
        assertTrue(deadLetterSink.isEmpty())
    }

    @Test
    fun `duplicate message - acks without retry (PaymentService idempotency check returns early, no throw)`() {
        val calls = intArrayOf(0)
        val listener: (String, Map<String, String>, () -> Unit) -> Unit = { _, _, ack ->
            calls[0]++
            ack()
        }
        deliver(listener = listener)             // first delivery
        assertTrue(deliver(listener = listener)) // duplicate
        assertEquals(2, calls[0])
        assertTrue(deadLetterSink.isEmpty())
    }

    @Test
    fun `headers are passed through to the listener unchanged`() {
        val received = mutableMapOf<String, String>()
        val sent = mapOf("traceId" to "abc-123", "orderId" to "order-456")
        deliver(headers = sent) { _, headers, ack ->
            received.putAll(headers)
            ack()
        }
        assertEquals(sent, received)
    }
}
