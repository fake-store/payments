package xyz.fakestore.payments.persistence

import org.springframework.stereotype.Repository
import xyz.fakestore.payments.dto.UserPaymentRequest
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Repository
class HashMapPaymentRepository : PaymentRepository {

    private val payments = ConcurrentHashMap<UUID, UserPaymentRequest>()

    override fun save(request: UserPaymentRequest): UserPaymentRequest {
        payments[request.userPaymentRequestId] = request
        return request
    }

    override fun findAll(): List<UserPaymentRequest> {
        return payments.values.toList()
    }

    override fun findById(id: UUID): UserPaymentRequest? {
        return payments[id]
    }

    override fun findByMethodIds(methodIds: Set<UUID>): List<UserPaymentRequest> {
        return payments.values.filter { it.userPaymentMethodId in methodIds }
    }

    override fun count(): Int {
        return payments.size
    }
}

