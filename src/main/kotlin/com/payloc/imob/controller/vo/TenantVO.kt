package com.payloc.imob.controller.vo

import com.payloc.imob.model.enumerate.PersonStatus

data class TenantVO(
    val id: String?,
    val tenantNumber: String?,
    val name: String?,
    val cpf: String?,
    val status: PersonStatus?,
)
