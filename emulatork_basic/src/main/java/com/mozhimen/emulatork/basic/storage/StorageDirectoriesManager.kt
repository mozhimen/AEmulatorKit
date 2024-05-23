package com.mozhimen.emulatork.basic.storage

import android.content.Context
import java.io.File

/**
 * @ClassName DirectoriesManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
class StorageDirectoriesManager(private val appContext: Context) {
    @Deprecated("Use the external states directory")
    fun getInternalStatesDirectory(): File = File(appContext.filesDir, "states").apply {
        mkdirs()
    }

    fun getCoresDirectory(): File = File(appContext.filesDir, "cores").apply {
        mkdirs()
    }

    fun getSystemDirectory(): File = File(appContext.filesDir, "system").apply {
        mkdirs()
    }

    fun getStatesDirectory(): File = File(appContext.getExternalFilesDir(null), "states").apply {
        mkdirs()
    }

    fun getStatesPreviewDirectory(): File = File(appContext.getExternalFilesDir(null), "state-previews").apply {
        mkdirs()
    }

    fun getSavesDirectory(): File = File(appContext.getExternalFilesDir(null), "saves").apply {
        mkdirs()
    }

    fun getInternalRomsDirectory(): File = File(appContext.getExternalFilesDir(null), "roms").apply {
        mkdirs()
    }
}
