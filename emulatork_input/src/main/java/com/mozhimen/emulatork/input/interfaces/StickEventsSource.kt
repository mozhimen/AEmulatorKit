package com.mozhimen.emulatork.input.interfaces

import com.mozhimen.emulatork.input.events.ViewEvent
import io.reactivex.Observable

/**
 * @ClassName StickEventsSource
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
interface StickEventsSource {
    fun getEvents(): Observable<ViewEvent.Stick>

    companion object {
        const val SOURCE_DPAD = 0
        const val SOURCE_LEFT_STICK = 1
        const val SOURCE_RIGHT_STICK = 2
    }
}
