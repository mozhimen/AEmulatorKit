package com.mozhimen.emulatork.basic.game.shader

import android.content.Context
import com.mozhimen.basick.utilk.android.content.getRegGlEsVersion
import com.mozhimen.emulatork.basic.game.system.GameSystem
import com.mozhimen.emulatork.basic.game.system.GameSystemID
import com.swordfish.libretrodroid.ShaderConfig

/**
 * @ClassName ShaderChooser
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
object GameShaderChooser {

    fun getShaderForSystem(
        context: Context,
        hdMode: Boolean,
        forceLegacyHdMode: Boolean,
        screenFilter: String,
        system: GameSystem
    ): ShaderConfig {
        val useNewHdMode = context.getRegGlEsVersion() >= 3 && hdMode && !forceLegacyHdMode
        return when {
            useNewHdMode -> getHDShaderForSystem(system)
            hdMode -> getLegacyHDShaderForSystem(system)
            else -> when (screenFilter) {
                "crt" -> ShaderConfig.CRT
                "lcd" -> ShaderConfig.LCD
                "smooth" -> ShaderConfig.Default
                "sharp" -> ShaderConfig.Sharp
                else -> getDefaultShaderForSystem(system)
            }
        }
    }

    private fun getDefaultShaderForSystem(system: GameSystem): ShaderConfig {
        return when (system.id) {
            GameSystemID.GBA -> ShaderConfig.LCD
            GameSystemID.GBC -> ShaderConfig.LCD
            GameSystemID.GB -> ShaderConfig.LCD
            GameSystemID.N64 -> ShaderConfig.CRT
            GameSystemID.GENESIS -> ShaderConfig.CRT
            GameSystemID.SEGACD -> ShaderConfig.CRT
            GameSystemID.NES -> ShaderConfig.CRT
            GameSystemID.SNES -> ShaderConfig.CRT
            GameSystemID.FBNEO -> ShaderConfig.CRT
            GameSystemID.SMS -> ShaderConfig.CRT
            GameSystemID.PSP -> ShaderConfig.LCD
            GameSystemID.NDS -> ShaderConfig.LCD
            GameSystemID.GG -> ShaderConfig.LCD
            GameSystemID.ATARI2600 -> ShaderConfig.CRT
            GameSystemID.PSX -> ShaderConfig.CRT
            GameSystemID.MAME2003PLUS -> ShaderConfig.CRT
            GameSystemID.ATARI7800 -> ShaderConfig.CRT
            GameSystemID.PC_ENGINE -> ShaderConfig.CRT
            GameSystemID.LYNX -> ShaderConfig.LCD
            GameSystemID.DOS -> ShaderConfig.CRT
            GameSystemID.NGP -> ShaderConfig.LCD
            GameSystemID.NGC -> ShaderConfig.LCD
            GameSystemID.WS -> ShaderConfig.LCD
            GameSystemID.WSC -> ShaderConfig.LCD
            GameSystemID.NINTENDO_3DS -> ShaderConfig.LCD
        }
    }

    private fun getLegacyHDShaderForSystem(system: GameSystem): ShaderConfig.CUT {
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

        return when (system.id) {
            GameSystemID.GBA -> upscale16BitsMobile
            GameSystemID.GBC -> upscale8BitsMobile
            GameSystemID.GB -> upscale8BitsMobile
            GameSystemID.N64 -> upscale32Bits
            GameSystemID.GENESIS -> upscale16Bits
            GameSystemID.SEGACD -> upscale16Bits
            GameSystemID.NES -> upscale8Bits
            GameSystemID.SNES -> upscale16Bits
            GameSystemID.FBNEO -> upscale32Bits
            GameSystemID.SMS -> upscale8Bits
            GameSystemID.PSP -> modern
            GameSystemID.NDS -> upscale32Bits
            GameSystemID.GG -> upscale8BitsMobile
            GameSystemID.ATARI2600 -> upscale8Bits
            GameSystemID.PSX -> upscale32Bits
            GameSystemID.MAME2003PLUS -> upscale32Bits
            GameSystemID.ATARI7800 -> upscale8Bits
            GameSystemID.PC_ENGINE -> upscale16Bits
            GameSystemID.LYNX -> upscale8BitsMobile
            GameSystemID.DOS -> upscale32Bits
            GameSystemID.NGP -> upscale8BitsMobile
            GameSystemID.NGC -> upscale8BitsMobile
            GameSystemID.WS -> upscale16BitsMobile
            GameSystemID.WSC -> upscale16BitsMobile
            GameSystemID.NINTENDO_3DS -> modern
        }
    }

    private fun getHDShaderForSystem(system: GameSystem): ShaderConfig.CUT2 {
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

        return when (system.id) {
            GameSystemID.GBA -> upscale16BitsMobile
            GameSystemID.GBC -> upscale8BitsMobile
            GameSystemID.GB -> upscale8BitsMobile
            GameSystemID.N64 -> upscale32Bits
            GameSystemID.GENESIS -> upscale16Bits
            GameSystemID.SEGACD -> upscale16Bits
            GameSystemID.NES -> upscale8Bits
            GameSystemID.SNES -> upscale16Bits
            GameSystemID.FBNEO -> upscale32Bits
            GameSystemID.SMS -> upscale8Bits
            GameSystemID.PSP -> modern
            GameSystemID.NDS -> upscale32Bits
            GameSystemID.GG -> upscale8BitsMobile
            GameSystemID.ATARI2600 -> upscale8Bits
            GameSystemID.PSX -> upscale32Bits
            GameSystemID.MAME2003PLUS -> upscale32Bits
            GameSystemID.ATARI7800 -> upscale8Bits
            GameSystemID.PC_ENGINE -> upscale16Bits
            GameSystemID.LYNX -> upscale8BitsMobile
            GameSystemID.DOS -> upscale32Bits
            GameSystemID.NGP -> upscale8BitsMobile
            GameSystemID.NGC -> upscale8BitsMobile
            GameSystemID.WS -> upscale16BitsMobile
            GameSystemID.WSC -> upscale16BitsMobile
            GameSystemID.NINTENDO_3DS -> modern
        }
    }
}
