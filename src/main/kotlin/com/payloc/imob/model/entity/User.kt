package com.payloc.imob.model.entity

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
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