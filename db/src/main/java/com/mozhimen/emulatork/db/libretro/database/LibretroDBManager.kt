package com.mozhimen.emulatork.db.libretro.database

/**
 * @ClassName LibretroDBManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class LibretroDBManager {
    val db: LibretroDB by lazy { LibretroDB.getLibretroDB() }

    /////////////////////////////////////////////////////////////////////////////////////

    companion object {
        @JvmStatic
        val instance = INSTANCE.holder
    }

    /////////////////////////////////////////////////////////////////////////////////////

    private object INSTANCE {
        val holder = LibretroDBManager()
    }
}
