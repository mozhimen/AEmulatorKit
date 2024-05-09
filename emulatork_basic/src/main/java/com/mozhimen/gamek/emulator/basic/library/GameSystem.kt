package com.mozhimen.gamek.emulator.basic.library

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.mozhimen.gamek.emulator.basic.R
import java.util.Locale

/**
 * @ClassName GameSystem
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
data class GameSystem(
    val id: String,

    @StringRes
    val titleResId: Int,

    @StringRes
    val shortTitleResId: Int,

    @DrawableRes
    val imageResId: Int,

    val sortKey: String,

    val coreFileName: String,

    val supportedExtensions: List<String>
) {

    companion object {
        const val NES_ID = "nes"
        const val SNES_ID = "snes"
        const val GENESIS_ID = "md"
        const val GB_ID = "gb"
        const val GBC_ID = "gbc"
        const val GBA_ID = "gba"
        const val N64_ID = "n64"
        const val ARCADE_ID = "arcade"

        private val SYSTEMS = listOf(
            GameSystem(
                NES_ID,
                R.string.game_system_title_nes,
                R.string.game_system_abbr_nes,
                R.drawable.game_system_nes,
                "nintendo0",
                "fceumm_libretro_android.so.zip",
                listOf("nes")
            ),
            GameSystem(
                SNES_ID,
                R.string.game_system_title_snes,
                R.string.game_system_abbr_snes,
                R.drawable.game_system_snes,
                "nintendo1",
                "snes9x_libretro_android.so.zip",
                listOf("smc", "sfc", "swc", "fig")
            ),
            GameSystem(
                GENESIS_ID,
                R.string.game_system_title_genesis,
                R.string.game_system_abbr_genesis,
                R.drawable.game_system_genesis,
                "sega0",
                "picodrive_libretro_android.so.zip",
                listOf("gen", "smd", "md")
            ),
            GameSystem(
                GB_ID,
                R.string.game_system_title_gb,
                R.string.game_system_abbr_gb,
                R.drawable.game_system_gb,
                "nintendo2",
                "gambatte_libretro_android.so.zip",
                listOf("gb")

            ),
            GameSystem(
                GBC_ID,
                R.string.game_system_title_gbc,
                R.string.game_system_abbr_gbc,
                R.drawable.game_system_gbc,
                "nintendo3",
                "gambatte_libretro_android.so.zip",
                listOf("gbc")
            ),
            GameSystem(
                GBA_ID,
                R.string.game_system_title_gba,
                R.string.game_system_abbr_gba,
                R.drawable.game_system_gba,
                "nintendo4",
                "mgba_libretro_android.so.zip",
                listOf("gba")
            ),
            GameSystem(
                N64_ID,
                R.string.game_system_title_n64,
                R.string.game_system_abbr_n64,
                R.drawable.game_system_n64,
                "nintendo5",
                "mupen64plus_next_libretro_android.so.zip",
                listOf("n64", "z64")
            )
            // We are currently disabling MAME emulation, since it's a bit of a mess to handle romsets versions.
            /*GameSystem(
                    ARCADE_ID,
                    R.string.game_system_title_arcade,
                    R.string.game_system_abbr_arcade,
                    R.drawable.game_system_arcade,
                    "arcade",
                    "fbneo_libretro_android.so.zip",
                    listOf("zip")
            )*/
        )

        private val byIdCache by lazy { mapOf(*SYSTEMS.map { it.id to it }.toTypedArray()) }
        private val byExtensionCache by lazy {
            val mutableMap = mutableMapOf<String, GameSystem>()
            for (system in SYSTEMS) {
                for (extension in system.supportedExtensions) {
                    mutableMap[extension.toLowerCase(Locale.US)] = system
                }
            }
            mutableMap.toMap()
        }

        fun findById(id: String): GameSystem? = byIdCache[id]

        fun findByShortName(shortName: String): GameSystem? =
            findById(shortName.toLowerCase())

        fun findByFileExtension(fileExtension: String): GameSystem? =
            byExtensionCache[fileExtension.toLowerCase(Locale.US)]
    }
}
