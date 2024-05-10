package com.mozhimen.emulatork.basic.logging

import com.jakewharton.rxrelay2.ReplayRelay
import io.reactivex.Observable
import timber.log.Timber

/**
 * @ClassName RxTimberTree
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class RxTimberTree : Timber.DebugTree() {

    private val relay = ReplayRelay.createWithSize<LogEntry>(500)

    val observable: Observable<LogEntry> = relay.hide()

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        relay.accept(LogEntry(System.currentTimeMillis(), priority, tag, message, t))
    }

    data class LogEntry(
        val timestamp: Long,
        val priority: Int,
        val tag: String?,
        val message: String?,
        val error: Throwable?
    )
}
