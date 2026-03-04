package xyz.fakestore.payments.payments

/**
 * Thrown intentionally during simulated payment processing to test error handling.
 */
class TestException(message: String) : Exception(message)

