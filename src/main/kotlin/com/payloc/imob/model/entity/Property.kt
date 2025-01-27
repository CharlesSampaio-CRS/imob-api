package com.payloc.imob.model.entity

import com.payloc.imob.model.enumerate.PropertyStatus
import com.payloc.imob.model.enumerate.TypeProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import com.fasterxml.jackson.annotation.JsonInclude

@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Property (

    @Id
    var id: String?,
    var propertyNumber: String?,
    var typeProperty: TypeProperty,
    var status: PropertyStatus?,
    var owner: Person,
    var value: Double,
    var address: Address,
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null
)