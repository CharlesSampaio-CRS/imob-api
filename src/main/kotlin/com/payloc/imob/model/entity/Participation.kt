package com.payloc.imob.model.entity

import java.time.LocalDate

data class Participation(
    val socialReason: String,
    val companyName: String,
    val admissionDate: LocalDate,
    val companyAddress: Address,
    val salary: Double,
    val cnpj: String
)
