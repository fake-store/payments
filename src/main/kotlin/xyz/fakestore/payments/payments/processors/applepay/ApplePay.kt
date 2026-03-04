package xyz.fakestore.payments.payments.processors.applepay

import xyz.fakestore.payments.dto.UserPaymentMethod

data class ApplePay(
    val label: String,
    val deviceAccountId: String
) {
    constructor(method: UserPaymentMethod) : this(
        label = method.label,
        deviceAccountId = method.details["deviceAccountId"] as? String ?: ""
    )
}

