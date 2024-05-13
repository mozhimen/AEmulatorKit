package com.mozhimen.emulatork.util.graphics

import androidx.core.graphics.ColorUtils
import kotlin.math.abs

/**
 * @ClassName ColorUtils
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
object ColorUtils {
    fun randomColor(
        seed: Any,
        paletteSize: Int = 128,
        saturation: Float = 0.5f,
        lightness: Float = 0.5f
    ): Int {
        val hue = abs(seed.hashCode() % paletteSize) / paletteSize.toFloat() * 360f
        return ColorUtils.HSLToColor(floatArrayOf(hue, saturation, lightness))
    }
}
