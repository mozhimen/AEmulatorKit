package com.mozhimen.emulatork.db.libretro.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mozhimen.basick.utilk.android.app.UtilKApplicationWrapper
import com.mozhimen.emulatork.db.libretro.daos.RomDao
import com.mozhimen.emulatork.db.libretro.entities.LibretroRom

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
    abstract fun gameDao(): RomDao

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
