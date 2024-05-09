package com.mozhimen.gamek.emulator.basic.library.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mozhimen.gamek.emulator.basic.library.db.commons.GameDao
import com.mozhimen.gamek.emulator.basic.library.db.commons.GameSearchDao
import com.mozhimen.gamek.emulator.basic.library.db.helpers.Converters
import com.mozhimen.gamek.emulator.basic.library.db.mos.Game

/**
 * @ClassName RetrogradeDatabase
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/7
 * @Version 1.0
 */
@Database(
    entities = [Game::class],
    version = 8,
    exportSchema = true)
@TypeConverters(Converters::class)
abstract class RetrogradeDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "retrograde"
    }

    abstract fun gameDao(): GameDao

    fun gameSearchDao() = GameSearchDao(gameSearchDaoInternal())

    protected abstract fun gameSearchDaoInternal(): GameSearchDao.Internal
}