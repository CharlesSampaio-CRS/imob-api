package com.payloc.imob.model.entity

import com.payloc.imob.model.enumerate.RentalStatus
import com.payloc.imob.model.enumerate.TypeWarranty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class Rental(
    @Id
    var id: String?,
    var rentalNumber: String?,
    var tenant: Tenant,
    var property: Property,
    var rentalValue: Double,
    var inputValue: Double,
    var adminValue: Double,
    var dueDate: LocalDateTime,
    var paid: Boolean,
    var initDate: LocalDateTime,
    var finalDate: LocalDateTime,
    var typeWarranty: TypeWarranty,
    var status: RentalStatus?,
    var createdAt: LocalDateTime? = LocalDateTime.now(),
    var updatedAt: LocalDateTime? = null
)
