package com.payloc.imob.controller.vo

import com.payloc.imob.model.enumerate.RentalStatus
import com.payloc.imob.model.enumerate.TypeWarranty
import java.time.LocalDateTime

data class RentalVO(
    val rentalNumber: Long?,
    val tenantNumber: Long?,
    val tenantName: String?,
    val propertyNumber: Long,
    val status: RentalStatus?,
    val rentalValue: Double,
    val inputValue: Double,
    val adminValue: Double,
    val dueDate: LocalDateTime,
    val paid: Boolean,
    val initDate: LocalDateTime,
    val finalDate: LocalDateTime,
    val typeWarranty: TypeWarranty,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
