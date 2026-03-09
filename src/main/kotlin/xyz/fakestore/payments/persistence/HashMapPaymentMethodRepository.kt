package xyz.fakestore.payments.persistence

import org.springframework.stereotype.Repository
import xyz.fakestore.payments.dto.PaymentMethodTypeEnum
import xyz.fakestore.payments.dto.UserPaymentMethod
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Repository
class HashMapPaymentMethodRepository : PaymentMethodRepository {

    private val live = ConcurrentHashMap<UUID, UserPaymentMethod>()

    override fun findById(id: UUID): UserPaymentMethod? = live[id]

    override fun findByUserId(userId: UUID): List<UserPaymentMethod> =
        live.values.filter { it.userId == userId }

    override fun save(method: UserPaymentMethod): UserPaymentMethod {
        live[method.userPaymentMethodId] = method
        return method
    }

    override fun delete(id: UUID) { live.remove(id) }

    override fun generateRandom(userId: UUID): UserPaymentMethod {
        val type = TYPES.random()
        val label = when (type) {
            PaymentMethodTypeEnum.CREDIT_CARD -> "${NETWORKS.random()} ${(1000..9999).random()}"
            PaymentMethodTypeEnum.DEBIT_CARD  -> "${NETWORKS.random()} Debit ${(1000..9999).random()}"
            PaymentMethodTypeEnum.PAYPAL      -> "PayPal (${EMAILS.random()})"
            PaymentMethodTypeEnum.APPLE_PAY   -> "Apple Pay"
            PaymentMethodTypeEnum.GOOGLE_PAY  -> "Google Pay"
            PaymentMethodTypeEnum.ACH         -> "Bank Account ****${(1000..9999).random()}"
        }
        return UserPaymentMethod(
            userPaymentMethodId = UUID.randomUUID(),
            userId = userId,
            type = type,
            label = label,
            isDefault = false
        )
    }

    companion object {
        private val TYPES = listOf(
            PaymentMethodTypeEnum.CREDIT_CARD,
            PaymentMethodTypeEnum.DEBIT_CARD,
            PaymentMethodTypeEnum.PAYPAL,
            PaymentMethodTypeEnum.APPLE_PAY,
            PaymentMethodTypeEnum.GOOGLE_PAY,
            PaymentMethodTypeEnum.ACH
        )
        private val NETWORKS = listOf("Visa", "Mastercard", "Amex", "Discover")
        private val EMAILS = listOf("user@gmail.com", "me@outlook.com", "pay@yahoo.com")
    }
}
