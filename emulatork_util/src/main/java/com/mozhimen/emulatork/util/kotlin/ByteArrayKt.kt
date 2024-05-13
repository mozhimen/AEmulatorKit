package com.mozhimen.emulatork.util.kotlin

/**
 * @ClassName ByteArrayKt
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
// https://bitbucket.org/snippets/gelin/zLebo/extension-functions-to-format-bytes-as-hex

private val CHARS = arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

fun Byte.toHexString(): String {
    val i = this.toInt()
    val char2 = CHARS[i and 0x0f]
    val char1 = CHARS[i shr 4 and 0x0f]
    return "$char1$char2"
}

fun ByteArray.toHexString(): String {
    val builder = StringBuilder()
    for (b in this) {
        builder.append(b.toHexString())
    }
    return builder.toString()
}

fun ByteArray.isAllZeros(): Boolean = this.firstOrNull { it != 0x0.toByte() } == null

/** Return the index at which the array was found or -1. */
fun ByteArray.indexOf(byteArray: ByteArray): Int {
    if (byteArray.isEmpty()) {
        return 0
    }

    outer@ for (i in 0 until this.size - byteArray.size + 1) {
        for (j in byteArray.indices) {
            if (this[i + j] != byteArray[j]) {
                continue@outer
            }
        }
        return i
    }
    return -1
}