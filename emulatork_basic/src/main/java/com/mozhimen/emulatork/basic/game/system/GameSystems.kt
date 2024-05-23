package com.mozhimen.emulatork.basic.game.system

import com.mozhimen.emulatork.basic.R
import com.mozhimen.emulatork.basic.controller.touch.ControllerTouchConfigs
import com.mozhimen.emulatork.basic.core.CoreID
import com.mozhimen.emulatork.basic.core.CoreVariable
import java.util.Locale

/**
 * @ClassName Systems
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/22
 * @Version 1.0
 */
object GameSystems {
    private val SYSTEMS = listOf(
        GameSystem(
            GameSystemID.ATARI2600,
            "Atari - 2600",
            R.string.game_system_title_atari2600,
            R.string.game_system_abbr_atari2600,
            listOf(
                GameSystemCoreConfig(
                    coreID = CoreID.STELLA,
                    systemExposedSettings = listOf(
                        GameSystemExposedSetting(
                            "stella_filter",
                            R.string.setting_stella_filter,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "disabled",
                                    R.string.value_stella_filter_disabled
                                ),
                                GameSystemExposedSetting.Value(
                                    "composite",
                                    R.string.value_stella_filter_composite
                                ),
                                GameSystemExposedSetting.Value(
                                    "s-video",
                                    R.string.value_stella_filter_svideo
                                ),
                                GameSystemExposedSetting.Value("rgb", R.string.value_stella_filter_rgb),
                                GameSystemExposedSetting.Value(
                                    "badly adjusted",
                                    R.string.value_stella_filter_badlyadjusted
                                ),
                            )
                        ),
                        GameSystemExposedSetting(
                            "stella_crop_hoverscan",
                            R.string.setting_stella_crop_hoverscan
                        )
                    ),
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.ATARI_2600)
                    )
                )
            ),
            uniqueExtensions = listOf("a26"),
        ),
        GameSystem(
            GameSystemID.NES,
            "Nintendo - Nintendo Entertainment System",
            R.string.game_system_title_nes,
            R.string.game_system_abbr_nes,
            listOf(
                GameSystemCoreConfig(
                    CoreID.FCEUMM,
                    systemExposedSettings = listOf(
                        GameSystemExposedSetting(
                            "fceumm_overscan_h",
                            R.string.setting_fceumm_overscan_h
                        ),
                        GameSystemExposedSetting(
                            "fceumm_overscan_v",
                            R.string.setting_fceumm_overscan_v
                        ),
                    ),
                    exposedAdvancedSettings = listOf(
                        GameSystemExposedSetting(
                            "fceumm_nospritelimit",
                            R.string.setting_fceumm_nospritelimit
                        ),
                    ),
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.NES)
                    ),
                )
            ),
            uniqueExtensions = listOf("nes"),
        ),
        GameSystem(
            GameSystemID.SNES,
            "Nintendo - Super Nintendo Entertainment System",
            R.string.game_system_title_snes,
            R.string.game_system_abbr_snes,
            listOf(
                GameSystemCoreConfig(
                    CoreID.SNES9X,
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.SNES)
                    )
                )
            ),
            uniqueExtensions = listOf("smc", "sfc"),
        ),
        GameSystem(
            GameSystemID.SMS,
            "Sega - Master System - Mark III",
            R.string.game_system_title_sms,
            R.string.game_system_abbr_sms,
            listOf(
                GameSystemCoreConfig(
                    CoreID.GENESIS_PLUS_GX,
                    systemExposedSettings = listOf(
                        GameSystemExposedSetting(
                            "genesis_plus_gx_blargg_ntsc_filter",
                            R.string.setting_genesis_plus_gx_blargg_ntsc_filter,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "disabled",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_disabled
                                ),
                                GameSystemExposedSetting.Value(
                                    "monochrome",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_monochrome
                                ),
                                GameSystemExposedSetting.Value(
                                    "composite",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_composite
                                ),
                                GameSystemExposedSetting.Value(
                                    "svideo",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_svideo
                                ),
                                GameSystemExposedSetting.Value(
                                    "rgb",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_rgb
                                ),
                            )
                        )
                    ),
                    exposedAdvancedSettings = listOf(
                        GameSystemExposedSetting(
                            "genesis_plus_gx_no_sprite_limit",
                            R.string.setting_genesis_plus_gx_no_sprite_limit
                        ),
                        GameSystemExposedSetting(
                            "genesis_plus_gx_overscan",
                            R.string.setting_genesis_plus_gx_overscan,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "disabled",
                                    R.string.value_genesis_plus_gx_overscan_disabled
                                ),
                                GameSystemExposedSetting.Value(
                                    "top/bottom",
                                    R.string.value_genesis_plus_gx_overscan_topbottom
                                ),
                                GameSystemExposedSetting.Value(
                                    "left/right",
                                    R.string.value_genesis_plus_gx_overscan_leftright
                                ),
                                GameSystemExposedSetting.Value(
                                    "full",
                                    R.string.value_genesis_plus_gx_overscan_full
                                ),
                            )
                        )
                    ),
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.SMS)
                    )
                )
            ),
            uniqueExtensions = listOf("sms"),
        ),
        GameSystem(
            GameSystemID.GENESIS,
            "Sega - Mega Drive - Genesis",
            R.string.game_system_title_genesis,
            R.string.game_system_abbr_genesis,
            listOf(
                GameSystemCoreConfig(
                    CoreID.GENESIS_PLUS_GX,
                    systemExposedSettings = listOf(
                        GameSystemExposedSetting(
                            "genesis_plus_gx_blargg_ntsc_filter",
                            R.string.setting_genesis_plus_gx_blargg_ntsc_filter,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "disabled",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_disabled
                                ),
                                GameSystemExposedSetting.Value(
                                    "monochrome",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_monochrome
                                ),
                                GameSystemExposedSetting.Value(
                                    "composite",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_composite
                                ),
                                GameSystemExposedSetting.Value(
                                    "svideo",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_svideo
                                ),
                                GameSystemExposedSetting.Value(
                                    "rgb",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_rgb
                                ),
                            )
                        )
                    ),
                    exposedAdvancedSettings = listOf(
                        GameSystemExposedSetting(
                            "genesis_plus_gx_no_sprite_limit",
                            R.string.setting_genesis_plus_gx_no_sprite_limit
                        ),
                        GameSystemExposedSetting(
                            "genesis_plus_gx_overscan",
                            R.string.setting_genesis_plus_gx_overscan,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "disabled",
                                    R.string.value_genesis_plus_gx_overscan_disabled
                                ),
                                GameSystemExposedSetting.Value(
                                    "top/bottom",
                                    R.string.value_genesis_plus_gx_overscan_topbottom
                                ),
                                GameSystemExposedSetting.Value(
                                    "left/right",
                                    R.string.value_genesis_plus_gx_overscan_leftright
                                ),
                                GameSystemExposedSetting.Value(
                                    "full",
                                    R.string.value_genesis_plus_gx_overscan_full
                                ),
                            )
                        )
                    ),
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(
                            ControllerTouchConfigs.GENESIS_3,
                            ControllerTouchConfigs.GENESIS_6
                        ),
                        1 to arrayListOf(
                            ControllerTouchConfigs.GENESIS_3,
                            ControllerTouchConfigs.GENESIS_6
                        ),
                        2 to arrayListOf(
                            ControllerTouchConfigs.GENESIS_3,
                            ControllerTouchConfigs.GENESIS_6
                        ),
                        3 to arrayListOf(
                            ControllerTouchConfigs.GENESIS_3,
                            ControllerTouchConfigs.GENESIS_6
                        )
                    )
                )
            ),
            uniqueExtensions = listOf("gen", "smd", "md"),
        ),
        GameSystem(
            GameSystemID.SEGACD,
            "Sega - Mega-CD - Sega CD",
            R.string.game_system_title_scd,
            R.string.game_system_abbr_scd,
            listOf(
                GameSystemCoreConfig(
                    CoreID.GENESIS_PLUS_GX,
                    systemExposedSettings = listOf(
                        GameSystemExposedSetting(
                            "genesis_plus_gx_blargg_ntsc_filter",
                            R.string.setting_genesis_plus_gx_blargg_ntsc_filter,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "disabled",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_disabled
                                ),
                                GameSystemExposedSetting.Value(
                                    "monochrome",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_monochrome
                                ),
                                GameSystemExposedSetting.Value(
                                    "composite",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_composite
                                ),
                                GameSystemExposedSetting.Value(
                                    "svideo",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_svideo
                                ),
                                GameSystemExposedSetting.Value(
                                    "rgb",
                                    R.string.value_genesis_plus_gx_blargg_ntsc_filter_rgb
                                ),
                            )
                        )
                    ),
                    exposedAdvancedSettings = listOf(
                        GameSystemExposedSetting(
                            "genesis_plus_gx_no_sprite_limit",
                            R.string.setting_genesis_plus_gx_no_sprite_limit
                        ),
                        GameSystemExposedSetting(
                            "genesis_plus_gx_overscan",
                            R.string.setting_genesis_plus_gx_overscan,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "disabled",
                                    R.string.value_genesis_plus_gx_overscan_disabled
                                ),
                                GameSystemExposedSetting.Value(
                                    "top/bottom",
                                    R.string.value_genesis_plus_gx_overscan_topbottom
                                ),
                                GameSystemExposedSetting.Value(
                                    "left/right",
                                    R.string.value_genesis_plus_gx_overscan_leftright
                                ),
                                GameSystemExposedSetting.Value(
                                    "full",
                                    R.string.value_genesis_plus_gx_overscan_full
                                ),
                            )
                        )
                    ),
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(
                            ControllerTouchConfigs.GENESIS_3,
                            ControllerTouchConfigs.GENESIS_6
                        ),
                        1 to arrayListOf(
                            ControllerTouchConfigs.GENESIS_3,
                            ControllerTouchConfigs.GENESIS_6
                        ),
                        2 to arrayListOf(
                            ControllerTouchConfigs.GENESIS_3,
                            ControllerTouchConfigs.GENESIS_6
                        ),
                        3 to arrayListOf(
                            ControllerTouchConfigs.GENESIS_3,
                            ControllerTouchConfigs.GENESIS_6
                        )
                    ),
                    regionalBIOSFiles = mapOf(
                        "Europe" to "bios_CD_E.bin",
                        "Japan" to "bios_CD_J.bin",
                        "USA" to "bios_CD_U.bin"
                    ),
                )
            ),
            scanOptions = GameSystemScanOptions(
                scanByFilename = false,
                scanByUniqueExtension = false,
                scanByPathAndSupportedExtensions = true,
                scanBySimilarSerial = true
            ),
            uniqueExtensions = listOf(),
            supportedExtensions = listOf("cue", "iso", "chd"),
        ),
        GameSystem(
            GameSystemID.GG,
            "Sega - Game Gear",
            R.string.game_system_title_gg,
            R.string.game_system_abbr_gg,
            listOf(
                GameSystemCoreConfig(
                    CoreID.GENESIS_PLUS_GX,
                    systemExposedSettings = listOf(
                        GameSystemExposedSetting(
                            "genesis_plus_gx_lcd_filter",
                            R.string.setting_genesis_plus_gx_lcd_filter
                        )
                    ),
                    exposedAdvancedSettings = listOf(
                        GameSystemExposedSetting(
                            "genesis_plus_gx_no_sprite_limit",
                            R.string.setting_genesis_plus_gx_no_sprite_limit
                        ),
                    ),
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.GG)
                    )
                )
            ),
            uniqueExtensions = listOf("gg"),
        ),
        GameSystem(
            GameSystemID.GB,
            "Nintendo - Game Boy",
            R.string.game_system_title_gb,
            R.string.game_system_abbr_gb,
            listOf(
                GameSystemCoreConfig(
                    CoreID.GAMBATTE,
                    systemExposedSettings = listOf(
                        GameSystemExposedSetting(
                            "gambatte_gb_colorization",
                            R.string.setting_gambatte_gb_colorization
                        ),
                        GameSystemExposedSetting(
                            "gambatte_gb_internal_palette",
                            R.string.setting_gambatte_gb_internal_palette
                        ),
                        GameSystemExposedSetting(
                            "gambatte_mix_frames",
                            R.string.setting_gambatte_mix_frames,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "disabled",
                                    R.string.value_gambatte_mix_frames_disabled
                                ),
                                GameSystemExposedSetting.Value(
                                    "mix",
                                    R.string.value_gambatte_mix_frames_mix
                                ),
                                GameSystemExposedSetting.Value(
                                    "lcd_ghosting",
                                    R.string.value_gambatte_mix_frames_lcd_ghosting
                                ),
                                GameSystemExposedSetting.Value(
                                    "lcd_ghosting_fast",
                                    R.string.value_gambatte_mix_frames_lcd_ghosting_fast
                                ),
                            )
                        ),
                        GameSystemExposedSetting(
                            "gambatte_dark_filter_level",
                            R.string.setting_gambatte_dark_filter_level
                        )
                    ),
                    defaultSettings = listOf(
                        CoreVariable("gambatte_gb_colorization", "internal"),
                        CoreVariable("gambatte_gb_internal_palette", "GB - Pocket")
                    ),
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.GB)
                    )
                ),
            ),
            uniqueExtensions = listOf("gb"),
        ),
        GameSystem(
            GameSystemID.GBC,
            "Nintendo - Game Boy Color",
            R.string.game_system_title_gbc,
            R.string.game_system_abbr_gbc,
            listOf(
                GameSystemCoreConfig(
                    CoreID.GAMBATTE,
                    systemExposedSettings = listOf(
                        GameSystemExposedSetting(
                            "gambatte_mix_frames",
                            R.string.setting_gambatte_mix_frames,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "disabled",
                                    R.string.value_gambatte_mix_frames_disabled
                                ),
                                GameSystemExposedSetting.Value(
                                    "mix",
                                    R.string.value_gambatte_mix_frames_mix
                                ),
                                GameSystemExposedSetting.Value(
                                    "lcd_ghosting",
                                    R.string.value_gambatte_mix_frames_lcd_ghosting
                                ),
                                GameSystemExposedSetting.Value(
                                    "lcd_ghosting_fast",
                                    R.string.value_gambatte_mix_frames_lcd_ghosting_fast
                                ),
                            )
                        ),
                        GameSystemExposedSetting(
                            "gambatte_gbc_color_correction",
                            R.string.setting_gambatte_gbc_color_correction,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "disabled",
                                    R.string.value_gambatte_gbc_color_correction_disabled
                                ),
                                GameSystemExposedSetting.Value(
                                    "always",
                                    R.string.value_gambatte_gbc_color_correction_always
                                )
                            )
                        ),
                        GameSystemExposedSetting(
                            "gambatte_dark_filter_level",
                            R.string.setting_gambatte_dark_filter_level
                        )
                    ),
                    rumbleSupported = true,
                    defaultSettings = listOf(
                        CoreVariable("gambatte_gbc_color_correction", "disabled"),
                    ),
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.GB)
                    )
                ),
            ),
            uniqueExtensions = listOf("gbc"),
        ),
        GameSystem(
            GameSystemID.GBA,
            "Nintendo - Game Boy Advance",
            R.string.game_system_title_gba,
            R.string.game_system_abbr_gba,
            listOf(
                GameSystemCoreConfig(
                    CoreID.MGBA,
                    systemExposedSettings = listOf(
                        GameSystemExposedSetting(
                            "mgba_solar_sensor_level",
                            R.string.setting_mgba_solar_sensor_level
                        ),
                        GameSystemExposedSetting(
                            "mgba_interframe_blending",
                            R.string.setting_mgba_interframe_blending,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "OFF",
                                    R.string.value_mgba_interframe_blending_off
                                ),
                                GameSystemExposedSetting.Value(
                                    "mix",
                                    R.string.value_mgba_interframe_blending_mix
                                ),
                                GameSystemExposedSetting.Value(
                                    "lcd_ghosting",
                                    R.string.value_mgba_interframe_blending_lcd_ghosting
                                ),
                                GameSystemExposedSetting.Value(
                                    "lcd_ghosting_fast",
                                    R.string.value_mgba_interframe_blending_lcd_ghosting_fast
                                ),
                            )
                        ),
                        GameSystemExposedSetting(
                            "mgba_frameskip",
                            R.string.setting_mgba_frameskip,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "disabled",
                                    R.string.value_mgba_frameskip_disabled
                                ),
                                GameSystemExposedSetting.Value("auto", R.string.value_mgba_frameskip_auto)
                            )
                        ),
                        GameSystemExposedSetting(
                            "mgba_color_correction",
                            R.string.setting_mgba_color_correction,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "OFF",
                                    R.string.value_mgba_color_correction_off
                                ),
                                GameSystemExposedSetting.Value(
                                    "GBA",
                                    R.string.value_mgba_color_correction_gba
                                )
                            )
                        ),
                    ),
                    rumbleSupported = true,
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.GBA)
                    )
                ),
            ),
            uniqueExtensions = listOf("gba"),
        ),
        GameSystem(
            GameSystemID.N64,
            "Nintendo - Nintendo 64",
            R.string.game_system_title_n64,
            R.string.game_system_abbr_n64,
            listOf(
                GameSystemCoreConfig(
                    CoreID.MUPEN64_PLUS_NEXT,
                    systemExposedSettings = listOf(
                        GameSystemExposedSetting(
                            "mupen64plus-43screensize",
                            R.string.setting_mupen64plus_43screensize
                        ),
                        GameSystemExposedSetting(
                            "mupen64plus-cpucore",
                            R.string.setting_mupen64plus_cpucore,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "dynamic_recompiler",
                                    R.string.value_mupen64plus_cpucore_dynamicrecompiler
                                ),
                                GameSystemExposedSetting.Value(
                                    "pure_interpreter",
                                    R.string.value_mupen64plus_cpucore_pureinterpreter
                                ),
                                GameSystemExposedSetting.Value(
                                    "cached_interpreter",
                                    R.string.value_mupen64plus_cpucore_cachedinterpreter
                                ),
                            )
                        ),
                        GameSystemExposedSetting(
                            "mupen64plus-BilinearMode",
                            R.string.setting_mupen64plus_BilinearMode,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "standard",
                                    R.string.value_mupen64plus_bilinearmode_standard
                                ),
                                GameSystemExposedSetting.Value(
                                    "3point",
                                    R.string.value_mupen64plus_bilinearmode_3point
                                ),
                            )
                        ),
                        GameSystemExposedSetting(
                            "mupen64plus-pak1",
                            R.string.setting_mupen64plus_pak1,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "memory",
                                    R.string.value_mupen64plus_mupen64plus_pak1_memory
                                ),
                                GameSystemExposedSetting.Value(
                                    "rumble",
                                    R.string.value_mupen64plus_mupen64plus_pak1_rumble
                                ),
                                GameSystemExposedSetting.Value(
                                    "none",
                                    R.string.value_mupen64plus_mupen64plus_pak1_none
                                )
                            )
                        ),
                        GameSystemExposedSetting(
                            "mupen64plus-pak2",
                            R.string.setting_mupen64plus_pak2,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "none",
                                    R.string.value_mupen64plus_mupen64plus_pak2_none
                                ),
                                GameSystemExposedSetting.Value(
                                    "rumble",
                                    R.string.value_mupen64plus_mupen64plus_pak2_rumble
                                )
                            )
                        )
                    ),
                    defaultSettings = listOf(
                        CoreVariable("mupen64plus-43screensize", "320x240"),
                        CoreVariable("mupen64plus-FrameDuping", "True")
                    ),
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.N64)
                    ),
                    rumbleSupported = true,
                    skipDuplicateFrames = false
                )
            ),
            uniqueExtensions = listOf("n64", "z64"),
        ),
        GameSystem(
            GameSystemID.PSX,
            "Sony - PlayStation",
            R.string.game_system_title_psx,
            R.string.game_system_abbr_psx,
            listOf(
                GameSystemCoreConfig(
                    CoreID.PCSX_REARMED,
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(
                            ControllerTouchConfigs.PSX_STANDARD,
                            ControllerTouchConfigs.PSX_DUALSHOCK
                        ),
                        1 to arrayListOf(
                            ControllerTouchConfigs.PSX_STANDARD,
                            ControllerTouchConfigs.PSX_DUALSHOCK
                        ),
                        2 to arrayListOf(
                            ControllerTouchConfigs.PSX_STANDARD,
                            ControllerTouchConfigs.PSX_DUALSHOCK
                        ),
                        3 to arrayListOf(
                            ControllerTouchConfigs.PSX_STANDARD,
                            ControllerTouchConfigs.PSX_DUALSHOCK
                        ),
                    ),
                    systemExposedSettings = listOf(
                        GameSystemExposedSetting(
                            "pcsx_rearmed_frameskip",
                            R.string.setting_pcsx_rearmed_frameskip
                        )
                    ),
                    exposedAdvancedSettings = listOf(
                        GameSystemExposedSetting(
                            "pcsx_rearmed_drc",
                            R.string.setting_pcsx_rearmed_drc
                        )
                    ),
                    defaultSettings = listOf(
                        CoreVariable("pcsx_rearmed_drc", "disabled"),
                        CoreVariable("pcsx_rearmed_duping_enable", "enabled"),
                    ),
                    rumbleSupported = true,
                    supportsLibretroVFS = true,
                    skipDuplicateFrames = false
                )
            ),
            uniqueExtensions = listOf(),
            supportedExtensions = listOf("iso", "pbp", "chd", "cue", "m3u"),
            scanOptions = GameSystemScanOptions(
                scanByFilename = false,
                scanByUniqueExtension = false,
                scanByPathAndSupportedExtensions = true
            ),
            hasMultiDiskSupport = true
        ),
        GameSystem(
            GameSystemID.PSP,
            "Sony - PlayStation Portable",
            R.string.game_system_title_psp,
            R.string.game_system_abbr_psp,
            listOf(
                GameSystemCoreConfig(
                    CoreID.PPSSPP,
                    systemExposedSettings = listOf(
                        GameSystemExposedSetting(
                            "ppsspp_auto_frameskip",
                            R.string.setting_ppsspp_auto_frameskip
                        ),
                        GameSystemExposedSetting(
                            "ppsspp_frameskip",
                            R.string.setting_mgba_frameskip
                        )
                    ),
                    exposedAdvancedSettings = listOf(
                        GameSystemExposedSetting(
                            "ppsspp_cpu_core",
                            R.string.setting_ppsspp_cpu_core,
                            arrayListOf(
                                GameSystemExposedSetting.Value("JIT", R.string.value_ppsspp_cpu_core_jit),
                                GameSystemExposedSetting.Value(
                                    "IR JIT",
                                    R.string.value_ppsspp_cpu_core_irjit
                                ),
                                GameSystemExposedSetting.Value(
                                    "Interpreter",
                                    R.string.value_ppsspp_cpu_core_interpreter
                                ),
                            )
                        ),
                        GameSystemExposedSetting(
                            "ppsspp_internal_resolution",
                            R.string.setting_ppsspp_internal_resolution
                        ),
                        GameSystemExposedSetting(
                            "ppsspp_texture_scaling_level",
                            R.string.setting_ppsspp_texture_scaling_level
                        ),
                    ),
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.PSP)
                    ),
                    supportsLibretroVFS = true
                )
            ),
            uniqueExtensions = listOf(),
            supportedExtensions = listOf("iso", "cso", "pbp"),
            scanOptions = GameSystemScanOptions(
                scanByFilename = false,
                scanByUniqueExtension = false,
                scanByPathAndSupportedExtensions = true
            ),
            fastForwardSupport = false
        ),
        GameSystem(
            GameSystemID.FBNEO,
            "FBNeo - Arcade Games",
            R.string.game_system_title_arcade_fbneo,
            R.string.game_system_abbr_arcade_fbneo,
            listOf(
                GameSystemCoreConfig(
                    CoreID.FBNEO,
                    systemExposedSettings = listOf(
                        GameSystemExposedSetting(
                            "fbneo-frameskip",
                            R.string.setting_fbneo_frameskip
                        ),
                        GameSystemExposedSetting(
                            "fbneo-cpu-speed-adjust",
                            R.string.setting_fbneo_cpu_speed_adjust
                        )
                    ),
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.FB_NEO_4, ControllerTouchConfigs.FB_NEO_6)
                    )
                )
            ),
            uniqueExtensions = listOf(),
            supportedExtensions = listOf("zip"),
            scanOptions = GameSystemScanOptions(
                scanByFilename = false,
                scanByUniqueExtension = false,
                scanByPathAndFilename = true,
                scanByPathAndSupportedExtensions = false
            ),
        ),
        GameSystem(
            GameSystemID.MAME2003PLUS,
            "MAME 2003-Plus",
            R.string.game_system_title_arcade_mame2003_plus,
            R.string.game_system_abbr_arcade_mame2003_plus,
            listOf(
                GameSystemCoreConfig(
                    CoreID.MAME2003PLUS,
                    statesSupported = false,
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(
                            ControllerTouchConfigs.MAME_2003_4,
                            ControllerTouchConfigs.MAME_2003_6
                        )
                    )
                )
            ),
            uniqueExtensions = listOf(),
            supportedExtensions = listOf("zip"),
            scanOptions = GameSystemScanOptions(
                scanByFilename = false,
                scanByUniqueExtension = false,
                scanByPathAndFilename = true,
                scanByPathAndSupportedExtensions = false
            ),
        ),
        GameSystem(
            GameSystemID.NDS,
            "Nintendo - Nintendo DS",
            R.string.game_system_title_nds,
            R.string.game_system_abbr_nds,
            listOf(
                GameSystemCoreConfig(
                    CoreID.DESMUME,
                    systemExposedSettings = listOf(
                        GameSystemExposedSetting(
                            "desmume_screens_layout",
                            R.string.setting_desmume_screens_layout,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "top/bottom",
                                    R.string.value_desmume_screens_layout_topbottom
                                ),
                                GameSystemExposedSetting.Value(
                                    "left/right",
                                    R.string.value_desmume_screens_layout_leftright
                                )
                            )
                        ),
                        GameSystemExposedSetting(
                            "desmume_frameskip",
                            R.string.setting_desmume_frameskip
                        ),
                    ),
                    defaultSettings = listOf(
                        CoreVariable("desmume_pointer_type", "touch"),
                        CoreVariable("desmume_frameskip", "1")
                    ),
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.DESMUME)
                    ),
                    skipDuplicateFrames = false
                ),
                GameSystemCoreConfig(
                    CoreID.MELONDS,
                    systemExposedSettings = listOf(
                        GameSystemExposedSetting(
                            "melonds_screen_layout",
                            R.string.setting_melonds_screen_layout,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "Top/Bottom",
                                    R.string.value_melonds_screen_layout_topbottom
                                ),
                                GameSystemExposedSetting.Value(
                                    "Left/Right",
                                    R.string.value_melonds_screen_layout_leftright
                                ),
                                GameSystemExposedSetting.Value(
                                    "Hybrid Top",
                                    R.string.value_melonds_screen_layout_hybridtop
                                ),
                                GameSystemExposedSetting.Value(
                                    "Hybrid Bottom",
                                    R.string.value_melonds_screen_layout_hybridbottom
                                ),
                            )
                        )
                    ),
                    exposedAdvancedSettings = listOf(
                        GameSystemExposedSetting(
                            "melonds_threaded_renderer",
                            R.string.setting_melonds_threaded_renderer
                        ),
                        GameSystemExposedSetting(
                            "melonds_jit_enable",
                            R.string.setting_melonds_jit_enable
                        ),
                    ),
                    defaultSettings = listOf(
                        CoreVariable("melonds_touch_mode", "Touch"),
                        CoreVariable("melonds_threaded_renderer", "enabled")
                    ),
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.MELONDS)
                    ),
                    statesVersion = 1,
                )
            ),
            uniqueExtensions = listOf("nds"),
        ),
        GameSystem(
            GameSystemID.ATARI7800,
            "Atari - 7800",
            R.string.game_system_title_atari7800,
            R.string.game_system_abbr_atari7800,
            listOf(
                GameSystemCoreConfig(
                    CoreID.PROSYSTEM,
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.ATARI7800)
                    )
                ),
            ),
            uniqueExtensions = listOf("a78"),
            supportedExtensions = listOf("bin")
        ),
        GameSystem(
            GameSystemID.LYNX,
            "Atari - Lynx",
            R.string.game_system_title_lynx,
            R.string.game_system_abbr_lynx,
            listOf(
                GameSystemCoreConfig(
                    CoreID.HANDY,
                    requiredBIOSFiles = listOf(
                        "lynxboot.img"
                    ),
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.LYNX)
                    ),
                    systemExposedSettings = listOf(
                        GameSystemExposedSetting(
                            "handy_rot",
                            R.string.setting_handy_rot,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "None",
                                    R.string.value_handy_rot_none
                                ),
                                GameSystemExposedSetting.Value(
                                    "90",
                                    R.string.value_handy_rot_90
                                ),
                                GameSystemExposedSetting.Value(
                                    "270",
                                    R.string.value_handy_rot_270
                                ),
                            )
                        )
                    ),
                    defaultSettings = listOf(
                        CoreVariable("handy_rot", "None"),
                        CoreVariable("handy_refresh_rate", "60")
                    )
                ),
            ),
            uniqueExtensions = listOf("lnx"),
        ),
        GameSystem(
            GameSystemID.PC_ENGINE,
            "NEC - PC Engine - TurboGrafx 16",
            R.string.game_system_title_pce,
            R.string.game_system_abbr_pce,
            listOf(
                GameSystemCoreConfig(
                    CoreID.MEDNAFEN_PCE_FAST,
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.PCE)
                    )
                ),
            ),
            uniqueExtensions = listOf("pce"),
            supportedExtensions = listOf("bin"),
        ),
        GameSystem(
            GameSystemID.NGP,
            "SNK - Neo Geo Pocket",
            R.string.game_system_title_ngp,
            R.string.game_system_abbr_ngp,
            listOf(
                GameSystemCoreConfig(
                    CoreID.MEDNAFEN_NGP,
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.NGP)
                    )
                ),
            ),
            uniqueExtensions = listOf("ngp"),
        ),
        GameSystem(
            GameSystemID.NGC,
            "SNK - Neo Geo Pocket Color",
            R.string.game_system_title_ngc,
            R.string.game_system_abbr_ngc,
            listOf(
                GameSystemCoreConfig(
                    CoreID.MEDNAFEN_NGP,
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.NGP)
                    )
                ),
            ),
            uniqueExtensions = listOf("ngc"),
        ),
        GameSystem(
            GameSystemID.WS,
            "Bandai - WonderSwan",
            R.string.game_system_title_ws,
            R.string.game_system_abbr_ws,
            listOf(
                GameSystemCoreConfig(
                    CoreID.MEDNAFEN_WSWAN,
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.WS_LANDSCAPE, ControllerTouchConfigs.WS_PORTRAIT)
                    ),
                    systemExposedSettings = listOf(
                        GameSystemExposedSetting(
                            "wswan_rotate_display",
                            R.string.setting_wswan_rotate_display,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "landscape",
                                    R.string.value_wswan_rotate_display_landscape
                                ),
                                GameSystemExposedSetting.Value(
                                    "portrait",
                                    R.string.value_wswan_rotate_display_portrait
                                ),
                            )
                        ),
                        GameSystemExposedSetting(
                            "wswan_mono_palette",
                            R.string.setting_wswan_mono_palette
                        )
                    ),
                    defaultSettings = listOf(
                        CoreVariable("wswan_rotate_display", "landscape"),
                        CoreVariable("wswan_mono_palette", "wonderswan"),
                    )
                ),
            ),
            uniqueExtensions = listOf("ws"),
        ),
        GameSystem(
            GameSystemID.WSC,
            "Bandai - WonderSwan Color",
            R.string.game_system_title_wsc,
            R.string.game_system_abbr_wsc,
            listOf(
                GameSystemCoreConfig(
                    CoreID.MEDNAFEN_WSWAN,
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.WS_LANDSCAPE, ControllerTouchConfigs.WS_PORTRAIT)
                    ),
                    systemExposedSettings = listOf(
                        GameSystemExposedSetting(
                            "wswan_rotate_display",
                            R.string.setting_wswan_rotate_display,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "landscape",
                                    R.string.value_wswan_rotate_display_landscape
                                ),
                                GameSystemExposedSetting.Value(
                                    "portrait",
                                    R.string.value_wswan_rotate_display_portrait
                                ),
                            )
                        )
                    ),
                    defaultSettings = listOf(
                        CoreVariable("wswan_rotate_display", "landscape")
                    )
                ),
            ),
            uniqueExtensions = listOf("wsc"),
        ),
        GameSystem(
            GameSystemID.DOS,
            "DOS",
            R.string.game_system_title_dos,
            R.string.game_system_abbr_dos,
            listOf(
                GameSystemCoreConfig(
                    CoreID.DOSBOX_PURE,
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(
                            ControllerTouchConfigs.DOS_AUTO,
                            ControllerTouchConfigs.DOS_MOUSE_LEFT,
                            ControllerTouchConfigs.DOS_MOUSE_RIGHT,
                        )
                    ),
                    statesSupported = false,
                )
            ),
            fastForwardSupport = false,
            uniqueExtensions = listOf("dosz"),
            scanOptions = GameSystemScanOptions(
                scanByFilename = false,
                scanByUniqueExtension = true,
                scanByPathAndFilename = false,
                scanByPathAndSupportedExtensions = true
            ),
        ),
        GameSystem(
            GameSystemID.NINTENDO_3DS,
            "Nintendo - Nintendo 3DS",
            R.string.game_system_title_3ds,
            R.string.game_system_abbr_3ds,
            listOf(
                GameSystemCoreConfig(
                    CoreID.CITRA,
                    controllerConfigs = hashMapOf(
                        0 to arrayListOf(ControllerTouchConfigs.NINTENDO_3DS)
                    ),
                    defaultSettings = listOf(
                        CoreVariable("citra_use_acc_mul", "disabled"),
                        CoreVariable("citra_touch_touchscreen", "enabled"),
                        CoreVariable("citra_mouse_touchscreen", "disabled"),
                        CoreVariable("citra_render_touchscreen", "disabled"),
                        CoreVariable("citra_use_hw_shader_cache", "disabled"),
                    ),
                    systemExposedSettings = listOf(
                        GameSystemExposedSetting(
                            "citra_layout_option",
                            R.string.setting_citra_layout_option,
                            arrayListOf(
                                GameSystemExposedSetting.Value(
                                    "Default Top-Bottom Screen",
                                    R.string.value_citra_layout_option_topbottom
                                ),
                                GameSystemExposedSetting.Value(
                                    "Side by Side",
                                    R.string.value_citra_layout_option_sidebyside
                                )
                            )
                        ),
                        GameSystemExposedSetting(
                            "citra_resolution_factor",
                            R.string.setting_citra_resolution_factor
                        ),
                        GameSystemExposedSetting(
                            "citra_use_acc_mul",
                            R.string.setting_citra_use_acc_mul
                        ),
                        GameSystemExposedSetting(
                            "citra_use_acc_geo_shaders",
                            R.string.setting_citra_use_acc_geo_shaders
                        ),
                    ),
                    statesSupported = false,
                    supportsLibretroVFS = true,
                    supportedOnlyArchitectures = setOf("arm64-v8a")
                ),
            ),
            uniqueExtensions = listOf("3ds"),
        ),
    )

    private val byIdCache by lazy { mapOf(*SYSTEMS.map { it.id.dbname to it }.toTypedArray()) }

    private val byExtensionCache by lazy {
        val mutableMap = mutableMapOf<String, GameSystem>()
        for (system in SYSTEMS) {
            for (extension in system.uniqueExtensions) {
                mutableMap[extension.toLowerCase(Locale.US)] = system
            }
        }
        mutableMap.toMap()
    }

    /////////////////////////////////////////////////////////////////////////////////

    fun findById(id: String): GameSystem = byIdCache.getValue(id)

    fun all() = SYSTEMS

    fun getSupportedExtensions(): List<String> {
        return SYSTEMS.flatMap { it.supportedExtensions }
    }

    fun findSystemForCore(coreID: CoreID): List<GameSystem> {
        return all().filter { system -> system.systemCoreConfigs.any { it.coreID == coreID } }
    }

    fun findByUniqueFileExtension(fileExtension: String): GameSystem? =
        byExtensionCache[fileExtension.toLowerCase(Locale.US)]
}