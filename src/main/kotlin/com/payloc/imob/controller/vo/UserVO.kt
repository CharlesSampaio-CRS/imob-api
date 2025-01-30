package com.payloc.imob.controller.vo

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserVO(
    val username: String,
    val password: String,
    val role: String,
    val name: String,
    val email: String
)