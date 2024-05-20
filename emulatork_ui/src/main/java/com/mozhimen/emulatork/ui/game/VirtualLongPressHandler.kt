package com.mozhimen.emulatork.ui.game

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewConfiguration
import android.widget.ImageView
import androidx.core.view.isVisible
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.mozhimen.basick.utilk.android.util.dp2pxI
import com.mozhimen.emulatork.input.LemuroidTouchOverlayThemes
import com.mozhimen.emulatork.ui.R
import com.mozhimen.basick.utilk.android.widget.applyProgressAnimate
import com.mozhimen.basick.utilk.android.view.applyVisibleIfElseGoneAnimate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.withContext

/**
 * @ClassName VirtualLongPressHandler
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
object VirtualLongPressHandler {

    private val APPEAR_ANIMATION = (ViewConfiguration.getLongPressTimeout() * 0.1f).toLong()
    private val DISAPPEAR_ANIMATION = (ViewConfiguration.getLongPressTimeout() * 2f).toLong()
    private val LONG_PRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout().toLong()

    fun initializeTheme(gameActivity: GameActivity) {
        val palette = LemuroidTouchOverlayThemes.getGamePadTheme(longPressView(gameActivity))
        longPressIconView(gameActivity).setColorFilter(palette.textColor)
        longPressProgressBar(gameActivity).setIndicatorColor(palette.textColor)
        longPressView(gameActivity).background = buildCircleDrawable(
            gameActivity,
            palette.backgroundColor,
            palette.backgroundStrokeColor,
            palette.strokeWidthDp
        )
        longPressForegroundView(gameActivity).background = buildCircleDrawable(
            gameActivity,
            palette.normalColor,
            palette.normalStrokeColor,
            palette.strokeWidthDp
        )
    }

    private fun buildCircleDrawable(
        context: Context,
        fillColor: Int,
        strokeColor: Int,
        strokeSize: Float
    ): Drawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(fillColor)
            setStroke(strokeSize.dp2pxI(), strokeColor)
        }
    }

    suspend fun displayLoading(
        activity: GameActivity,
        iconId: Int,
        cancellation: Flow<Unit>
    ): Boolean {
        withContext(Dispatchers.Main) {
            longPressView(activity).alpha = 0f
            longPressIconView(activity).setImageResource(iconId)
            displayLongPressView(activity)
        }

        val successFlow = flow {
            delay(LONG_PRESS_TIMEOUT)
            emit(true)
        }

        val cancellationFlow = cancellation.map { false }

        val isSuccessful = merge(successFlow, cancellationFlow)
            .first()

        withContext(Dispatchers.Main) {
            if (isSuccessful) {
                longPressView(activity).isVisible = false
            }
            hideLongPressView(activity)
        }

        return isSuccessful
    }

    private fun longPressIconView(activity: GameActivity) =
        activity.findViewById<ImageView>(R.id.settings_loading_icon)

    private fun longPressProgressBar(activity: GameActivity) =
        activity.findViewById<CircularProgressIndicator>(R.id.settings_loading_progress)

    private fun longPressView(activity: GameActivity) =
        activity.findViewById<View>(R.id.settings_loading)

    private fun longPressForegroundView(activity: GameActivity) =
        activity.findViewById<View>(R.id.long_press_foreground)

    private fun displayLongPressView(activity: GameActivity) {
        longPressView(activity).applyVisibleIfElseGoneAnimate(true, APPEAR_ANIMATION)
        longPressProgressBar(activity).applyProgressAnimate(100, LONG_PRESS_TIMEOUT)
    }

    private fun hideLongPressView(activity: GameActivity) {
        longPressView(activity).applyVisibleIfElseGoneAnimate(false, DISAPPEAR_ANIMATION)
        longPressProgressBar(activity).applyProgressAnimate(0, LONG_PRESS_TIMEOUT)
    }
}
