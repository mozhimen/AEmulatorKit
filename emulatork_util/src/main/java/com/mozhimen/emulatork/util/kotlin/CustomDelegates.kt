package com.mozhimen.emulatork.util.kotlin

import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty

/**
 * @ClassName CustomDelegates
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
object CustomDelegates {
    inline fun <T> onChangeObservable(
        initialValue: T,
        crossinline onChange: () -> Unit
    ): ReadWriteProperty<Any?, T> =
        Delegates.observable(initialValue) { _, old, new -> if (old != new) onChange() }
}
