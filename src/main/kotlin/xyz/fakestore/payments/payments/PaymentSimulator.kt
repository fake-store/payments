package xyz.fakestore.payments.payments

import org.slf4j.LoggerFactory

/**
 * Simulates payment outcomes for testing.
 *
 * TestException (5% — simulated processing error)
 * PaymentRejectedException (10% — simulated decline)
 * success (85%)
 */
object PaymentSimulator {

    private val log = LoggerFactory.getLogger(PaymentSimulator::class.java)

    // LEAVE THIS FALSE IN SOURCE CONTROL
    //LEAVE THIS FALSE FOR UNIT TESTING
    val enableRandomErrors = true

    private val rejectionReasons = listOf(
        "Insufficient funds",
        "Card expired",
        "Account frozen",
        "Suspected fraud",
        "Daily limit exceeded"
    )

    fun generateResult() {
        if (!enableRandomErrors) return

        // RANDOM OUTCOME!
        val roll = (1..100).random()
        when {
            roll <= 5 -> {
                log.warn("RANDOM RESULT: throwing TestException")
                throw TestException("Test exception: this error was randomly, intentionally generated for testing purposes.")
            }

            roll <= 15 -> {
                val reason = rejectionReasons.random()
                log.warn("RANDOM RESULT: rejecting: $reason")
                throw PaymentRejectedException(reason = reason)
            }

            else -> {
                log.info("RANDOM RESULT: approved")
            }
        }
    }
}


