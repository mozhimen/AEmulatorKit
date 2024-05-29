package com.mozhimen.emulatork.common.game.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mozhimen.emulatork.basic.game.db.daos.DataFileDao
import com.mozhimen.emulatork.basic.game.db.daos.GameDao
import com.mozhimen.emulatork.basic.game.db.daos.GameSearchDao
import com.mozhimen.emulatork.basic.game.db.entities.DataFile
import com.mozhimen.emulatork.basic.game.db.entities.Game

/**
 * @ClassName RetrogradeDatabase
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/7
 * @Version 1.0
 */
@Database(
    entities = [Game::class, DataFile::class],
    version = 9,
    exportSchema = false
)
abstract class RetrogradeDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "retrograde"
    }

    abstract fun gameDao(): GameDao

    abstract fun dataFileDao(): DataFileDao

    fun gameSearchDao() = GameSearchDao(gameSearchDaoInternal())

    protected abstract fun gameSearchDaoInternal(): GameSearchDao.Internal
}
