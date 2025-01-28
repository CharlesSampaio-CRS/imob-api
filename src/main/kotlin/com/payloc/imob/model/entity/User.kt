package com.payloc.imob.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Document(collection = "users")
data class User(
    @Id val id: String? = null,
    val username: String,
    val name: String,
    @field:Email val email: String,
    @field:NotBlank val password: String,
    @field:NotBlank val role: String,
    val status: String = "ACTIVE"
)
