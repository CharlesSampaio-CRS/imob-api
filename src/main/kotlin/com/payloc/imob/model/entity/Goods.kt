package com.payloc.imob.model.entity

import com.payloc.imob.model.enumerate.TypeGoods

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Goods(
    val typeGoods: TypeGoods,
    val value: Double,
)
