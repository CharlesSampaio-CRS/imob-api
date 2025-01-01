package com.payloc.imob.model.entity

import com.payloc.imob.model.enumerate.TypeGoods

data class Goods(
    val typeGoods: TypeGoods,
    val value: Double,
)
