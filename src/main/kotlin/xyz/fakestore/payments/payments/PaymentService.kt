package xyz.fakestore.payments.payments

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Service
import xyz.fakestore.payments.dto.AddPaymentMethodRequest
import xyz.fakestore.payments.dto.UpdatePaymentMethodRequest
import xyz.fakestore.payments.dto.UserPaymentMethod
import xyz.fakestore.payments.dto.UserPaymentRequest
import xyz.fakestore.payments.enumz.Topic
import xyz.fakestore.payments.messagequeue.MessageSender
import xyz.fakestore.payments.persistence.PaymentMethodRepository
import xyz.fakestore.payments.persistence.PaymentRepository
import java.util.*

@Service
class PaymentService(
    private val paymentMethodRepository: PaymentMethodRepository,
    private val paymentRepository: PaymentRepository,
    private val messageSender: MessageSender,
    private val mapper: ObjectMapper,
    processors: List<PaymentProcessor>
) {
    private val log = LoggerFactory.getLogger(PaymentService::class.java)
    private val processorsByType = processors.associateBy { it.supportedType }


    init {
        log.info("Discovered ${processorsByType.size} payment processor(s):")
        processorsByType.forEach { (type, proc) -> log.info("  - ${proc::class.simpleName} -> ${type.simpleName}") }
    }

    fun processPayment(request: UserPaymentRequest) {
        MDC.put("orderId", request.orderId.toString())
        MDC.put("paymentId", request.userPaymentRequestId.toString())
        try {
            processPaymentInternal(request)
        } finally {
            MDC.remove("orderId")
            MDC.remove("paymentId")
        }
    }

    private fun processPaymentInternal(request: UserPaymentRequest) {
        log.info(" ###########    Processing payment request")

        if (paymentRepository.findById(request.userPaymentRequestId) != null) {
            log.warn("Payment request already processed — skipping")
            return
        }

        val userPaymentMethod = paymentMethodRepository.findById(request.userPaymentMethodId)
            ?: throw IllegalArgumentException("Payment method not found: ${request.userPaymentMethodId}")

        val paymentProcessor = processorsByType[userPaymentMethod.type]
            ?: throw IllegalStateException("No processor registered for payment type: ${userPaymentMethod.type.simpleName}")

        // --- Attempt the irreversible action: charge the payment method ---
        var status: String
        var topic: Topic
        var error: String? = null

        try {
            paymentProcessor.processPayment(
                userPaymentRequest = request,
                userPaymentMethod = userPaymentMethod
            )
            status = "PROCESSED"
            topic = Topic.PAYMENTS__PAYMENT_PROCESSED
            log.info("Payment processed successfully")
        } catch (e: PaymentRejectedException) {
            status = "REJECTED"
            topic = Topic.PAYMENTS__PAYMENT_REJECTED
            error = e.reason
            log.warn("Payment rejected: ${e.reason}")
        }
        // Non-rejection exceptions propagate to the listener → Kafka retry → DLT after exhaustion.

        val outgoingHeaders = mutableMapOf("status" to status)
        MDC.get("traceId")?.let { outgoingHeaders["traceId"] = it }
        MDC.get("orderId")?.let { outgoingHeaders["orderId"] = it }
        MDC.get("paymentId")?.let { outgoingHeaders["paymentId"] = it }
        error?.let { outgoingHeaders["error"] = it }

        saveQuietly(request = request, status = status)
        sendQuietly(
            payload = request,
            headers = outgoingHeaders,
            topic = topic,
            key = request.userPaymentRequestId.toString()
        )
    }

    private fun saveQuietly(request: UserPaymentRequest, status: String) {
        try {
            paymentRepository.save(request)
        } catch (e: Exception) {
            log.error("CRITICAL: Failed to persist $status for ${request.userPaymentRequestId}: ${e.message}", e)
        }
    }

    private fun sendQuietly(payload: Any, headers: Map<String, String>, topic: Topic, key: String) {
        try {
            messageSender.sendMessage(
                key = key,
                topic = topic,
                headers = headers,
                payload = mapper.writeValueAsString(payload)

            )
        } catch (e: Exception) {
            log.error("CRITICAL: Failed to send message to ${topic} for key ${key}: ${e.message}", e)
        }
    }

    fun findPaymentMethodsByUserId(userId: UUID): List<UserPaymentMethod> {
        return paymentMethodRepository.findByUserId(userId)

            // TODO: remove when poc is done
            .ifEmpty {
                (1..3).map { paymentMethodRepository.save(paymentMethodRepository.generateRandom(userId)) }
            }
    }

    fun addPaymentMethod(userId: UUID, request: AddPaymentMethodRequest): UserPaymentMethod {
        val method = UserPaymentMethod(
            userPaymentMethodId = UUID.randomUUID(),
            userId = userId,
            type = request.type,
            label = request.label
        )
        return paymentMethodRepository.save(method)
    }

    fun updatePaymentMethod(userId: UUID, methodId: UUID, request: UpdatePaymentMethodRequest): UserPaymentMethod {
        val existing = paymentMethodRepository.findById(methodId)
            ?: throw NoSuchElementException("Payment method not found: $methodId")
        if (existing.userId != userId) throw IllegalArgumentException("Payment method does not belong to user")
        if (request.isDefault) {
            paymentMethodRepository.findByUserId(userId).forEach {
                if (it.isDefault) paymentMethodRepository.save(it.copy(isDefault = false))
            }
        }
        return paymentMethodRepository.save(existing.copy(label = request.label, isDefault = request.isDefault))
    }

    fun deletePaymentMethod(userId: UUID, methodId: UUID) {
        val existing = paymentMethodRepository.findById(methodId)
            ?: throw NoSuchElementException("Payment method not found: $methodId")
        if (existing.userId != userId) throw IllegalArgumentException("Payment method does not belong to user")
        paymentMethodRepository.delete(methodId)
    }
}
