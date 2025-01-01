package com.payloc.imob.model.entity

import com.payloc.imob.model.enumerate.TypeReference

data class Reference (
    val typeReference: TypeReference,
    val name: String,
    val phone: String,
    val relationship: String,
)