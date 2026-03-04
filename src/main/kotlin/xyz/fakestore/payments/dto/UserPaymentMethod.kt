package xyz.fakestore.payments.dto

import java.util.*

data class UserPaymentMethod(
    val userPaymentMethodId: UUID,
    val userId: String,
    val type: PaymentMethodTypeEnum,
    val label: String = "",
    val isDefault: Boolean = false,
    val active: Boolean = true,
    val details: Map<String, Any> = emptyMap()
)

