package com.mozhimen.emulatork.basic.library.db.commons

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mozhimen.emulatork.basic.library.db.mos.DataFile

/**
 * @ClassName DataFileDao
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
@Dao
interface DataFileDao {

    @Query("SELECT * FROM datafiles where gameId = :gameId")
    fun selectDataFilesForGame(gameId: Int): List<DataFile>

    @Query("SELECT * FROM datafiles WHERE lastIndexedAt < :lastIndexedAt")
    fun selectByLastIndexedAtLessThan(lastIndexedAt: Long): List<DataFile>

    @Insert
    fun insert(dataFile: DataFile)

    @Insert
    fun insert(dataFiles: List<DataFile>)

    @Delete
    fun delete(dataFiles: List<DataFile>)
}
