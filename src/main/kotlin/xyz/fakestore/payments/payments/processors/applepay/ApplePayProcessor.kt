package xyz.fakestore.payments.payments.processors.applepay

import org.springframework.stereotype.Service
import xyz.fakestore.payments.payments.PaymentProcessor
import xyz.fakestore.payments.payments.PaymentSimulator
import xyz.fakestore.payments.dto.PaymentMethodTypeEnum
import xyz.fakestore.payments.dto.UserPaymentMethod
import xyz.fakestore.payments.dto.UserPaymentRequest

@Service
class ApplePayProcessor : PaymentProcessor() {

    override val supportedType = PaymentMethodTypeEnum.APPLE_PAY

    override fun describe(request: UserPaymentRequest, method: UserPaymentMethod): String {
        val ap = ApplePay(method = method)
        return "Charging ${request.amount} ${request.currency} to Apple Pay device ${ap.deviceAccountId}"
    }

    override fun charge(request: UserPaymentRequest, method: UserPaymentMethod) {
        // TODO: integrate with Apple Pay gateway
        PaymentSimulator.generateResult()
    }
}

