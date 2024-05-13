package com.mozhimen.emulatork.util.kotlin

/**
 * @ClassName CollectionKt
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
fun <K, V> Map<K, V?>.filterNotNullValues(): Map<K, V> {
    val destination = mutableMapOf<K, V>()
    for ((key, value) in this) {
        if (value != null) {
            destination[key] = value
        }
    }
    return destination
}

inline fun <X, Y, Z, H> Map<X, Y>.zipOnKeys(other: Map<X, Z>, f: (Y, Z) -> H): Map<X, H> {
    return this.keys.intersect(other.keys)
        .map { key ->
            key to f(this[key]!!, other[key]!!)
        }
        .toMap()
}

fun <E> Array<E>.toIndexedMap(): Map<Int, E> = this
    .mapIndexed { index, e -> index to e }
    .toMap()

inline fun <T, K> Iterable<T>.associateByNotNull(keySelector: (T) -> K?): Map<K, T> {
    return this.map { keySelector(it) to it }
        .filter { (key, _) -> key != null }
        .associate { (key, value) -> key!! to value }
}
