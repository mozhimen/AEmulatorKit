package com.mozhimen.emulatork.basic.rom

import android.os.ParcelFileDescriptor
import java.io.File

/**
 * @ClassName RomFiles
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
sealed class SRomFileType {
    data class Standard(val files: List<File>) : SRomFileType()
    data class Virtual(val files: List<Entry>) : SRomFileType() {
        data class Entry(val filePath: String, val fileDescriptor: ParcelFileDescriptor)
    }
}
