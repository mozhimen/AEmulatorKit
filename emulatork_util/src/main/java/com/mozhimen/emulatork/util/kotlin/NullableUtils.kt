package com.mozhimen.emulatork.util.kotlin

/**
 * @ClassName NullableUtils
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
inline fun <T> T?.filterNullable(predicate: (T) -> Boolean): T? {
    return if (this != null && predicate(this)) {
        this
    } else {
        null
    }
}
