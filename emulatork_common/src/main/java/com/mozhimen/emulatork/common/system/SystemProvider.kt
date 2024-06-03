package com.mozhimen.emulatork.common.system

import android.content.Context
import com.mozhimen.emulatork.common.R
import com.mozhimen.emulatork.basic.system.ESystemType
import com.mozhimen.emulatork.basic.system.SystemProperty
import com.mozhimen.emulatork.basic.system.SystemSetting
import com.mozhimen.emulatork.common.core.CoreBundle
import com.mozhimen.emulatork.basic.system.SystemScanOption
import com.mozhimen.emulatork.core.ECoreType
import com.mozhimen.emulatork.core.property.CoreProperty
import com.mozhimen.emulatork.db.game.entities.Game
import com.mozhimen.emulatork.input.virtual.gamepad.GamepadConfigProvider
import java.util.Locale

/**
 * @ClassName GameSystemProvider
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/30
 * @Version 1.0
 */
object SystemProvider {
    private val SYSTEMS: List<SystemBundle> by lazy {
        listOf(
            getATARI2600(),
            getNES(),
            getSNES(),
            getSMS(),
            getGENESIS(),
            getSEGACD(),
            getGG(),
            getGB(),
            getGBC(),
            getGBA(),
            getN64(),
            getPSX(),
            getPSP(),
            getFBNEO(),
            getMAME2003PLUS(),
            getNDS(),
            getATARI7800(),
            getLYNX(),
            getPC_ENGINE(),
            getNGP(),
            getNGC(),
            getWS(),
            getWSC(),
            getDOS(),
            getNINTENDO_3DS(),
        )
    }

    private val SYSTEM_MAP_NAME: Map<String, SystemBundle> by lazy {
        mapOf(*getSystems().map { it.eSystemType.simpleName to it }.toTypedArray())
    }

    private val SYSTEM_MAP_ROM_EXTENSION by lazy {
        val mutableMap = mutableMapOf<String, SystemBundle>()
        for (system in getSystems()) {
            for (extension in system.uniqueFileExtNames) {
                mutableMap[extension.toLowerCase(Locale.US)] = system
            }
        }
        mutableMap.toMap()
    }

    /////////////////////////////////////////////////////////////////////////////////

    fun getSupportRomExtNames(): List<String> =
        SYSTEMS.flatMap { it.supportRomExtNames }

    fun findSysByName(name: String): SystemBundle =
        SYSTEM_MAP_NAME.getValue(name)

    fun findSysByCoreType(eCoreType: ECoreType): List<SystemBundle> =
        getSystems().filter { system -> system.coreBundles.any { it.eCoreType == eCoreType } }

    fun findSysByUniqueFileExtName(uniqueFileExtName: String): SystemBundle? =
        SYSTEM_MAP_ROM_EXTENSION[uniqueFileExtName.toLowerCase(Locale.US)]

    fun getGameSubtitle(context: Context, game: Game): String {
        val systemName = context.getString(findSysByName(game.systemName).shortTitleResId)
        val developerName =
            if (game.developer?.isNotBlank() == true)
                "- ${game.developer}"
            else ""
        return "$systemName $developerName"
    }

    /////////////////////////////////////////////////////////////////////////////////

    fun getSystems(): List<SystemBundle> =
        SYSTEMS

