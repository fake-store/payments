package xyz.fakestore.payments.dto

import xyz.fakestore.payments.enumz.Currency
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class UserPaymentRequest(
    val userPaymentRequestId: UUID,
    val orderId: UUID,
    val userPaymentMethodId: UUID,
    val amount: BigDecimal,
    val currency: Currency,
    val createdAt: Instant = Instant.now()
)

