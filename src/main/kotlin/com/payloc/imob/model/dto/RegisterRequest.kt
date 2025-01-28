package com.payloc.imob.model.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class RegisterRequest(
    @field:Email val email: String,
    @field:NotBlank val name: String,
    @field:NotBlank val password: String,
    @field:NotBlank val role: String
)
