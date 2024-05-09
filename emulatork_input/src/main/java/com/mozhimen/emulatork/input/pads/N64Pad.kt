package com.mozhimen.emulatork.input.pads

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import com.mozhimen.emulatork.input.R
import com.mozhimen.emulatork.input.events.PadEvent
import io.reactivex.Observable

/**
 * @ClassName N64Pad
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
class N64Pad @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseGamePad(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.layout_n64, this)
    }

    override fun getEvents(): Observable<PadEvent> {
        return Observable.merge(listOf(
            getLeftStickEvents(),
            getRightStickEvents(),
            getStartEvent(),
            getDirectionEvents(),
            getActionEvents(),
            getR1Events(),
            getL1Events(),
            getL2Events()))
    }

    private fun getStartEvent(): Observable<PadEvent> {
        return findViewById<SmallSingleButton>(R.id.start)
            .getEvents()
            .compose(EventsTransformers.singleButtonMap(KeyEvent.KEYCODE_BUTTON_START))
    }

    private fun getActionEvents(): Observable<PadEvent> {
        return findViewById<ActionButtons>(R.id.actions)
            .getEvents()
            .compose(EventsTransformers.actionButtonsMap(
                KeyEvent.KEYCODE_BUTTON_Y,
                KeyEvent.KEYCODE_BUTTON_B)
            )
    }

    private fun getDirectionEvents(): Observable<PadEvent> {
        return findViewById<DirectionPad>(R.id.direction)
            .getEvents()
            .compose(EventsTransformers.directionPadMap())
    }

    private fun getLeftStickEvents(): Observable<PadEvent> {
        return findViewById<Stick>(R.id.leftanalog)
            .getEvents()
            .compose(EventsTransformers.leftStickMap())
    }

    private fun getRightStickEvents(): Observable<PadEvent> {
        return findViewById<DirectionPad>(R.id.cbuttons)
            .getEvents()
            .compose(EventsTransformers.rightStickMap())
    }

    private fun getL1Events(): Observable<PadEvent> {
        return findViewById<LargeSingleButton>(R.id.l1)
            .getEvents()
            .compose(EventsTransformers.singleButtonMap(KeyEvent.KEYCODE_BUTTON_L1))
    }

    private fun getL2Events(): Observable<PadEvent> {
        return findViewById<LargeSingleButton>(R.id.l2)
            .getEvents()
            .compose(EventsTransformers.singleButtonMap(KeyEvent.KEYCODE_BUTTON_L2))
    }

    private fun getR1Events(): Observable<PadEvent> {
        return findViewById<LargeSingleButton>(R.id.r1)
            .getEvents()
            .compose(EventsTransformers.singleButtonMap(KeyEvent.KEYCODE_BUTTON_R1))
    }
}
