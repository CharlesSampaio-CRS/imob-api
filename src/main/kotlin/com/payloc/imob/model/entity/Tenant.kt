package com.payloc.imob.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
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
