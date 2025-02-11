package com.payloc.imob.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.util.Date

@Document(collection = "users")
data class User(
    @Id val id: String? = null,
    val username: String,
    val name: String,
    @field:Email val email: String,
    @field:NotBlank var password: String,
    @field:NotBlank var role: String,
    var status: String = "ACTIVE",
    val createdAt: Date,
    var updatedAt: Date?
)
