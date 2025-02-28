package com.payloc.imob.service

import com.payloc.imob.model.dto.LoginRequestDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class AuthService(
    private val restTemplate: RestTemplate,
    @Value("\${security-manager.api.url}")
    private val baseUrl: String
) {
    fun login(request: LoginRequestDTO): ResponseEntity<Any> {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val httpEntity = HttpEntity(request, headers)
        val loginUrl = "$baseUrl/auth/login"

        return try {
            val response = restTemplate.exchange(loginUrl, HttpMethod.POST, httpEntity, Any::class.java)
            ResponseEntity.status(response.statusCode).body(response.body)
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao chamar API de login: ${ex.message}")
        }
    }
}