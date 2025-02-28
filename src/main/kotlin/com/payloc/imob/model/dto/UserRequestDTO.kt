package com.payloc.imob.model.dto

data class UserRequestDTO(
    val id: String?,
    val email: String,
    val name: String,
    val password: String,
    val role: String,
    val status: String?,
    val token: String?
)
