package com.payloc.imob.model.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.payloc.imob.model.enumerate.PersonStatus
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TenantDTO(
    val tenantNumber: String?,
    val name: String?,
    val cpf: String?,
    val status: PersonStatus?,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val files: List<String>? = null

)
