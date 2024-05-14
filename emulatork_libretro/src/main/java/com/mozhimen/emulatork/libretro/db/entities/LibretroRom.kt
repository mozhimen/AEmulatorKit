package com.mozhimen.emulatork.libretro.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * @ClassName LibretroRom
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
@Entity(
    tableName = "games",
    indices = [
        Index("romName"),
        Index("crc32"),
        Index("serial")
    ]
)
data class LibretroRom(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "system")
    val system: String?,

    @ColumnInfo(name = "romName")
    val romName: String?,

    @ColumnInfo(name = "developer")
    val developer: String?,

    @ColumnInfo(name = "crc32")
    val crc32: String?,

    @ColumnInfo(name = "serial")
    val serial: String?
)
