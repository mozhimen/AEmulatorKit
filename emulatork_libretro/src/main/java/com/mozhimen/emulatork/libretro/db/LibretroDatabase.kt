package com.mozhimen.emulatork.libretro.db

import androidx.room.Database
import androidx.room.RoomDatabase
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
    version = 1,
    exportSchema = false)
abstract class LibretroDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}
