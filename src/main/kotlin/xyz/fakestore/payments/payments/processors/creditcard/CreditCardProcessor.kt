package xyz.fakestore.payments.payments.processors.creditcard

import org.springframework.stereotype.Service
import xyz.fakestore.payments.payments.PaymentProcessor
import xyz.fakestore.payments.payments.PaymentSimulator
import xyz.fakestore.payments.dto.PaymentMethodTypeEnum
import xyz.fakestore.payments.dto.UserPaymentMethod
import xyz.fakestore.payments.dto.UserPaymentRequest

@Service
class CreditCardProcessor : PaymentProcessor() {

    override val supportedType = PaymentMethodTypeEnum.CREDIT_CARD

    override fun describe(request: UserPaymentRequest, method: UserPaymentMethod): String {
        val cc = CreditCard(method = method)
        return "Charging ${request.amount} ${request.currency} to ${cc.cardNetwork} ending in ${cc.cardLastFour}"
    }

    override fun charge(request: UserPaymentRequest, method: UserPaymentMethod) {
        // TODO: integrate with credit card gateway
        PaymentSimulator.generateResult()
    }
}

