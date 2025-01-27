package com.payloc.imob.model.entity

import com.payloc.imob.model.enumerate.TypeOccupation
import java.time.LocalDate

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Occupation(
    val typeOccupation: TypeOccupation,
    val companyName: String,
    val position: String,
    val salary: Double,
    val companyAddress: Address,
    val admissionDate: LocalDate,
    val cnpj: String
)
