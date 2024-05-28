package com.mozhimen.emulatork.libretro.db.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mozhimen.basick.utilk.android.app.UtilKApplicationWrapper
import com.mozhimen.emulatork.libretro.db.daos.GameDao
import com.mozhimen.emulatork.libretro.db.entities.LibretroRom

/**
 * @ClassName LibretroDatabase
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
@Database(
    entities = [LibretroRom::class],
    version = 8,
    exportSchema = false
)
abstract class LibretroDB : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object{
        private const val DB_NAME = "libretro-db"

        @JvmStatic
        fun getLibretroDB(): LibretroDB =
            Room.databaseBuilder(UtilKApplicationWrapper.instance.applicationContext, LibretroDB::class.java, DB_NAME)
                .createFromAsset("libretro-db.sqlite")
                .fallbackToDestructiveMigration()
                .build()
    }
}
