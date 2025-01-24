package com.payloc.imob.model.entity

import java.time.LocalDate

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Participation(
    val socialReason: String,
    val companyName: String,
    val admissionDate: LocalDate,
    val companyAddress: Address,
    val salary: Double,
    val cnpj: String
)
