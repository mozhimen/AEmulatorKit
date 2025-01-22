package com.mozhimen.emulatork.db.game.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mozhimen.emulatork.db.game.entities.GameDataFile

/**
 * @ClassName DataFileDao
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
@Dao
interface GameDataFileDao {

    @Query("SELECT * FROM datafiles where gameId = :gameId")
    fun selectDataFilesForGame(gameId: Int): List<GameDataFile>

    @Query("SELECT * FROM datafiles WHERE lastIndexedAt < :lastIndexedAt")
    fun selectByLastIndexedAtLessThan(lastIndexedAt: Long): List<GameDataFile>

    @Insert
    fun insert(gameDataFile: GameDataFile)

    @Insert
    fun insert(gameDataFiles: List<GameDataFile>)

    @Delete
    fun delete(gameDataFiles: List<GameDataFile>)
}
