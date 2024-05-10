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
class DirectoriesManager(private val appContext: Context) {

    fun getStatesDirectory() = File(appContext.filesDir, "states").apply {
        mkdirs()
    }

    fun getCoresDirectory() = File(appContext.filesDir, "cores").apply {
        mkdirs()
    }

    fun getSystemDirectory() = File(appContext.filesDir, "system").apply {
        mkdirs()
    }

    fun getSavesDirectory() = File(appContext.filesDir, "saves").apply {
        mkdirs()
    }
}
