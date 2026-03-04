package xyz.fakestore.payments.dto

import com.fasterxml.jackson.annotation.JsonValue

enum class PaymentMethodTypeEnum(
    val label: String,
    @JsonValue val simpleName: String
) {
    CREDIT_CARD("Credit Card", "CreditCard"),
    DEBIT_CARD("Debit Card", "DebitCard"),
    PAYPAL("PayPal", "Paypal"),
    APPLE_PAY("Apple Pay", "ApplePay"),
    GOOGLE_PAY("Google Pay", "GooglePay"),
    ACH("ACH", "Ach");

    companion object {
        private val bySimpleName = entries.associateBy { it.simpleName }

        fun fromSimpleName(name: String): PaymentMethodTypeEnum =
            bySimpleName[name]
                ?: throw IllegalArgumentException("Unknown payment method type: $name")
    }
}

