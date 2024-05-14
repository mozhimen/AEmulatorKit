package com.mozhimen.emulatork.util.kotlin

/**
 * @ClassName StringsUtils
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
fun String.startsWithAny(strings: Collection<String>) = strings.any { this.startsWith(it) }
