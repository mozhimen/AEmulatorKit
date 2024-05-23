package com.mozhimen.emulatork.basic.game.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * @ClassName DataFile
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
@Entity(
    tableName = "datafiles",
    foreignKeys = [
        ForeignKey(
            entity = Game::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("gameId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("id", unique = true),
        Index("fileUri"),
        Index("gameId"),
        Index("lastIndexedAt")
    ]
)
data class DataFile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val gameId: Int,
    val fileName: String,
    val fileUri: String,
    val lastIndexedAt: Long,
    val path: String?
) : Serializable
