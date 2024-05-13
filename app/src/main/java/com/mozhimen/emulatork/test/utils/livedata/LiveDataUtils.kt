package com.mozhimen.emulatork.test.utils.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.map

/**
 * @ClassName LiveDataUtils
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */

fun <T, K, S> LiveData<T>.combineLatest(
    other: LiveData<K>,
    combine: (data1: T, data2: K) -> S
): LiveData<S> {
    return CombinedLiveData(this, other, combine)
}

fun <T> LiveData<T>.throttle(delayMs: Long): LiveData<T> {
    return ThrottledLiveData(this, delayMs)
}

fun <T, K> LiveData<T>.map(mapper: (T) -> K): LiveData<K> {
    return this.map(mapper)
}
