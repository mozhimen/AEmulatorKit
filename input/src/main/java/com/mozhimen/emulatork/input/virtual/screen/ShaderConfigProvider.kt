package com.mozhimen.emulatork.input.virtual.screen

import android.content.Context
import com.mozhimen.basick.utilk.android.content.getRegGlEsVersion
import com.mozhimen.emulatork.basic.system.ESystemType
import com.swordfish.libretrodroid.ShaderConfig

/**
 * @ClassName ScreenProvider
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/31
 * @Version 1.0
 */
object ShaderConfigProvider {
    @JvmStatic
    fun getShaderBySystem(context: Context, hdMode: Boolean, forceLegacyHdMode: Boolean, screenFilter: String, eSystemType: ESystemType): ShaderConfig {
        val useNewHdMode = context.getRegGlEsVersion() >= 3 && hdMode && !forceLegacyHdMode
        return when {
            useNewHdMode -> getHDShaderBySystem(eSystemType)
            hdMode -> getLegacyHDShaderBySystem(eSystemType)
            else -> when (screenFilter) {
                "crt" -> ShaderConfig.CRT
                "lcd" -> ShaderConfig.LCD
                "smooth" -> ShaderConfig.Default
                "sharp" -> ShaderConfig.Sharp
                else -> getDefaultShaderBySystem(eSystemType)
            }
        }
    }

    @JvmStatic
    private fun getDefaultShaderBySystem(eSystemType: ESystemType): ShaderConfig {
        return when (eSystemType) {
            ESystemType.GBA -> ShaderConfig.LCD
            ESystemType.GBC -> ShaderConfig.LCD
            ESystemType.GB -> ShaderConfig.LCD
            ESystemType.N64 -> ShaderConfig.CRT
            ESystemType.GENESIS -> ShaderConfig.CRT
            ESystemType.SEGACD -> ShaderConfig.CRT
            ESystemType.NES -> ShaderConfig.CRT
            ESystemType.SNES -> ShaderConfig.CRT
            ESystemType.FBNEO -> ShaderConfig.CRT
            ESystemType.SMS -> ShaderConfig.CRT
            ESystemType.PSP -> ShaderConfig.LCD
            ESystemType.NDS -> ShaderConfig.LCD
            ESystemType.GG -> ShaderConfig.LCD
            ESystemType.ATARI2600 -> ShaderConfig.CRT
            ESystemType.PSX -> ShaderConfig.CRT
            ESystemType.MAME2003PLUS -> ShaderConfig.CRT
            ESystemType.ATARI7800 -> ShaderConfig.CRT
            ESystemType.PC_ENGINE -> ShaderConfig.CRT
            ESystemType.LYNX -> ShaderConfig.LCD
            ESystemType.DOS -> ShaderConfig.CRT
            ESystemType.NGP -> ShaderConfig.LCD
            ESystemType.NGC -> ShaderConfig.LCD
            ESystemType.WS -> ShaderConfig.LCD
            ESystemType.WSC -> ShaderConfig.LCD
            ESystemType.NINTENDO_3DS -> ShaderConfig.LCD
        }
    }

