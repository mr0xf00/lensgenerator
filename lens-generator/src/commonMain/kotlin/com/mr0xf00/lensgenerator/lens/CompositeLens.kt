package com.mr0xf00.lensgenerator.lens

internal abstract class CompositeLens<R, T>: Lens<R, T> {
    abstract val list: List<Lens<*, *>>
}

public operator fun <R, P, T> Lens<R, P>.plus(other: Lens<P, T>): Lens<R, T> {
    return object: CompositeLens<R, T>() {
        override val list: List<Lens<*, *>> = this@plus.split() + other.split()
        override fun get(parent: R): T {
            return other.get(this@plus.get(parent))
        }

        override fun set(parent: R, value: T): R {
            return this@plus.set(parent, other.set(this@plus.get(parent), value))
        }
    }
}

public fun <P, T> Lens<P, T>.split(): List<Lens<*, *>> = (this as? CompositeLens)?.list ?: listOf(this)
