package xyz.fakestore.payments.payments.processors.creditcard

import xyz.fakestore.payments.dto.UserPaymentMethod

data class CreditCard(
    val label: String,
    val cardLastFour: String,
    val expirationMonth: Int,
    val expirationYear: Int,
    val cardNetwork: String
) {
    constructor(method: UserPaymentMethod) : this(
        label = method.label,
        cardLastFour = method.details["cardLastFour"] as? String ?: "",
        expirationMonth = (method.details["expirationMonth"] as? Number)?.toInt() ?: 0,
        expirationYear = (method.details["expirationYear"] as? Number)?.toInt() ?: 0,
        cardNetwork = method.details["cardNetwork"] as? String ?: ""
    )
}

