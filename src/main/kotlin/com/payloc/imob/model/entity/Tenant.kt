package com.payloc.imob.model.entity

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
@JsonInclude(JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
data class Tenant(
    @Id
    var id: String?,
    var tenantNumber: String?,
    var person: Person,
    var address: Address,
    var occupation: Occupation,
    var numberOfChildren: Int,
    var minors: Int,
    var spouse: Spouse?,
    var participation: Participation?,
    var references: List<Reference>? = emptyList(),
    var goods: List<Goods>? = emptyList(),
    var createdAt: LocalDateTime? = LocalDateTime.now(),
    var updatedAt: LocalDateTime? = null,
    var rentalNumber: List<String>? = listOf())
