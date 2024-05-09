package com.mozhimen.emulatork.input.interfaces

import com.mozhimen.emulatork.input.events.ViewEvent
import io.reactivex.Observable

/**
 * @ClassName ButtonEventsSource
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
interface ButtonEventsSource {
    fun getEvents(): Observable<ViewEvent.Button>
}