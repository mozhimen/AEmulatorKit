package com.mozhimen.emulatork.input.pads

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.mozhimen.emulatork.input.events.PadEvent
import io.reactivex.Observable

/**
 * @ClassName BaseGamePad
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
abstract class BaseGamePad @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    abstract fun getEvents(): Observable<PadEvent>
}
