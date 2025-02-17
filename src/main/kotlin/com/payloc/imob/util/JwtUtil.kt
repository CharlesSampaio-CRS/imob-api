package com.payloc.imob.util

import com.payloc.imob.model.entity.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*

@Suppress("UNCHECKED_CAST")
@Component
class JwtUtil {
    private val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private val expirationTime = 3600000

    fun generateToken(user: User): String {
        return Jwts.builder()
            .setSubject(user.username)
            .claim("user", user.name)
            .claim("email", user.email)
            .claim("grants", getGrants(user.role))
            .claim("authorities", getGrants(user.role))
            .claim("credentials", user.password)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expirationTime))
            .signWith(secretKey)
            .compact()
    }

    private fun getGrants(role: String): List<String> {
        return when (role) {
            "ADMIN" -> listOf("ROLE_ADMIN", "ROLE_USER")
            "USER" -> listOf("ROLE_USER")
            else -> emptyList()
        }
    }

    fun getUsernameFromToken(token: String): String {
        return getClaimFromToken(token, Claims::getSubject)
    }

    fun getAuthoritiesFromToken(token: String): List<String> {
        val claims = getAllClaimsFromToken(token)
        return claims["authorities"] as List<String>
    }

    fun validateToken(token: String): Boolean {
        return !isTokenExpired(token)
    }

    private fun <T> getClaimFromToken(token: String, claimsResolver: (Claims) -> T): T {
        val claims = getAllClaimsFromToken(token)
        return claimsResolver(claims)
    }

    private fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).body
    }

    private fun isTokenExpired(token: String): Boolean {
        val expiration = getClaimFromToken(token, Claims::getExpiration)
        return expiration.before(Date())
    }
}