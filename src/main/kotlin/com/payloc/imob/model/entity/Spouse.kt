package com.payloc.imob.model.entity

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Spouse(
    val person: Person,
    val occupation: Occupation,
)
