package com.payloc.imob.model.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.payloc.imob.model.enumerate.PersonStatus
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Person(
    var status: PersonStatus?,
    var cpf: String,
    val name: String,
    val phone: String,
    val email: String,
    val birthDate: LocalDate,
    val naturalness: String,
    val nationality: String,
    val maritalStatus: String?,
    val motherName: String? = null,
    val fatherName: String?= null,
)
