package xyz.fakestore.payments.persistence

import xyz.fakestore.payments.dto.UserPaymentRequest
import java.util.*

interface PaymentRepository {
    fun save(request: UserPaymentRequest): UserPaymentRequest
    fun findAll(): List<UserPaymentRequest>
    fun findById(id: UUID): UserPaymentRequest?
    fun findByMethodIds(methodIds: Set<UUID>): List<UserPaymentRequest>
    fun count(): Int
}
