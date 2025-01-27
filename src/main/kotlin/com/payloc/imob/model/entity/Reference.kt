package com.payloc.imob.model.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.payloc.imob.model.enumerate.TypeReference

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Reference (
    val typeReference: TypeReference,
    val name: String,
    val phone: String,
    val relationship: String,
)