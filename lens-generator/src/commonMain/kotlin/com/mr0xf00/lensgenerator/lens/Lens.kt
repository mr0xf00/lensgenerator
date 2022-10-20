package com.mr0xf00.lensgenerator.lens

public interface Lens<out P, T> {
    public fun get(parent: @UnsafeVariance P): T
    public fun set(parent: @UnsafeVariance P, value: T): P
}

public inline fun <P, T> Lens<P, T>.edit(parent: P, mapper: (T) -> T): P = set(parent, mapper(get(parent)))

@PublishedApi
internal abstract class SimpleLens<P, T> : Lens<P, T>

public inline fun <P, T> lens(crossinline getter: (parent: P) -> T, crossinline setter: P.(value: T) -> P): Lens<P, T> = object : SimpleLens<P, T>() {
    override fun get(parent: P): T = getter(parent)
    override fun set(parent: P, value: T): P = setter(parent, value)
}