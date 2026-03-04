package xyz.fakestore.payments.payments.processors.debitcard

import org.springframework.stereotype.Service
import xyz.fakestore.payments.payments.PaymentProcessor
import xyz.fakestore.payments.payments.PaymentSimulator
import xyz.fakestore.payments.dto.PaymentMethodTypeEnum
import xyz.fakestore.payments.dto.UserPaymentMethod
import xyz.fakestore.payments.dto.UserPaymentRequest

@Service
class DebitCardProcessor : PaymentProcessor() {

    override val supportedType = PaymentMethodTypeEnum.DEBIT_CARD

    override fun describe(request: UserPaymentRequest, method: UserPaymentMethod): String {
        val dc = DebitCard(method = method)
        return "Charging ${request.amount} ${request.currency} to ${dc.cardNetwork} debit ending in ${dc.cardLastFour}"
    }

    override fun charge(request: UserPaymentRequest, method: UserPaymentMethod) {
        // TODO: integrate with debit card gateway
        PaymentSimulator.generateResult()
    }
}

