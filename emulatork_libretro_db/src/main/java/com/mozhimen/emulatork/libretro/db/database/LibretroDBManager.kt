package com.mozhimen.emulatork.libretro.db.database

import android.content.Context

/**
 * @ClassName LibretroDBManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class LibretroDBManager(private val context: Context) {
    val db: LibretroDB by lazy { LibretroDB.getLibretroDB() }
}
