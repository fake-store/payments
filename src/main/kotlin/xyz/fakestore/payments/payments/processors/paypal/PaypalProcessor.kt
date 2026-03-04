package xyz.fakestore.payments.payments.processors.paypal

import org.springframework.stereotype.Service
import xyz.fakestore.payments.payments.PaymentProcessor
import xyz.fakestore.payments.payments.PaymentSimulator
import xyz.fakestore.payments.dto.PaymentMethodTypeEnum
import xyz.fakestore.payments.dto.UserPaymentMethod
import xyz.fakestore.payments.dto.UserPaymentRequest

@Service
class PaypalProcessor : PaymentProcessor() {

    override val supportedType = PaymentMethodTypeEnum.PAYPAL

    override fun describe(request: UserPaymentRequest, method: UserPaymentMethod): String {
        val paypal = Paypal(method = method)
        return "Charging ${request.amount} ${request.currency} to PayPal account ${paypal.email}"
    }

    override fun charge(request: UserPaymentRequest, method: UserPaymentMethod) {
        // TODO: integrate with PayPal gateway
        PaymentSimulator.generateResult()
    }
}

