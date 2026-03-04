package xyz.fakestore.payments.payments.processors.ach

import xyz.fakestore.payments.dto.UserPaymentMethod

data class Ach(
    val label: String,
    val bankName: String,
    val accountLastFour: String
) {
    constructor(method: UserPaymentMethod) : this(
        label = method.label,
        bankName = method.details["bankName"] as? String ?: "",
        accountLastFour = method.details["accountLastFour"] as? String ?: ""
    )
}

