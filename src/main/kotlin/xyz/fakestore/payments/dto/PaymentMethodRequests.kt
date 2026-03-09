package xyz.fakestore.payments.dto

data class AddPaymentMethodRequest(
    val type: PaymentMethodTypeEnum,
    val label: String
)

data class UpdatePaymentMethodRequest(
    val label: String,
    val isDefault: Boolean
)
