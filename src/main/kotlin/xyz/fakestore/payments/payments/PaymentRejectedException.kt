package xyz.fakestore.payments.payments

/**
 * Thrown when a payment was submitted to the processor and explicitly rejected
 * (e.g. insufficient funds, card declined, account frozen).
 *
 * This is distinct from an unexpected processing error — a rejection is a
 * definitive business outcome, not a transient failure.
 */
class PaymentRejectedException(
    val reason: String,
    cause: Throwable? = null
) : Exception("Payment rejected: $reason", cause)

