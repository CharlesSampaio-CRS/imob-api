package com.payloc.imob.model.entity

import com.payloc.imob.model.enumerate.PersonStatus
import java.time.LocalDate

data class Person(
    var status: PersonStatus?,
    val cpf: String,
    val name: String,
    val phone: String,
    val email: String,
    val birthDate: LocalDate,
    val naturalness: String,
    val nationality: String,
    val maritalStatus: String?,
    val motherName: String?,
    val fatherName: String?,
)
