package com.mozhimen.emulatork.input.events

import com.mozhimen.emulatork.input.interfaces.StickEventsSource
import io.reactivex.ObservableTransformer

/**
 * @ClassName EventsTransformers
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
internal object EventsTransformers {
    fun actionButtonsMap(vararg keycodes: Int): ObservableTransformer<ViewEvent.Button, PadEvent> {
        return ObservableTransformer { upstream ->
            upstream.map { PadEvent.Button(it.action, keycodes[it.index], it.haptic) }
        }
    }

    fun singleButtonMap(keycode: Int): ObservableTransformer<ViewEvent.Button, PadEvent> {
        return ObservableTransformer { upstream ->
            upstream.map { PadEvent.Button(it.action, keycode, it.haptic) }
        }
    }

    fun directionPadMap() = ObservableTransformer<ViewEvent.Stick, PadEvent> { upstream ->
        upstream
            .map {
                PadEvent.Stick(StickEventsSource.SOURCE_DPAD, it.xAxis, it.yAxis, it.haptic)
            }
    }

    fun leftStickMap() = stickMap(StickEventsSource.SOURCE_LEFT_STICK)

    fun rightStickMap() = stickMap(StickEventsSource.SOURCE_RIGHT_STICK)

    private fun stickMap(eventSource: Int): ObservableTransformer<ViewEvent.Stick, PadEvent> {
        return ObservableTransformer { upstream ->
            upstream.map {
                PadEvent.Stick(eventSource, it.xAxis, it.yAxis, it.haptic)
            }
        }
    }
}
