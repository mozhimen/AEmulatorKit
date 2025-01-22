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
interface RomDao {
    @Query("SELECT * FROM roms WHERE romName LIKE :romName")
    fun findByName(romName: String): List<LibretroRom>?

    @Query("SELECT * FROM roms WHERE romName = :romName LIMIT 1")
    fun findByFileName(romName: String): LibretroRom?

    @Query("SELECT * FROM roms WHERE crc32 = :crc LIMIT 1")
    fun findByCRC(crc: String): LibretroRom?

    @Query("SELECT * FROM roms WHERE serial = :serial LIMIT 1")
    fun findBySerial(serial: String): LibretroRom?
}