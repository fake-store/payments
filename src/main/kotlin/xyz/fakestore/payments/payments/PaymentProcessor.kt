package xyz.fakestore.payments.payments

import org.slf4j.LoggerFactory
import xyz.fakestore.payments.dto.PaymentMethodTypeEnum
import xyz.fakestore.payments.dto.UserPaymentMethod
import xyz.fakestore.payments.dto.UserPaymentRequest

abstract class PaymentProcessor {

    private val log = LoggerFactory.getLogger(this::class.java)

    abstract val supportedType: PaymentMethodTypeEnum

    /** Return a human-readable description of the charge (used in trace logging). */
    abstract fun describe(request: UserPaymentRequest, method: UserPaymentMethod): String

    /** Execute the actual charge against the payment gateway. */
    protected abstract fun charge(request: UserPaymentRequest, method: UserPaymentMethod)

    fun processPayment(userPaymentRequest: UserPaymentRequest, userPaymentMethod: UserPaymentMethod) {
        log.trace(describe(request = userPaymentRequest, method = userPaymentMethod))
        try {
            charge(request = userPaymentRequest, method = userPaymentMethod)
        } catch (e: Exception) {
            log.warn("${supportedType.label} payment failed: ${e.message}")
            throw e
        }
        log.trace("${supportedType.label} payment succeeded")
    }
}


