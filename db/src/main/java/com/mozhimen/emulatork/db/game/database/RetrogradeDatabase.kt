package com.mozhimen.emulatork.db.game.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mozhimen.basick.utilk.android.app.UtilKApplicationWrapper
import com.mozhimen.emulatork.db.game.daos.GameDataFileDao
import com.mozhimen.emulatork.db.game.daos.GameDao
import com.mozhimen.emulatork.db.game.daos.GameSearchDao
import com.mozhimen.emulatork.db.game.entities.Game
import com.mozhimen.emulatork.db.game.entities.GameDataFile
import com.mozhimen.emulatork.db.game.helpers.Migrations

/**
 * @ClassName RetrogradeDatabase
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/7
 * @Version 1.0
 */
@Database(
    entities = [Game::class, GameDataFile::class],
    version = 9,
    exportSchema = false
)
abstract class RetrogradeDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "retrograde"

        @JvmStatic
        fun getRetrogradeDb():RetrogradeDatabase =
            Room.databaseBuilder(UtilKApplicationWrapper.instance.applicationContext,RetrogradeDatabase::class.java, DB_NAME)
                .addCallback(Migrations.CALLBACK)
                .addMigrations(Migrations.VERSION_7_8, Migrations.VERSION_8_9)
                .fallbackToDestructiveMigration()
                .build()
    }

    ////////////////////////////////////////////////////////////////////////

    abstract fun gameDao(): GameDao

    abstract fun dataFileDao(): GameDataFileDao

    fun gameSearchDao() = GameSearchDao(gameSearchDaoInternal())

    protected abstract fun gameSearchDaoInternal(): GameSearchDao.Internal
}
