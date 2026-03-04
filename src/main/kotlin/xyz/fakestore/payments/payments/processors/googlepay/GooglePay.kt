package xyz.fakestore.payments.payments.processors.googlepay

import xyz.fakestore.payments.dto.UserPaymentMethod

data class GooglePay(
    val label: String,
    val email: String
) {
    constructor(method: UserPaymentMethod) : this(
        label = method.label,
        email = method.details["email"] as? String ?: ""
    )
}

