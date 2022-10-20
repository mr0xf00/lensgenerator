package com.mr0xf00.lensgenerator.functional

import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentHashMap
import kotlinx.collections.immutable.toPersistentList

fun <T> List<T>.removeRange(from: Int = 0, toExclusive: Int = size): List<T> {
    if (toExclusive - from < 1) return this
    return toPersistentList().mutate { it.subList(from, toExclusive).clear() }
}

fun <T> List<T>.add(e: T): List<T> = toPersistentList().add(e)

fun <K, V> Map<K, V>.remove(key: K): Map<K, V> {
    return toPersistentHashMap().remove(key)
}

fun <K, V> Map<K, V>.put(key: K, value: V): Map<K, V> {
    return toPersistentHashMap() //toPersistentMap() for ordered map
        .put(key, value)
}

fun <T> List<T>.set(index: Int, element: T): List<T> = toPersistentList().set(index, element)

fun <T> List<T>.removeAt(index: Int): List<T> = toPersistentList().removeAt(index)
