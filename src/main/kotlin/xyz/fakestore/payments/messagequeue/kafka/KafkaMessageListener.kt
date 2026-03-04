package xyz.fakestore.payments.messagequeue.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.header.Header
import org.slf4j.MDC
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import xyz.fakestore.payments.dto.UserPaymentRequest
import xyz.fakestore.payments.payments.PaymentService
import xyz.fakestore.payments.enumz.Topics

@Service
class KafkaMessageListener(
    private val paymentService: PaymentService,
    private val mapper: ObjectMapper
) {

    @KafkaListener(
        topics = [Topics.Orders.PAYMENTS_PAYMENTREQUESTED],
        groupId = "payment-service"
    )
    fun onPaymentRequestMessageReceived(record: ConsumerRecord<String, String>, acknowledgment: Acknowledgment) {
        val payload = record.value()
        val headers = record.headers().associate<Header, String, String> { it.key() to String(it.value()) }
        val request = mapper.readValue<UserPaymentRequest>(payload)

        headers["traceId"]?.let { MDC.put("traceId", it) }

        try {
            paymentService.processPayment(request)  // denied payments dont throw, but processing errors will throw and prevent ack
            acknowledgment.acknowledge()
        } finally {
            MDC.remove("traceId")
        }
    }
}
