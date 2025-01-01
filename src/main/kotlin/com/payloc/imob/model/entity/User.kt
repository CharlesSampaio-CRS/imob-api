package com.payloc.imob.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(
    @Id
    val id: Int,
    val username: String,
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val status: String
)