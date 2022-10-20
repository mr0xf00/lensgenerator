package com.mr0xf00.lensgenerator.functional

import com.mr0xf00.lensgenerator.lens.Lens
import com.mr0xf00.lensgenerator.lens.lens
import com.mr0xf00.lensgenerator.lens.plus

operator fun <P, K, V> Lens<P, Map<K, V>>.get(key: K): Lens<P, V> {
    return this + lens({ it.getValue(key) }) { value -> put(key, value) }
}

fun <P, K, V> Lens<P, Map<K, V>>.getOrNull(key: K): Lens<P, V?> {
    return this + lens({ it[key] }) { value -> if (value == null) remove(key) else put(key, value) }
}

operator fun <P, V> Lens<P, List<V>>.get(index: Int): Lens<P, V> {
    return this + lens({ it[index] }) { value -> set(index, value) }
}

internal inline fun <P, T> P.editValue(
    lens: Lens<P, T>,
    editor: (T) -> (T)
): P = lens.set(this, editor(lens.get(this)))

internal fun <P, T> P.setValue(lens: Lens<P, T>, value: T): P = lens.set(this, value)