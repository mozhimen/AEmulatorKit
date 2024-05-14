package com.mozhimen.emulatork.libretro.db

import android.content.Context
import androidx.room.Room

/**
 * @ClassName LibretroDBManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class LibretroDBManager(private val context: Context) {

    companion object {
        private const val DB_NAME = "libretro-db"
    }

    val dbInstance: LibretroDatabase by lazy {
        Room.databaseBuilder(context, LibretroDatabase::class.java, DB_NAME)
            .createFromAsset("libretro-db.sqlite")
            .fallbackToDestructiveMigration()
            .build()
    }
}
