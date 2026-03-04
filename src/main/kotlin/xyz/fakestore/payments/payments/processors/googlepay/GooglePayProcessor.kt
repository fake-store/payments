package xyz.fakestore.payments.payments.processors.googlepay

import org.springframework.stereotype.Service
import xyz.fakestore.payments.payments.PaymentProcessor
import xyz.fakestore.payments.payments.PaymentSimulator
import xyz.fakestore.payments.dto.PaymentMethodTypeEnum
import xyz.fakestore.payments.dto.UserPaymentMethod
import xyz.fakestore.payments.dto.UserPaymentRequest

@Service
class GooglePayProcessor : PaymentProcessor() {

    override val supportedType = PaymentMethodTypeEnum.GOOGLE_PAY

    override fun describe(request: UserPaymentRequest, method: UserPaymentMethod): String {
        val gp = GooglePay(method = method)
        return "Charging ${request.amount} ${request.currency} to Google Pay account ${gp.email}"
    }

    override fun charge(request: UserPaymentRequest, method: UserPaymentMethod) {
        // TODO: integrate with Google Pay gateway
        PaymentSimulator.generateResult()
    }
}

