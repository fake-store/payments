package xyz.fakestore.payments.persistence

import xyz.fakestore.payments.dto.UserPaymentMethod
import java.util.UUID

interface PaymentMethodRepository {
    fun findById(id: UUID): UserPaymentMethod?
    fun findByUserId(userId: UUID): List<UserPaymentMethod>
    fun save(method: UserPaymentMethod): UserPaymentMethod
    fun generateRandom(userId: UUID): UserPaymentMethod
}
