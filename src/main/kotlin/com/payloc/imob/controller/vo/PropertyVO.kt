package com.payloc.imob.controller.vo

import com.payloc.imob.model.enumerate.PropertyStatus
import com.payloc.imob.model.enumerate.TypeProperty

data class PropertyVO(
    val propertyNumber: String?,
    val typeProperty: TypeProperty,
    val status: PropertyStatus?,
    val owner: String,
    val value: Double,
    val createdAt: String
)
