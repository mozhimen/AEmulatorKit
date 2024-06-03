package com.mozhimen.emulatork.common.game

import com.mozhimen.emulatork.common.save.SaveState
import com.mozhimen.emulatork.basic.storage.SStorageRomFileType
import com.mozhimen.emulatork.core.property.CoreProperty
import com.mozhimen.emulatork.db.game.entities.Game
import java.io.File

/**
 * @ClassName GameBundle
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/31
 * @Version 1.0
 */
data class GameBundle constructor(
    val game: Game,
    val coreLibrary: String,
    val gameFiles: SStorageRomFileType,
    val quickSaveData: SaveState?,
    val saveRAMData: ByteArray?,
    val coreProperties: Array<CoreProperty>,
    val systemDirectory: File,
    val savesDirectory: File
)