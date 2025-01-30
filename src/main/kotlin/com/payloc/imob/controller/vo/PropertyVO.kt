package com.payloc.imob.controller.vo

import com.fasterxml.jackson.annotation.JsonInclude
import com.payloc.imob.model.enumerate.PropertyStatus
import com.payloc.imob.model.enumerate.TypeProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PropertyVO(
    val propertyNumber: String?,
    val typeProperty: TypeProperty,
    val status: PropertyStatus?,
    val owner: String,
    val value: Double,
    val createdAt: String
)
