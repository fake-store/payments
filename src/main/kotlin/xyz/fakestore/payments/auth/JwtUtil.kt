package xyz.fakestore.payments.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtUtil(@Value("\${jwt.secret}") private val secret: String) {

    private val key by lazy { Keys.hmacShaKeyFor(secret.toByteArray()) }

    fun validateAndGetClaims(token: String): Map<String, Any>? = runCatching {
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
            .let { mapOf("userId" to it.subject, "email" to it["email"]!!, "username" to it["username"]!!) }
    }.getOrNull()
}
