package xyz.fakestore.payments.web

import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import xyz.fakestore.payments.dto.AddPaymentMethodRequest
import xyz.fakestore.payments.dto.UpdatePaymentMethodRequest
import xyz.fakestore.payments.dto.UserPaymentMethod
import xyz.fakestore.payments.dto.UserPaymentRequest
import xyz.fakestore.payments.payments.PaymentService
import java.util.UUID

@RestController
@RequestMapping("/api/payments")
class PaymentController(
    private val paymentService: PaymentService
) {

    @GetMapping("/history")
    fun getHistory(
        @RequestHeader(value = "X-Trace-Id", required = false) traceId: String?
    ): List<UserPaymentRequest> {
        if (traceId != null) MDC.put("traceId", traceId)
        return try {
            paymentService.getHistoryForUser(userId())
        } finally { if (traceId != null) MDC.remove("traceId") }
    }

    @GetMapping("/methods")
    fun getMethods(
        @RequestHeader(value = "X-Trace-Id", required = false) traceId: String?
    ): List<UserPaymentMethod> {
        if (traceId != null) MDC.put("traceId", traceId)
        return try {
            paymentService.findPaymentMethodsByUserId(userId())
        } finally { if (traceId != null) MDC.remove("traceId") }
    }

    @PostMapping("/methods")
    @ResponseStatus(HttpStatus.CREATED)
    fun addMethod(
        @RequestBody request: AddPaymentMethodRequest,
        @RequestHeader(value = "X-Trace-Id", required = false) traceId: String?
    ): UserPaymentMethod {
        if (traceId != null) MDC.put("traceId", traceId)
        return try {
            paymentService.addPaymentMethod(userId(), request)
        } finally { if (traceId != null) MDC.remove("traceId") }
    }

    @PostMapping("/methods/{id}/update")
    fun updateMethod(
        @PathVariable id: UUID,
        @RequestBody request: UpdatePaymentMethodRequest,
        @RequestHeader(value = "X-Trace-Id", required = false) traceId: String?
    ): UserPaymentMethod {
        if (traceId != null) MDC.put("traceId", traceId)
        return try {
            paymentService.updatePaymentMethod(userId(), id, request)
        } finally { if (traceId != null) MDC.remove("traceId") }
    }

    @PostMapping("/methods/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMethod(
        @PathVariable id: UUID,
        @RequestHeader(value = "X-Trace-Id", required = false) traceId: String?
    ) {
        if (traceId != null) MDC.put("traceId", traceId)
        try {
            paymentService.deletePaymentMethod(userId(), id)
        } finally { if (traceId != null) MDC.remove("traceId") }
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> =
        ResponseEntity.badRequest().body(mapOf("error" to (ex.message ?: "Bad request")))

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to (ex.message ?: "Not found")))

    private fun userId(): UUID =
        UUID.fromString(SecurityContextHolder.getContext().authentication.principal as String)
}
