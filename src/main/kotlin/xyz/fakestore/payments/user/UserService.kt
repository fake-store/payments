package xyz.fakestore.payments.user

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Service
import xyz.fakestore.payments.dto.UserPaymentMethod
import java.util.*

@Service
class UserService {

    private val paymentMethods: Map<UUID, UserPaymentMethod>
    private val mapper = jacksonObjectMapper()

    init {
        val json = this::class.java.classLoader
            .getResourceAsStream("user-payment-methods.json")
            ?.bufferedReader()?.readText()
            ?: throw IllegalStateException("user-payment-methods.json not found")

        paymentMethods = mapper.readValue<List<UserPaymentMethod>>(json).associateBy { it.userPaymentMethodId }
    }

    fun getUserPaymentMethod(userPaymentMethodId: UUID): UserPaymentMethod {
        return paymentMethods[userPaymentMethodId]
            ?: throw IllegalArgumentException("Payment method not found: $userPaymentMethodId")
    }

    fun getAllPaymentMethods(): List<UserPaymentMethod> {
        return paymentMethods.values.toList()
    }
}
