package com.mozhimen.emulatork.basic.storage

import android.os.ParcelFileDescriptor
import java.io.File

/**
 * @ClassName RomFiles
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
sealed class RomFiles {
    data class Standard(val files: List<File>) : RomFiles()
    data class Virtual(val files: List<Entry>) : RomFiles() {
        data class Entry(val filePath: String, val fd: ParcelFileDescriptor)
    }
}
