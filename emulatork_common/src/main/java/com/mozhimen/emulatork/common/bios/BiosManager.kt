package com.mozhimen.emulatork.common.bios

import com.mozhimen.basick.utilk.java.io.deleteFile
import com.mozhimen.basick.utilk.java.io.inputStream2file_use_ofCopyTo
import com.mozhimen.basick.utilk.kotlin.collections.associateByNotNull
import com.mozhimen.emulatork.basic.game.system.GameSystemCoreConfig
import com.mozhimen.emulatork.basic.system.ESystemType
import com.mozhimen.emulatork.basic.game.db.entities.Game
import com.mozhimen.emulatork.basic.storage.StorageProvider
import com.mozhimen.emulatork.basic.storage.StorageFile
import timber.log.Timber
import java.io.File
import java.io.InputStream

/**
 * @ClassName BiosManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
class BiosManager(private val storageProvider: StorageProvider) {

    companion object {
        private val SUPPORTED_BIOS = listOf(
            Bios(
                "scph101.bin",
                "6E3735FF4C7DC899EE98981385F6F3D0",
                "PS One 4.5 NTSC-U/C",
                ESystemType.PSX,
                "171BDCEC"
            ),
            Bios(
                "scph7001.bin",
                "1E68C231D0896B7EADCAD1D7D8E76129",
                "PS Original 4.1 NTSC-U/C",
                ESystemType.PSX,
                "502224B6"
            ),
            Bios(
                "scph5501.bin",
                "490F666E1AFB15B7362B406ED1CEA246",
                "PS Original 3.0 NTSC-U/C",
                ESystemType.PSX,
                "8D8CB7E4"
            ),
            Bios(
                "scph1001.bin",
                "924E392ED05558FFDB115408C263DCCF",
                "PS Original 2.2 NTSC-U/C",
                ESystemType.PSX,
                "37157331"
            ),
            Bios(
                "lynxboot.img",
                "FCD403DB69F54290B51035D82F835E7B",
                "Lynx Boot Image",
                ESystemType.LYNX,
                "0D973C9D"
            ),
            Bios(
                "bios_CD_E.bin",
                "E66FA1DC5820D254611FDCDBA0662372",
                "Sega CD E",
                ESystemType.SEGACD,
                "529AC15A"
            ),
            Bios(
                "bios_CD_J.bin",
                "278A9397D192149E84E820AC621A8EDD",
                "Sega CD J",
                ESystemType.SEGACD,
                "9D2DA8F2"
            ),
            Bios(
                "bios_CD_U.bin",
                "2EFD74E3232FF260E371B99F84024F7F",
                "Sega CD U",
                ESystemType.SEGACD,
                "C6D10268"
            ),
            Bios(
                "bios7.bin",
                "DF692A80A5B1BC90728BC3DFC76CD948",
                "Nintendo DS ARM7",
                ESystemType.NDS,
                "1280F0D5"
            ),
            Bios(
                "bios9.bin",
                "A392174EB3E572FED6447E956BDE4B25",
                "Nintendo DS ARM9",
                ESystemType.NDS,
                "2AB23573"
            ),
            Bios(
                "firmware.bin",
                "E45033D9B0FA6B0DE071292BBA7C9D13",
                "Nintendo DS Firmware",
                ESystemType.NDS,
                "945F9DC9",
                "nds_firmware.bin"
            )
        )
    }

    /////////////////////////////////////////////////////////////////////////////

    private val crcLookup = SUPPORTED_BIOS.associateByNotNull { it.externalCRC32 }
    private val nameLookup = SUPPORTED_BIOS.associateByNotNull { it.externalName }

    /////////////////////////////////////////////////////////////////////////////

    fun getMissingBiosFiles(coreConfig: GameSystemCoreConfig, game: Game): List<String> {
        val regionalBiosFiles = coreConfig.regionalBIOSFiles

        val gameLabels = Regex("\\([A-Za-z]+\\)")
            .findAll(game.title)
            .map { it.value.drop(1).dropLast(1) }
            .filter { it.isNotBlank() }
            .toSet()

        Timber.d("Found game labels: $gameLabels")

        val requiredRegionalFiles = gameLabels.intersect(regionalBiosFiles.keys)
            .ifEmpty { regionalBiosFiles.keys }
            .mapNotNull { regionalBiosFiles[it] }

        Timber.d("Required regional files for game: $requiredRegionalFiles")

        return (coreConfig.requiredBIOSFiles + requiredRegionalFiles)
            .filter { !File(storageProvider.getInternalFileSystem(), it).exists() }
    }

    fun deleteBiosBefore(timestampMs: Long) {
        Timber.i("Pruning old bios files")
        SUPPORTED_BIOS
            .map { File(storageProvider.getInternalFileSystem(), it.libretroFileName) }
            .filter { it.lastModified() < normalizeTimestamp(timestampMs) }
            .forEach {
                Timber.d("Pruning old bios file: ${it.path}")
                it.deleteFile()
            }
    }

    fun getBiosInfo(): BiosInfo {
        val bios = SUPPORTED_BIOS.groupBy {
            File(storageProvider.getInternalFileSystem(), it.libretroFileName).exists()
        }.withDefault { listOf() }

        return BiosInfo(bios.getValue(true), bios.getValue(false))
    }

    fun tryAddBiosAfter(
        storageFile: StorageFile,
        inputStream: InputStream,
        timestampMs: Long
    ): Boolean {
        val bios = findByCRC(storageFile) ?: findByName(storageFile) ?: return false

        Timber.i("Importing bios file: $bios")

        val biosFile = File(storageProvider.getInternalFileSystem(), bios.libretroFileName)
        if (biosFile.exists() && biosFile.setLastModified(normalizeTimestamp(timestampMs))) {
            Timber.d("Bios file already present. Updated last modification date.")
        } else {
            Timber.d("Bios file not available. Copying new file.")
            inputStream.inputStream2file_use_ofCopyTo(biosFile)
        }
        return true
    }

    /////////////////////////////////////////////////////////////////////////////

    private fun findByCRC(storageFile: StorageFile): Bios? {
        return crcLookup[storageFile.crc]
    }

    private fun findByName(storageFile: StorageFile): Bios? {
        return nameLookup[storageFile.name]
    }

    private fun normalizeTimestamp(timestamp: Long) = (timestamp / 1000) * 1000
}
