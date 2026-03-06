package xyz.fakestore.payments.web

import org.slf4j.MDC
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xyz.fakestore.payments.payments.PaymentService
import java.util.UUID

@RestController
@RequestMapping("/api/payments")
class PaymentController(
    private val paymentService: PaymentService
) {

    @GetMapping("/me")
    fun me(
        @RequestHeader(value = "X-Trace-Id", required = false) traceId: String?
    ): String {
        if (traceId != null) MDC.put("traceId", traceId)
        try {
            val userId = UUID.fromString(SecurityContextHolder.getContext().authentication.principal as String)
            val methods = paymentService.findPaymentMethodsByUserId(userId)
            return "Hello from Payments Service! ${methods}"
        } finally {
            if (traceId != null) MDC.remove("traceId")
        }
    }
}
