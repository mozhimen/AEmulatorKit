package com.mozhimen.emulatork.util.files

import java.io.File
import java.util.Locale

/**
 * @ClassName FileUtils
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
fun File.safeDelete() = exists() && delete()

class FileUtils {
    companion object {
        fun extractExtension(fileName: String): String =
            fileName.substringAfterLast(".", "").toLowerCase(Locale.US)

        fun discardExtension(fileName: String): String = fileName.substringBeforeLast(".")
    }
}