    @JvmStatic
    fun getATARI2600(): SystemBundle =
        SystemBundle(
            ESystemType.ATARI2600,
            "Atari - 2600",
            com.mozhimen.emulatork.basic.R.string.game_system_title_atari2600,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_atari2600,
            listOf(
                CoreBundle(
                    eCoreType = ECoreType.STELLA,
                    systemSettings_exposed = listOf(
                        SystemSetting(
                            "stella_filter",
                            R.string.setting_stella_filter,
                            arrayListOf(
                                SystemProperty(
                                    "disabled",
                                    R.string.value_stella_filter_disabled
                                ),
                                SystemProperty(
                                    "composite",
                                    R.string.value_stella_filter_composite
                                ),
                                SystemProperty(
                                    "s-video",
                                    R.string.value_stella_filter_svideo
                                ),
                                SystemProperty("rgb", R.string.value_stella_filter_rgb),
                                SystemProperty(
                                    "badly adjusted",
                                    R.string.value_stella_filter_badlyadjusted
                                ),
                            )
                        ),
                        SystemSetting(
                            "stella_crop_hoverscan",
                            R.string.setting_stella_crop_hoverscan
                        )
                    ),
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getATARI_2600())
                    )
                )
            ),
            uniqueFileExtNames = listOf("a26"),
        )

    @JvmStatic
    fun getNES(): SystemBundle =
        SystemBundle(
            ESystemType.NES,
            "Nintendo - Nintendo Entertainment System",
            com.mozhimen.emulatork.basic.R.string.game_system_title_nes,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_nes,
            listOf(
                CoreBundle(
                    ECoreType.FCEUMM,
                    systemSettings_exposed = listOf(
                        SystemSetting(
                            "fceumm_overscan_h",
                            R.string.setting_fceumm_overscan_h
                        ),
                        SystemSetting(
                            "fceumm_overscan_v",
                            R.string.setting_fceumm_overscan_v
                        ),
                    ),
                    systemSettings_exposedAdvanced = listOf(
                        SystemSetting(
                            "fceumm_nospritelimit",
                            R.string.setting_fceumm_nospritelimit
                        ),
                    ),
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getNES())
                    ),
                )
            ),
            uniqueFileExtNames = listOf("nes"),
        )

    @JvmStatic
    fun getSNES(): SystemBundle =
        SystemBundle(
            ESystemType.SNES,
            "Nintendo - Super Nintendo Entertainment System",
            com.mozhimen.emulatork.basic.R.string.game_system_title_snes,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_snes,
            listOf(
                CoreBundle(
                    ECoreType.SNES9X,
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getSNES())
                    )
                )
            ),
            uniqueFileExtNames = listOf("smc", "sfc"),
        )

    @JvmStatic
    fun getSMS(): SystemBundle =
        SystemBundle(
            ESystemType.SMS,
            "Sega - Master System - Mark III",
            com.mozhimen.emulatork.basic.R.string.game_system_title_sms,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_sms,
            listOf(
                CoreBundle(
                    ECoreType.GENESIS_PLUS_GX,
                    systemSettings_exposed = listOf(
                        SystemSetting(
                            "genesis_plus_gx_blargg_ntsc_filter",
                            R.string.setting_genesis_plus_gx_blargg_ntsc_filter,
                            arrayListOf(
                                SystemProperty(
                                    "disabled",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_disabled
                                ),
                                SystemProperty(
                                    "monochrome",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_monochrome
                                ),
                                SystemProperty(
                                    "composite",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_composite
                                ),
                                SystemProperty(
                                    "svideo",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_svideo
                                ),
                                SystemProperty(
                                    "rgb",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_rgb
                                ),
                            )
                        )
                    ),
                    systemSettings_exposedAdvanced = listOf(
                        SystemSetting(
                            "genesis_plus_gx_no_sprite_limit",
                            R.string.setting_genesis_plus_gx_no_sprite_limit
                        ),
                        SystemSetting(
                            "genesis_plus_gx_overscan",
                            R.string.setting_genesis_plus_gx_overscan,
                            arrayListOf(
                                SystemProperty(
                                    "disabled",
                                    R.string.value_genesis_plus_gx_overscan_disabled
                                ),
                                SystemProperty(
                                    "top/bottom",
                                    R.string.value_genesis_plus_gx_overscan_topbottom
                                ),
                                SystemProperty(
                                    "left/right",
                                    R.string.value_genesis_plus_gx_overscan_leftright
                                ),
                                SystemProperty(
                                    "full",
                                    R.string.value_genesis_plus_gx_overscan_full
                                ),
                            )
                        )
                    ),
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getSMS())
                    )
                )
            ),
            uniqueFileExtNames = listOf("sms"),
        )

    @JvmStatic
    fun getGENESIS(): SystemBundle =
        SystemBundle(
            ESystemType.GENESIS,
            "Sega - Mega Drive - Genesis",
            com.mozhimen.emulatork.basic.R.string.game_system_title_genesis,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_genesis,
            listOf(
                CoreBundle(
                    ECoreType.GENESIS_PLUS_GX,
                    systemSettings_exposed = listOf(
                        SystemSetting(
                            "genesis_plus_gx_blargg_ntsc_filter",
                            R.string.setting_genesis_plus_gx_blargg_ntsc_filter,
                            arrayListOf(
                                SystemProperty(
                                    "disabled",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_disabled
                                ),
                                SystemProperty(
                                    "monochrome",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_monochrome
                                ),
                                SystemProperty(
                                    "composite",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_composite
                                ),
                                SystemProperty(
                                    "svideo",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_svideo
                                ),
                                SystemProperty(
                                    "rgb",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_rgb
                                ),
                            )
                        )
                    ),
                    systemSettings_exposedAdvanced = listOf(
                        SystemSetting(
                            "genesis_plus_gx_no_sprite_limit",
                            R.string.setting_genesis_plus_gx_no_sprite_limit
                        ),
                        SystemSetting(
                            "genesis_plus_gx_overscan",
                            R.string.setting_genesis_plus_gx_overscan,
                            arrayListOf(
                                SystemProperty(
                                    "disabled",
                                    R.string.value_genesis_plus_gx_overscan_disabled
                                ),
                                SystemProperty(
                                    "top/bottom",
                                    R.string.value_genesis_plus_gx_overscan_topbottom
                                ),
                                SystemProperty(
                                    "left/right",
                                    R.string.value_genesis_plus_gx_overscan_leftright
                                ),
                                SystemProperty(
                                    "full",
                                    R.string.value_genesis_plus_gx_overscan_full
                                ),
                            )
                        )
                    ),
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(
                            GamepadConfigProvider.getGENESIS_3(),
                            GamepadConfigProvider.getGENESIS_6()
                        ),
                        1 to arrayListOf(
                            GamepadConfigProvider.getGENESIS_3(),
                            GamepadConfigProvider.getGENESIS_6()
                        ),
                        2 to arrayListOf(
                            GamepadConfigProvider.getGENESIS_3(),
                            GamepadConfigProvider.getGENESIS_6()
                        ),
                        3 to arrayListOf(
                            GamepadConfigProvider.getGENESIS_3(),
                            GamepadConfigProvider.getGENESIS_6()
                        )
                    )
                )
            ),
            uniqueFileExtNames = listOf("gen", "smd", "md"),
        )

    @JvmStatic
    fun getSEGACD(): SystemBundle =
        SystemBundle(
            ESystemType.SEGACD,
            "Sega - Mega-CD - Sega CD",
            com.mozhimen.emulatork.basic.R.string.game_system_title_scd,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_scd,
            listOf(
                CoreBundle(
                    ECoreType.GENESIS_PLUS_GX,
                    systemSettings_exposed = listOf(
                        SystemSetting(
                            "genesis_plus_gx_blargg_ntsc_filter",
                            R.string.setting_genesis_plus_gx_blargg_ntsc_filter,
                            arrayListOf(
                                SystemProperty(
                                    "disabled",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_disabled
                                ),
                                SystemProperty(
                                    "monochrome",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_monochrome
                                ),
                                SystemProperty(
                                    "composite",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_composite
                                ),
                                SystemProperty(
                                    "svideo",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_svideo
                                ),
                                SystemProperty(
                                    "rgb",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_rgb
                                ),
                            )
                        )
                    ),
                    systemSettings_exposedAdvanced = listOf(
                        SystemSetting(
                            "genesis_plus_gx_no_sprite_limit",
                            R.string.setting_genesis_plus_gx_no_sprite_limit
                        ),
                        SystemSetting(
                            "genesis_plus_gx_overscan",
                            R.string.setting_genesis_plus_gx_overscan,
                            arrayListOf(
                                SystemProperty(
                                    "disabled",
                                    R.string.value_genesis_plus_gx_overscan_disabled
                                ),
                                SystemProperty(
                                    "top/bottom",
                                    R.string.value_genesis_plus_gx_overscan_topbottom
                                ),
                                SystemProperty(
                                    "left/right",
                                    R.string.value_genesis_plus_gx_overscan_leftright
                                ),
                                SystemProperty(
                                    "full",
                                    R.string.value_genesis_plus_gx_overscan_full
                                ),
                            )
                        )
                    ),
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(
                            GamepadConfigProvider.getGENESIS_3(),
                            GamepadConfigProvider.getGENESIS_6()
                        ),
                        1 to arrayListOf(
                            GamepadConfigProvider.getGENESIS_3(),
                            GamepadConfigProvider.getGENESIS_6()
                        ),
                        2 to arrayListOf(
                            GamepadConfigProvider.getGENESIS_3(),
                            GamepadConfigProvider.getGENESIS_6()
                        ),
                        3 to arrayListOf(
                            GamepadConfigProvider.getGENESIS_3(),
                            GamepadConfigProvider.getGENESIS_6()
                        )
                    ),
                    regionalBIOSFiles = mapOf(
                        "Europe" to "bios_CD_E.bin",
                        "Japan" to "bios_CD_J.bin",
                        "USA" to "bios_CD_U.bin"
                    ),
                )
            ),
            systemScanOption = SystemScanOption(
                scanByFilename = false,
                scanByUniqueExtension = false,
                scanByPathAndSupportedExtensions = true,
                scanBySimilarSerial = true
            ),
            uniqueFileExtNames = listOf(),
            supportRomExtNames = listOf("cue", "iso", "chd"),
        )

    @JvmStatic
    fun getGG(): SystemBundle =
        SystemBundle(
            ESystemType.GG,
            "Sega - Game Gear",
            com.mozhimen.emulatork.basic.R.string.game_system_title_gg,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_gg,
            listOf(
                CoreBundle(
                    ECoreType.GENESIS_PLUS_GX,
                    systemSettings_exposed = listOf(
                        SystemSetting(
                            "genesis_plus_gx_lcd_filter",
                            R.string.setting_genesis_plus_gx_lcd_filter
                        )
                    ),
                    systemSettings_exposedAdvanced = listOf(
                        SystemSetting(
                            "genesis_plus_gx_no_sprite_limit",
                            R.string.setting_genesis_plus_gx_no_sprite_limit
                        ),
                    ),
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getGG())
                    )
                )
            ),
            uniqueFileExtNames = listOf("gg"),
        )

    @JvmStatic
    fun getGB(): SystemBundle =
        SystemBundle(
            ESystemType.GB,
            "Nintendo - Game Boy",
            com.mozhimen.emulatork.basic.R.string.game_system_title_gb,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_gb,
            listOf(
                CoreBundle(
                    ECoreType.GAMBATTE,
                    systemSettings_exposed = listOf(
                        SystemSetting(
                            "gambatte_gb_colorization",
                            R.string.setting_gambatte_gb_colorization
                        ),
                        SystemSetting(
                            "gambatte_gb_internal_palette",
                            R.string.setting_gambatte_gb_internal_palette
                        ),
                        SystemSetting(
                            "gambatte_mix_frames",
                            R.string.setting_gambatte_mix_frames,
                            arrayListOf(
                                SystemProperty(
                                    "disabled",
                                    R.string.value_gambatte_mix_frames_disabled
                                ),
                                SystemProperty(
                                    "mix",
                                    R.string.value_gambatte_mix_frames_mix
                                ),
                                SystemProperty(
                                    "lcd_ghosting",
                                    R.string.value_gambatte_mix_frames_lcd_ghosting
                                ),
                                SystemProperty(
                                    "lcd_ghosting_fast",
                                    R.string.value_gambatte_mix_frames_lcd_ghosting_fast
                                ),
                            )
                        ),
                        SystemSetting(
                            "gambatte_dark_filter_level",
                            R.string.setting_gambatte_dark_filter_level
                        )
                    ),
                    coreProperties = listOf(
                        CoreProperty("gambatte_gb_colorization", "internal"),
                        CoreProperty("gambatte_gb_internal_palette", "GB - Pocket")
                    ),
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getGB())
                    )
                ),
            ),
            uniqueFileExtNames = listOf("gb"),
        )

    @JvmStatic
    fun getGBC(): SystemBundle =
        SystemBundle(
            ESystemType.GBC,
            "Nintendo - Game Boy Color",
            com.mozhimen.emulatork.basic.R.string.game_system_title_gbc,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_gbc,
            listOf(
                CoreBundle(
                    ECoreType.GAMBATTE,
                    systemSettings_exposed = listOf(
                        SystemSetting(
                            "gambatte_mix_frames",
                            R.string.setting_gambatte_mix_frames,
                            arrayListOf(
                                SystemProperty(
                                    "disabled",
                                    R.string.value_gambatte_mix_frames_disabled
                                ),
                                SystemProperty(
                                    "mix",
                                    R.string.value_gambatte_mix_frames_mix
                                ),
                                SystemProperty(
                                    "lcd_ghosting",
                                    R.string.value_gambatte_mix_frames_lcd_ghosting
                                ),
                                SystemProperty(
                                    "lcd_ghosting_fast",
                                    R.string.value_gambatte_mix_frames_lcd_ghosting_fast
                                ),
                            )
                        ),
                        SystemSetting(
                            "gambatte_gbc_color_correction",
                            R.string.setting_gambatte_gbc_color_correction,
                            arrayListOf(
                                SystemProperty(
                                    "disabled",
                                    R.string.value_gambatte_gbc_color_correction_disabled
                                ),
                                SystemProperty(
                                    "always",
                                    R.string.value_gambatte_gbc_color_correction_always
                                )
                            )
                        ),
                        SystemSetting(
                            "gambatte_dark_filter_level",
                            R.string.setting_gambatte_dark_filter_level
                        )
                    ),
                    isSupportRumble = true,
                    coreProperties = listOf(
                        CoreProperty("gambatte_gbc_color_correction", "disabled"),
                    ),
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getGB())
                    )
                ),
            ),
            uniqueFileExtNames = listOf("gbc"),
        )

    @JvmStatic
    fun getGBA(): SystemBundle =
        SystemBundle(
            ESystemType.GBA,
            "Nintendo - Game Boy Advance",
            com.mozhimen.emulatork.basic.R.string.game_system_title_gba,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_gba,
            listOf(
                CoreBundle(
                    ECoreType.MGBA,
                    systemSettings_exposed = listOf(
                        SystemSetting(
                            "mgba_solar_sensor_level",
                            R.string.setting_mgba_solar_sensor_level
                        ),
                        SystemSetting(
                            "mgba_interframe_blending",
                            R.string.setting_mgba_interframe_blending,
                            arrayListOf(
                                SystemProperty(
                                    "OFF",
                                    R.string.value_mgba_interframe_blending_off
                                ),
                                SystemProperty(
                                    "mix",
                                    R.string.value_mgba_interframe_blending_mix
                                ),
                                SystemProperty(
                                    "lcd_ghosting",
                                    R.string.value_mgba_interframe_blending_lcd_ghosting
                                ),
                                SystemProperty(
                                    "lcd_ghosting_fast",
                                    R.string.value_mgba_interframe_blending_lcd_ghosting_fast
                                ),
                            )
                        ),
                        SystemSetting(
                            "mgba_frameskip",
                            R.string.setting_mgba_frameskip,
                            arrayListOf(
                                SystemProperty(
                                    "disabled",
                                    R.string.value_mgba_frameskip_disabled
                                ),
                                SystemProperty("auto", R.string.value_mgba_frameskip_auto)
                            )
                        ),
                        SystemSetting(
                            "mgba_color_correction",
                            R.string.setting_mgba_color_correction,
                            arrayListOf(
                                SystemProperty(
                                    "OFF",
                                    R.string.value_mgba_color_correction_off
                                ),
                                SystemProperty(
                                    "GBA",
                                    R.string.value_mgba_color_correction_gba
                                )
                            )
                        ),
                    ),
                    isSupportRumble = true,
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getGBA())
                    )
                ),
            ),
            uniqueFileExtNames = listOf("gba"),
        )

    @JvmStatic
    fun getN64(): SystemBundle =
        SystemBundle(
            ESystemType.N64,
            "Nintendo - Nintendo 64",
            com.mozhimen.emulatork.basic.R.string.game_system_title_n64,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_n64,
            listOf(
                CoreBundle(
                    ECoreType.MUPEN64_PLUS_NEXT,
                    systemSettings_exposed = listOf(
                        SystemSetting(
                            "mupen64plus-43screensize",
                            R.string.setting_mupen64plus_43screensize
                        ),
                        SystemSetting(
                            "mupen64plus-cpucore",
                            R.string.setting_mupen64plus_cpucore,
                            arrayListOf(
                                SystemProperty(
                                    "dynamic_recompiler",
                                    R.string.value_mupen64plus_cpucore_dynamicrecompiler
                                ),
                                SystemProperty(
                                    "pure_interpreter",
                                    R.string.value_mupen64plus_cpucore_pureinterpreter
                                ),
                                SystemProperty(
                                    "cached_interpreter",
                                    R.string.value_mupen64plus_cpucore_cachedinterpreter
                                ),
                            )
                        ),
                        SystemSetting(
                            "mupen64plus-BilinearMode",
                            R.string.setting_mupen64plus_BilinearMode,
                            arrayListOf(
                                SystemProperty(
                                    "standard",
                                    R.string.value_mupen64plus_bilinearmode_standard
                                ),
                                SystemProperty(
                                    "3point",
                                    R.string.value_mupen64plus_bilinearmode_3point
                                ),
                            )
                        ),
                        SystemSetting(
                            "mupen64plus-pak1",
                            R.string.setting_mupen64plus_pak1,
                            arrayListOf(
                                SystemProperty(
                                    "memory",
                                    R.string.value_mupen64plus_mupen64plus_pak1_memory
                                ),
                                SystemProperty(
                                    "rumble",
                                    R.string.value_mupen64plus_mupen64plus_pak1_rumble
                                ),
                                SystemProperty(
                                    "none",
                                    R.string.value_mupen64plus_mupen64plus_pak1_none
                                )
                            )
                        ),
                        SystemSetting(
                            "mupen64plus-pak2",
                            R.string.setting_mupen64plus_pak2,
                            arrayListOf(
                                SystemProperty(
                                    "none",
                                    R.string.value_mupen64plus_mupen64plus_pak2_none
                                ),
                                SystemProperty(
                                    "rumble",
                                    R.string.value_mupen64plus_mupen64plus_pak2_rumble
                                )
                            )
                        )
                    ),
                    coreProperties = listOf(
                        CoreProperty("mupen64plus-43screensize", "320x240"),
                        CoreProperty("mupen64plus-FrameDuping", "True")
                    ),
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getN64())
                    ),
                    isSupportRumble = true,
                    skipDuplicateFrames = false
                )
            ),
            uniqueFileExtNames = listOf("n64", "z64"),
        )

    @JvmStatic
    fun getPSX(): SystemBundle =
        SystemBundle(
            ESystemType.PSX,
            "Sony - PlayStation",
            com.mozhimen.emulatork.basic.R.string.game_system_title_psx,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_psx,
            listOf(
                CoreBundle(
                    ECoreType.PCSX_REARMED,
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(
                            GamepadConfigProvider.getPSX_STANDARD(),
                            GamepadConfigProvider.getPSX_DUALSHOCK()
                        ),
                        1 to arrayListOf(
                            GamepadConfigProvider.getPSX_STANDARD(),
                            GamepadConfigProvider.getPSX_DUALSHOCK()
                        ),
                        2 to arrayListOf(
                            GamepadConfigProvider.getPSX_STANDARD(),
                            GamepadConfigProvider.getPSX_DUALSHOCK()
                        ),
                        3 to arrayListOf(
                            GamepadConfigProvider.getPSX_STANDARD(),
                            GamepadConfigProvider.getPSX_DUALSHOCK()
                        ),
                    ),
                    systemSettings_exposed = listOf(
                        SystemSetting(
                            "pcsx_rearmed_frameskip",
                            R.string.setting_pcsx_rearmed_frameskip
                        )
                    ),
                    systemSettings_exposedAdvanced = listOf(
                        SystemSetting(
                            "pcsx_rearmed_drc",
                            R.string.setting_pcsx_rearmed_drc
                        )
                    ),
                    coreProperties = listOf(
                        CoreProperty("pcsx_rearmed_drc", "disabled"),
                        CoreProperty("pcsx_rearmed_duping_enable", "enabled"),
                    ),
                    isSupportRumble = true,
                    supportsLibretroVFS = true,
                    skipDuplicateFrames = false
                )
            ),
            uniqueFileExtNames = listOf(),
            supportRomExtNames = listOf("iso", "pbp", "chd", "cue", "m3u"),
            systemScanOption = SystemScanOption(
                scanByFilename = false,
                scanByUniqueExtension = false,
                scanByPathAndSupportedExtensions = true
            ),
            hasMultiDiskSupport = true
        )

    @JvmStatic
    fun getPSP(): SystemBundle =
        SystemBundle(
            ESystemType.PSP,
            "Sony - PlayStation Portable",
            com.mozhimen.emulatork.basic.R.string.game_system_title_psp,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_psp,
            listOf(
                CoreBundle(
                    ECoreType.PPSSPP,
                    systemSettings_exposed = listOf(
                        SystemSetting(
                            "ppsspp_auto_frameskip",
                            R.string.setting_ppsspp_auto_frameskip
                        ),
                        SystemSetting(
                            "ppsspp_frameskip",
                            R.string.setting_mgba_frameskip
                        )
                    ),
                    systemSettings_exposedAdvanced = listOf(
                        SystemSetting(
                            "ppsspp_cpu_core",
                            R.string.setting_ppsspp_cpu_core,
                            arrayListOf(
                                SystemProperty("JIT", R.string.value_ppsspp_cpu_core_jit),
                                SystemProperty(
                                    "IR JIT",
                                    R.string.value_ppsspp_cpu_core_irjit
                                ),
                                SystemProperty(
                                    "Interpreter",
                                    R.string.value_ppsspp_cpu_core_interpreter
                                ),
                            )
                        ),
                        SystemSetting(
                            "ppsspp_internal_resolution",
                            R.string.setting_ppsspp_internal_resolution
                        ),
                        SystemSetting(
                            "ppsspp_texture_scaling_level",
                            R.string.setting_ppsspp_texture_scaling_level
                        ),
                    ),
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getPSP())
                    ),
                    supportsLibretroVFS = true
                )
            ),
            uniqueFileExtNames = listOf(),
            supportRomExtNames = listOf("iso", "cso", "pbp"),
            systemScanOption = SystemScanOption(
                scanByFilename = false,
                scanByUniqueExtension = false,
                scanByPathAndSupportedExtensions = true
            ),
            fastForwardSupport = false
        )

    @JvmStatic
    fun getFBNEO(): SystemBundle =
        SystemBundle(
            ESystemType.FBNEO,
            "FBNeo - Arcade Games",
            com.mozhimen.emulatork.basic.R.string.game_system_title_arcade_fbneo,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_arcade_fbneo,
            listOf(
                CoreBundle(
                    ECoreType.FBNEO,
                    systemSettings_exposed = listOf(
                        SystemSetting(
                            "fbneo-frameskip",
                            R.string.setting_fbneo_frameskip
                        ),
                        SystemSetting(
                            "fbneo-cpu-speed-adjust",
                            R.string.setting_fbneo_cpu_speed_adjust
                        )
                    ),
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getFB_NEO_4(), GamepadConfigProvider.getFB_NEO_6())
                    )
                )
            ),
            uniqueFileExtNames = listOf(),
            supportRomExtNames = listOf("zip"),
            systemScanOption = SystemScanOption(
                scanByFilename = false,
                scanByUniqueExtension = false,
                scanByPathAndFilename = true,
                scanByPathAndSupportedExtensions = false
            ),
        )

    @JvmStatic
    fun getMAME2003PLUS(): SystemBundle =
        SystemBundle(
            ESystemType.MAME2003PLUS,
            "MAME 2003-Plus",
            com.mozhimen.emulatork.basic.R.string.game_system_title_arcade_mame2003_plus,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_arcade_mame2003_plus,
            listOf(
                CoreBundle(
                    ECoreType.MAME2003PLUS,
                    isSupportStates = false,
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(
                            GamepadConfigProvider.getMAME_2003_4(),
                            GamepadConfigProvider.getMAME_2003_6()
                        )
                    )
                )
            ),
            uniqueFileExtNames = listOf(),
            supportRomExtNames = listOf("zip"),
            systemScanOption = SystemScanOption(
                scanByFilename = false,
                scanByUniqueExtension = false,
                scanByPathAndFilename = true,
                scanByPathAndSupportedExtensions = false
            ),
        )

    @JvmStatic
    fun getNDS(): SystemBundle =
        SystemBundle(
            ESystemType.NDS,
            "Nintendo - Nintendo DS",
            com.mozhimen.emulatork.basic.R.string.game_system_title_nds,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_nds,
            listOf(
                CoreBundle(
                    ECoreType.DESMUME,
                    systemSettings_exposed = listOf(
                        SystemSetting(
                            "desmume_screens_layout",
                            R.string.setting_desmume_screens_layout,
                            arrayListOf(
                                SystemProperty(
                                    "top/bottom",
                                    R.string.value_desmume_screens_layout_topbottom
                                ),
                                SystemProperty(
                                    "left/right",
                                    R.string.value_desmume_screens_layout_leftright
                                )
                            )
                        ),
                        SystemSetting(
                            "desmume_frameskip",
                            R.string.setting_desmume_frameskip
                        ),
                    ),
                    coreProperties = listOf(
                        CoreProperty("desmume_pointer_type", "touch"),
                        CoreProperty("desmume_frameskip", "1")
                    ),
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getDESMUME())
                    ),
                    skipDuplicateFrames = false
                ),
                CoreBundle(
                    ECoreType.MELONDS,
                    systemSettings_exposed = listOf(
                        SystemSetting(
                            "melonds_screen_layout",
                            R.string.setting_melonds_screen_layout,
                            arrayListOf(
                                SystemProperty(
                                    "Top/Bottom",
                                    R.string.value_melonds_screen_layout_topbottom
                                ),
                                SystemProperty(
                                    "Left/Right",
                                    R.string.value_melonds_screen_layout_leftright
                                ),
                                SystemProperty(
                                    "Hybrid Top",
                                    R.string.value_melonds_screen_layout_hybridtop
                                ),
                                SystemProperty(
                                    "Hybrid Bottom",
                                    R.string.value_melonds_screen_layout_hybridbottom
                                ),
                            )
                        )
                    ),
                    systemSettings_exposedAdvanced = listOf(
                        SystemSetting(
                            "melonds_threaded_renderer",
                            R.string.setting_melonds_threaded_renderer
                        ),
                        SystemSetting(
                            "melonds_jit_enable",
                            R.string.setting_melonds_jit_enable
                        ),
                    ),
                    coreProperties = listOf(
                        CoreProperty("melonds_touch_mode", "Touch"),
                        CoreProperty("melonds_threaded_renderer", "enabled")
                    ),
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getMELONDS())
                    ),
                    statesVersion = 1,
                )
            ),
            uniqueFileExtNames = listOf("nds"),
        )

    @JvmStatic
    fun getATARI7800(): SystemBundle =
        SystemBundle(
            ESystemType.ATARI7800,
            "Atari - 7800",
            com.mozhimen.emulatork.basic.R.string.game_system_title_atari7800,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_atari7800,
            listOf(
                CoreBundle(
                    ECoreType.PROSYSTEM,
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getATARI7800())
                    )
                ),
            ),
            uniqueFileExtNames = listOf("a78"),
            supportRomExtNames = listOf("bin")
        )

    @JvmStatic
    fun getLYNX(): SystemBundle =
        SystemBundle(
            ESystemType.LYNX,
            "Atari - Lynx",
            com.mozhimen.emulatork.basic.R.string.game_system_title_lynx,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_lynx,
            listOf(
                CoreBundle(
                    ECoreType.HANDY,
                    requiredBIOSFiles = listOf(
                        "lynxboot.img"
                    ),
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getLYNX())
                    ),
                    systemSettings_exposed = listOf(
                        SystemSetting(
                            "handy_rot",
                            R.string.setting_handy_rot,
                            arrayListOf(
                                SystemProperty(
                                    "None",
                                    R.string.value_handy_rot_none
                                ),
                                SystemProperty(
                                    "90",
                                    R.string.value_handy_rot_90
                                ),
                                SystemProperty(
                                    "270",
                                    R.string.value_handy_rot_270
                                ),
                            )
                        )
                    ),
                    coreProperties = listOf(
                        CoreProperty("handy_rot", "None"),
                        CoreProperty("handy_refresh_rate", "60")
                    )
                ),
            ),
            uniqueFileExtNames = listOf("lnx"),
        )

    @JvmStatic
    fun getPC_ENGINE(): SystemBundle =
        SystemBundle(
            ESystemType.PC_ENGINE,
            "NEC - PC Engine - TurboGrafx 16",
            com.mozhimen.emulatork.basic.R.string.game_system_title_pce,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_pce,
            listOf(
                CoreBundle(
                    ECoreType.MEDNAFEN_PCE_FAST,
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getPCE())
                    )
                ),
            ),
            uniqueFileExtNames = listOf("pce"),
            supportRomExtNames = listOf("bin"),
        )

    @JvmStatic
    fun getNGP(): SystemBundle =
        SystemBundle(
            ESystemType.NGP,
            "SNK - Neo Geo Pocket",
            com.mozhimen.emulatork.basic.R.string.game_system_title_ngp,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_ngp,
            listOf(
                CoreBundle(
                    ECoreType.MEDNAFEN_NGP,
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getNGP())
                    )
                ),
            ),
            uniqueFileExtNames = listOf("ngp"),
        )

    @JvmStatic
    fun getNGC(): SystemBundle =
        SystemBundle(
            ESystemType.NGC,
            "SNK - Neo Geo Pocket Color",
            com.mozhimen.emulatork.basic.R.string.game_system_title_ngc,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_ngc,
            listOf(
                CoreBundle(
                    ECoreType.MEDNAFEN_NGP,
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getNGP())
                    )
                ),
            ),
            uniqueFileExtNames = listOf("ngc"),
        )

    @JvmStatic
    fun getWS(): SystemBundle =
        SystemBundle(
            ESystemType.WS,
            "Bandai - WonderSwan",
            com.mozhimen.emulatork.basic.R.string.game_system_title_ws,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_ws,
            listOf(
                CoreBundle(
                    ECoreType.MEDNAFEN_WSWAN,
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getWS_LANDSCAPE(), GamepadConfigProvider.getWS_PORTRAIT())
                    ),
                    systemSettings_exposed = listOf(
                        SystemSetting(
                            "wswan_rotate_display",
                            R.string.setting_wswan_rotate_display,
                            arrayListOf(
                                SystemProperty(
                                    "landscape",
                                    R.string.value_wswan_rotate_display_landscape
                                ),
                                SystemProperty(
                                    "portrait",
                                    R.string.value_wswan_rotate_display_portrait
                                ),
                            )
                        ),
                        SystemSetting(
                            "wswan_mono_palette",
                            R.string.setting_wswan_mono_palette
                        )
                    ),
                    coreProperties = listOf(
                        CoreProperty("wswan_rotate_display", "landscape"),
                        CoreProperty("wswan_mono_palette", "wonderswan"),
                    )
                ),
            ),
            uniqueFileExtNames = listOf("ws"),
        )

    @JvmStatic
    fun getWSC(): SystemBundle =
        SystemBundle(
            ESystemType.WSC,
            "Bandai - WonderSwan Color",
            com.mozhimen.emulatork.basic.R.string.game_system_title_wsc,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_wsc,
            listOf(
                CoreBundle(
                    ECoreType.MEDNAFEN_WSWAN,
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getWS_LANDSCAPE(), GamepadConfigProvider.getWS_PORTRAIT())
                    ),
                    systemSettings_exposed = listOf(
                        SystemSetting(
                            "wswan_rotate_display",
                            R.string.setting_wswan_rotate_display,
                            arrayListOf(
                                SystemProperty(
                                    "landscape",
                                    R.string.value_wswan_rotate_display_landscape
                                ),
                                SystemProperty(
                                    "portrait",
                                    R.string.value_wswan_rotate_display_portrait
                                ),
                            )
                        )
                    ),
                    coreProperties = listOf(
                        CoreProperty("wswan_rotate_display", "landscape")
                    )
                ),
            ),
            uniqueFileExtNames = listOf("wsc"),
        )

    @JvmStatic
    fun getDOS(): SystemBundle =
        SystemBundle(
            ESystemType.DOS,
            "DOS",
            com.mozhimen.emulatork.basic.R.string.game_system_title_dos,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_dos,
            listOf(
                CoreBundle(
                    ECoreType.DOSBOX_PURE,
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(
                            GamepadConfigProvider.getDOS_AUTO(),
                            GamepadConfigProvider.getDOS_MOUSE_LEFT(),
                            GamepadConfigProvider.getDOS_MOUSE_RIGHT(),
                        )
                    ),
                    isSupportStates = false,
                )
            ),
            fastForwardSupport = false,
            uniqueFileExtNames = listOf("dosz"),
            systemScanOption = SystemScanOption(
                scanByFilename = false,
                scanByUniqueExtension = true,
                scanByPathAndFilename = false,
                scanByPathAndSupportedExtensions = true
            ),
        )

    @JvmStatic
    fun getNINTENDO_3DS(): SystemBundle =
        SystemBundle(
            ESystemType.NINTENDO_3DS,
            "Nintendo - Nintendo 3DS",
            com.mozhimen.emulatork.basic.R.string.game_system_title_3ds,
            com.mozhimen.emulatork.basic.R.string.game_system_abbr_3ds,
            listOf(
                CoreBundle(
                    ECoreType.CITRA,
                    gamepadConfigMap = hashMapOf(
                        0 to arrayListOf(GamepadConfigProvider.getNINTENDO_3DS())
                    ),
                    coreProperties = listOf(
                        CoreProperty("citra_use_acc_mul", "disabled"),
                        CoreProperty("citra_touch_touchscreen", "enabled"),
                        CoreProperty("citra_mouse_touchscreen", "disabled"),
                        CoreProperty("citra_render_touchscreen", "disabled"),
                        CoreProperty("citra_use_hw_shader_cache", "disabled"),
                    ),
                    systemSettings_exposed = listOf(
                        SystemSetting(
                            "citra_layout_option",
                            R.string.setting_citra_layout_option,
                            arrayListOf(
                                SystemProperty(
                                    "Default Top-Bottom Screen",
                                    R.string.value_citra_layout_option_topbottom
                                ),
                                SystemProperty(
                                    "Side by Side",
                                    R.string.value_citra_layout_option_sidebyside
                                )
                            )
                        ),
                        SystemSetting(
                            "citra_resolution_factor",
                            R.string.setting_citra_resolution_factor
                        ),
                        SystemSetting(
                            "citra_use_acc_mul",
                            R.string.setting_citra_use_acc_mul
                        ),
                        SystemSetting(
                            "citra_use_acc_geo_shaders",
                            R.string.setting_citra_use_acc_geo_shaders
                        ),
                    ),
                    isSupportStates = false,
                    supportsLibretroVFS = true,
                    supportedOnlyArchitectures = setOf("arm64-v8a")
                ),
            ),
            uniqueFileExtNames = listOf("3ds"),
        )
}