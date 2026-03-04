package xyz.fakestore.payments.payments.processors.ach

import xyz.fakestore.payments.dto.PaymentMethodTypeEnum
import xyz.fakestore.payments.dto.UserPaymentMethod
import xyz.fakestore.payments.dto.UserPaymentRequest
import xyz.fakestore.payments.payments.PaymentProcessor
import xyz.fakestore.payments.payments.PaymentSimulator
import org.springframework.stereotype.Service

@Service
class AchProcessor : PaymentProcessor() {

    override val supportedType = PaymentMethodTypeEnum.ACH

    override fun describe(request: UserPaymentRequest, method: UserPaymentMethod): String {
        val ach = Ach(method = method)
        return "Charging ${request.amount} ${request.currency} via ACH from ${ach.bankName} account ending in ${ach.accountLastFour}"
    }

    override fun charge(request: UserPaymentRequest, method: UserPaymentMethod) {
        // TODO: integrate with ACH gateway
        PaymentSimulator.generateResult()
    }
}

