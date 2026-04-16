package com.example.demo_kotlin.security

import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Service
class TokenBlacklistService {

    private val revokedTokens = ConcurrentHashMap<String, Instant>()

    fun revokeToken(token: String, expiresAt: Instant?) {
        if (token.isBlank()) return
        revokedTokens[token] = expiresAt ?: Instant.now().plusSeconds(60)
    }

    fun isTokenRevoked(token: String): Boolean {
        cleanupExpiredTokens()
        return revokedTokens.containsKey(token)
    }

    private fun cleanupExpiredTokens() {
        val now = Instant.now()
        revokedTokens.entries.removeIf { (_, expiresAt) -> expiresAt.isBefore(now) }
    }
}