package com.mozhimen.emulatork.libretro.db.daos

import androidx.room.Dao
import androidx.room.Query
import com.mozhimen.emulatork.libretro.db.entities.LibretroRom
import io.reactivex.Maybe

/**
 * @ClassName GameDao
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
@Dao
interface GameDao {

    @Query("SELECT * FROM games WHERE romName = :romName LIMIT 1")
    fun findByFileName(romName: String): Maybe<LibretroRom>

    @Query("SELECT * FROM games WHERE crc32 = :crc LIMIT 1")
    fun findByCRC(crc: String): Maybe<LibretroRom>
}
