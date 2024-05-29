package com.mozhimen.emulatork.libretro.db

import com.mozhimen.basick.utilk.kotlin.filterNullable
import com.mozhimen.emulatork.common.system.SystemBundle
import com.mozhimen.emulatork.basic.system.ESystemType
import com.mozhimen.emulatork.basic.metadata.Metadata
import com.mozhimen.emulatork.basic.metadata.MetadataProvider
import com.mozhimen.emulatork.basic.storage.StorageFile
import com.mozhimen.emulatork.basic.game.system.GameSystems
import com.mozhimen.emulatork.libretro.db.database.LibretroDB
import com.mozhimen.emulatork.libretro.db.database.LibretroDBManager
import com.mozhimen.emulatork.libretro.db.entities.LibretroRom
import timber.log.Timber
import java.util.Locale

/**
 * @ClassName LibretroDBMetadataProvider
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
// TODO We are not currently trying to guess the system using extensions. This is often not reliable, but sometimes it
// might work (think about .n64/.z64)

class MetadataProviderLibretroDB(private val ovgdbManager: LibretroDBManager) : MetadataProvider {
    companion object {
        private val THUMB_REPLACE = Regex("[&*/:`<>?\\\\|]")
    }

    private val sortedSystemIds: List<String> by lazy {
        ESystemType.values()
            .map { it.dbname }
            .sortedByDescending { it.length }
    }

    ////////////////////////////////////////////////////////////////////////////////

    override suspend fun retrieveMetadata(storageFile: StorageFile): Metadata? {
        val db = ovgdbManager.db

        Timber.d("Looking metadata for file: $storageFile")

        val metadata = runCatching {
            findByCRC(storageFile, db)
                ?: findBySerial(storageFile, db)
                ?: findByFilename(db, storageFile)
                ?: findByPathAndFilename(db, storageFile)
                ?: findByUniqueExtension(storageFile)
                ?: findByKnownSystem(storageFile)
                ?: findByPathAndSupportedExtension(storageFile)
        }.getOrElse {
            Timber.e("Error in retrieving $storageFile metadata: $it... Skipping.")
            null
        }

        metadata?.let { Timber.d("Metadata retrieved for item: $it") }

        return metadata
    }

    ////////////////////////////////////////////////////////////////////////////////

    private fun convertToGameMetadata(libretroRom: LibretroRom): Metadata {
        val system = GameSystems.findById(libretroRom.system!!)
        return Metadata(
            name = libretroRom.name,
            romName = libretroRom.romName,
            thumbnail = computeCoverUrl(system, libretroRom.name),
            system = libretroRom.system,
            developer = libretroRom.developer
        )
    }

    private suspend fun findByFilename(db: LibretroDB, file: StorageFile): Metadata? {
        return db.gameDao().findByFileName(file.name)
            .filterNullable { extractGameSystem(it).scanOptions.scanByFilename }
            ?.let { convertToGameMetadata(it) }
    }

    private suspend fun findByPathAndFilename(db: LibretroDB, file: StorageFile): Metadata? {
        return db.gameDao().findByFileName(file.name)
            .filterNullable { extractGameSystem(it).scanOptions.scanByPathAndFilename }
            .filterNullable { parentContainsSystem(file.path, extractGameSystem(it).id.dbname) }
            ?.let { convertToGameMetadata(it) }
    }

    private fun findByPathAndSupportedExtension(file: StorageFile): Metadata? {
        val system = sortedSystemIds
            .filter { parentContainsSystem(file.path, it) }
            .map { GameSystems.findById(it) }
            .filter { it.scanOptions.scanByPathAndSupportedExtensions }
            .firstOrNull { it.supportedExtensions.contains(file.extension) }

        return system?.let {
            Metadata(
                name = file.extensionlessName,
                romName = file.name,
                thumbnail = null,
                system = it.id.dbname,
                developer = null
            )
        }
    }

    private fun parentContainsSystem(parent: String?, dbname: String): Boolean {
        return parent?.toLowerCase(Locale.getDefault())?.contains(dbname) == true
    }

    private suspend fun findByCRC(file: StorageFile, db: LibretroDB): Metadata? {
        if (file.crc == null || file.crc == "0") return null
        return file.crc?.let { crc32 -> db.gameDao().findByCRC(crc32) }
            ?.let { convertToGameMetadata(it) }
    }

    private suspend fun findBySerial(file: StorageFile, db: LibretroDB): Metadata? {
        if (file.serial == null) return null
        return db.gameDao().findBySerial(file.serial!!)
            ?.let { convertToGameMetadata(it) }
    }

    private fun findByKnownSystem(file: StorageFile): Metadata? {
        if (file.systemID == null) return null

        return Metadata(
            name = file.extensionlessName,
            romName = file.name,
            thumbnail = null,
            system = file.systemID!!.dbname,
            developer = null,
        )
    }

    private fun findByUniqueExtension(file: StorageFile): Metadata? {
        val system = GameSystems.findByUniqueFileExtension(file.extension)

        if (system?.scanOptions?.scanByUniqueExtension == false) {
            return null
        }

        val result = system?.let {
            Metadata(
                name = file.extensionlessName,
                romName = file.name,
                thumbnail = null,
                system = it.id.dbname,
                developer = null
            )
        }

        return result
    }

    private fun extractGameSystem(rom: LibretroRom): com.mozhimen.emulatork.common.system.SystemBundle {
        return GameSystems.findById(rom.system!!)
    }

    private fun computeCoverUrl(system: com.mozhimen.emulatork.common.system.SystemBundle, name: String?): String? {
        var systemName = system.libretroFullName

        // Specific mame version don't have any thumbnails in Libretro database
        if (system.id == ESystemType.MAME2003PLUS) {
            systemName = "MAME"
        }

        if (name == null) {
            return null
        }

        val imageType = "Named_Boxarts"

        val thumbGameName = name.replace(THUMB_REPLACE, "_")

        return "http://thumbnails.libretro.com/$systemName/$imageType/$thumbGameName.png"
    }
}
