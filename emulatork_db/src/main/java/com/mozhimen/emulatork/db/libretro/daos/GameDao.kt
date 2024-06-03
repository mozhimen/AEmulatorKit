package com.mozhimen.emulatork.db.libretro.daos

import androidx.room.Dao
import androidx.room.Query
import com.mozhimen.emulatork.db.libretro.entities.LibretroRom

/**
 * @ClassName GameDao
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
@Dao
interface GameDao {
    @Query("SELECT * FROM games WHERE romName LIKE :romName")
    fun findByName(romName: String): List<LibretroRom>?

    @Query("SELECT * FROM games WHERE romName = :romName LIMIT 1")
    fun findByFileName(romName: String): LibretroRom?

    @Query("SELECT * FROM games WHERE crc32 = :crc LIMIT 1")
    fun findByCRC(crc: String): LibretroRom?

    @Query("SELECT * FROM games WHERE serial = :serial LIMIT 1")
    fun findBySerial(serial: String): LibretroRom?
}