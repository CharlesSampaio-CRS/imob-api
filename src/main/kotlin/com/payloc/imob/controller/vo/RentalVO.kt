package com.payloc.imob.controller.vo

import com.fasterxml.jackson.annotation.JsonInclude
import com.payloc.imob.model.enumerate.RentalStatus
import com.payloc.imob.model.enumerate.TypeWarranty
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RentalVO(
    val rentalNumber: String?,
    val tenantNumber: String?,
    val tenantName: String?,
    val propertyNumber: String?,
    val status: RentalStatus?,
    val rentalValue: Double,
    val inputValue: Double,
    val adminValue: Double,
    val dueDate: Int,
    val paid: Boolean,
    val initDate: LocalDateTime,
    val finalDate: LocalDateTime,
    val typeWarranty: TypeWarranty,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