    @JvmStatic
    private fun getLegacyHDShaderBySystem(eSystemType: ESystemType): ShaderConfig.CUT {
        val upscale8BitsMobile = ShaderConfig.CUT(
            blendMinContrastEdge = 0.00f,
            blendMaxContrastEdge = 0.50f,
            blendMaxSharpness = 0.85f,
        )

        val upscale8Bits = ShaderConfig.CUT(
            blendMinContrastEdge = 0.00f,
            blendMaxContrastEdge = 0.50f,
            blendMaxSharpness = 0.75f,
        )

        val upscale16BitsMobile = ShaderConfig.CUT(
            blendMinContrastEdge = 0.10f,
            blendMaxContrastEdge = 0.60f,
            blendMaxSharpness = 0.85f,
        )

        val upscale16Bits = ShaderConfig.CUT(
            blendMinContrastEdge = 0.10f,
            blendMaxContrastEdge = 0.60f,
            blendMaxSharpness = 0.75f,
        )

        val upscale32Bits = ShaderConfig.CUT(
            blendMinContrastEdge = 0.25f,
            blendMaxContrastEdge = 0.75f,
            blendMaxSharpness = 0.75f,
        )

        val modern = ShaderConfig.CUT(
            blendMinContrastEdge = 0.25f,
            blendMaxContrastEdge = 0.75f,
            blendMaxSharpness = 0.50f,
        )

        return when (eSystemType) {
            ESystemType.GBA -> upscale16BitsMobile
            ESystemType.GBC -> upscale8BitsMobile
            ESystemType.GB -> upscale8BitsMobile
            ESystemType.N64 -> upscale32Bits
            ESystemType.GENESIS -> upscale16Bits
            ESystemType.SEGACD -> upscale16Bits
            ESystemType.NES -> upscale8Bits
            ESystemType.SNES -> upscale16Bits
            ESystemType.FBNEO -> upscale32Bits
            ESystemType.SMS -> upscale8Bits
            ESystemType.PSP -> modern
            ESystemType.NDS -> upscale32Bits
            ESystemType.GG -> upscale8BitsMobile
            ESystemType.ATARI2600 -> upscale8Bits
            ESystemType.PSX -> upscale32Bits
            ESystemType.MAME2003PLUS -> upscale32Bits
            ESystemType.ATARI7800 -> upscale8Bits
            ESystemType.PC_ENGINE -> upscale16Bits
            ESystemType.LYNX -> upscale8BitsMobile
            ESystemType.DOS -> upscale32Bits
            ESystemType.NGP -> upscale8BitsMobile
            ESystemType.NGC -> upscale8BitsMobile
            ESystemType.WS -> upscale16BitsMobile
            ESystemType.WSC -> upscale16BitsMobile
            ESystemType.NINTENDO_3DS -> modern
        }
    }

    @JvmStatic
    private fun getHDShaderBySystem(eSystemType: ESystemType): ShaderConfig.CUT2 {
        val upscale8BitsMobile = ShaderConfig.CUT2(
            blendMinContrastEdge = 0.00f,
            blendMaxContrastEdge = 0.50f,
            blendMaxSharpness = 0.85f,
        )

        val upscale8Bits = ShaderConfig.CUT2(
            blendMinContrastEdge = 0.00f,
            blendMaxContrastEdge = 0.50f,
            blendMaxSharpness = 0.75f,
        )

        val upscale16BitsMobile = ShaderConfig.CUT2(
            blendMinContrastEdge = 0.10f,
            blendMaxContrastEdge = 0.60f,
            blendMaxSharpness = 0.85f,
        )

        val upscale16Bits = ShaderConfig.CUT2(
            blendMinContrastEdge = 0.10f,
            blendMaxContrastEdge = 0.60f,
            blendMaxSharpness = 0.75f,
        )

        val upscale32Bits = ShaderConfig.CUT2(
            blendMinContrastEdge = 0.25f,
            blendMaxContrastEdge = 0.75f,
            blendMaxSharpness = 0.75f,
        )

        val modern = ShaderConfig.CUT2(
            blendMinContrastEdge = 0.25f,
            blendMaxContrastEdge = 0.75f,
            blendMaxSharpness = 0.50f,
        )

        return when (eSystemType) {
            ESystemType.GBA -> upscale16BitsMobile
            ESystemType.GBC -> upscale8BitsMobile
            ESystemType.GB -> upscale8BitsMobile
            ESystemType.N64 -> upscale32Bits
            ESystemType.GENESIS -> upscale16Bits
            ESystemType.SEGACD -> upscale16Bits
            ESystemType.NES -> upscale8Bits
            ESystemType.SNES -> upscale16Bits
            ESystemType.FBNEO -> upscale32Bits
            ESystemType.SMS -> upscale8Bits
            ESystemType.PSP -> modern
            ESystemType.NDS -> upscale32Bits
            ESystemType.GG -> upscale8BitsMobile
            ESystemType.ATARI2600 -> upscale8Bits
            ESystemType.PSX -> upscale32Bits
            ESystemType.MAME2003PLUS -> upscale32Bits
            ESystemType.ATARI7800 -> upscale8Bits
            ESystemType.PC_ENGINE -> upscale16Bits
            ESystemType.LYNX -> upscale8BitsMobile
            ESystemType.DOS -> upscale32Bits
            ESystemType.NGP -> upscale8BitsMobile
            ESystemType.NGC -> upscale8BitsMobile
            ESystemType.WS -> upscale16BitsMobile
            ESystemType.WSC -> upscale16BitsMobile
            ESystemType.NINTENDO_3DS -> modern
        }
    }
}