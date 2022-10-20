package com.mr0xf00.lensgenerator

@GenerateLens
data class RangedValue<V0 : Any, V1 : Any>(
    val from: Int,
    val to: Int,
    val value0: V0,
    val value1: V1
) {
    companion object
}