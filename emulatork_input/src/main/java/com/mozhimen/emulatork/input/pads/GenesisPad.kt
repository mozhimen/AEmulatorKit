package com.mozhimen.emulatork.input.pads

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import com.mozhimen.emulatork.input.R
import com.mozhimen.emulatork.input.events.EventsTransformers
import com.mozhimen.emulatork.input.events.PadEvent
import com.mozhimen.emulatork.input.views.ActionButtons
import com.mozhimen.emulatork.input.views.DirectionPad
import com.mozhimen.emulatork.input.views.bases.BaseSingleButton
import io.reactivex.Observable

/**
 * @ClassName GenesisPad
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
class GenesisPad @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseGamePad(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.layout_genesis, this)
    }

    override fun getEvents(): Observable<PadEvent> {
        return Observable.merge(
            getStartEvent(),
            getDirectionEvents(),
            getActionEvents()
        )
    }

    private fun getStartEvent(): Observable<PadEvent> {
        return findViewById<BaseSingleButton>(R.id.start)
            .getEvents()
            .compose(EventsTransformers.singleButtonMap(KeyEvent.KEYCODE_BUTTON_START))
    }

    private fun getActionEvents(): Observable<PadEvent> {
        return findViewById<ActionButtons>(R.id.actions)
            .getEvents()
            .compose(EventsTransformers.actionButtonsMap(KeyEvent.KEYCODE_BUTTON_A, KeyEvent.KEYCODE_BUTTON_B, KeyEvent.KEYCODE_C))
    }

    private fun getDirectionEvents(): Observable<PadEvent> {
        return findViewById<DirectionPad>(R.id.direction)
            .getEvents()
            .compose(EventsTransformers.directionPadMap())
    }
}
