package com.payloc.imob.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest
import java.util.Date

import javax.crypto.SecretKey

@Suppress("UNCHECKED_CAST")
@Component
class JwtUtil {
    private val secretKey: SecretKey = loadSecretFromAWS()

    private fun loadSecretFromAWS(): SecretKey {
        val secretName = "api-sec-manager"
        val region = Region.US_EAST_1

        val client = SecretsManagerClient.builder()
            .region(region)
            .build()

        val request = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build()

        val secretValue = client.getSecretValue(request).secretString()
        return Keys.hmacShaKeyFor(secretValue.toByteArray())
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