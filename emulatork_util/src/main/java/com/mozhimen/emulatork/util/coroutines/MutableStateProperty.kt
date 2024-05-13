package com.mozhimen.emulatork.util.coroutines

import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @ClassName MutableStateProperty
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class MutableStateProperty<T>(
    private val mutableState: MutableStateFlow<T>
) : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = mutableState.value
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = run {
        mutableState.value = value
    }
}
