package com.mozhimen.emulatork.input

import android.graphics.Color
import android.view.View
import com.google.android.material.color.MaterialColors
import com.mozhimen.basick.utilk.android.util.UtilKTypedValue
import com.swordfish.radialgamepad.library.config.RadialGamePadTheme

/**
 * @ClassName LemuroidTouchOverlayThemes
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
object InputOverlayThemes {

    private const val ALPHA_FILL_LIGHT = 0.2f
    private const val ALPHA_FILL_STRONG = 0.2f
    private const val ALPHA_FILL_SIMULATED = 0.4f
    private const val ALPHA_FILL_PRESSED = 0.6f

    private const val ALPHA_STROKE = 0.9f
    private const val ALPHA_STROKE_LIGHT = 0.15f * ALPHA_STROKE
    private const val ALPHA_STROKE_STRONG = 0.5f * ALPHA_STROKE
    private const val ALPHA_STROKE_TEXT = 0.8f * ALPHA_STROKE

    private const val BACKGROUND_COLOR = Color.BLACK

    //////////////////////////////////////////////////////////////////

    fun getGamePadTheme(view: View): RadialGamePadTheme {
        return buildTheme(view)
    }

    //////////////////////////////////////////////////////////////////

    private fun buildTheme(view: View): RadialGamePadTheme {
        val strokeSize = UtilKTypedValue.complexToFloat(view.context, R.dimen.touch_control_stroke_size)
        val colorOnSurface = MaterialColors.getColor(view, com.google.android.material.R.attr.colorOnSurface)
        val colorPrimary = MaterialColors.getColor(view, com.google.android.material.R.attr.colorPrimary)
        val colorSecondary = MaterialColors.getColor(view, com.google.android.material.R.attr.colorSecondary)

        return RadialGamePadTheme(
            normalColor = withAlpha(BACKGROUND_COLOR, ALPHA_FILL_STRONG),
            normalStrokeColor = withAlpha(colorOnSurface, ALPHA_STROKE_STRONG),
            backgroundColor = withAlpha(BACKGROUND_COLOR, ALPHA_FILL_LIGHT),
            backgroundStrokeColor = withAlpha(colorOnSurface, ALPHA_STROKE_LIGHT),
            pressedColor = withAlpha(colorPrimary, ALPHA_FILL_PRESSED),
            textColor = withAlpha(colorOnSurface, ALPHA_STROKE_TEXT),
            simulatedColor = withAlpha(colorSecondary, ALPHA_FILL_SIMULATED),
            lightColor = withAlpha(BACKGROUND_COLOR, ALPHA_FILL_STRONG),
            lightStrokeColor = withAlpha(colorOnSurface, ALPHA_STROKE_LIGHT),
            strokeWidthDp = strokeSize
        )
    }

    private fun withAlpha(color: Int, alpha: Float): Int {
        val alphaInt = (alpha * 255).toInt()
        return MaterialColors.compositeARGBWithAlpha(color, alphaInt)
    }
}
