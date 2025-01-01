package com.payloc.imob.model.entity

data class Address(
    val street: String,
    val number: String,
    val complement: String? = null,
    val neighborhood: String,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String
)
