package com.mozhimen.emulatork.input.views.bases

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatButton
import com.jakewharton.rxrelay2.PublishRelay
import com.mozhimen.emulatork.input.events.ViewEvent
import com.mozhimen.emulatork.input.interfaces.ButtonEventsSource
import io.reactivex.Observable

/**
 * @ClassName BaseSingleButton
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
abstract class BaseSingleButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr), ButtonEventsSource {

    private val events: PublishRelay<ViewEvent.Button> = PublishRelay.create()

    init {
        setOnTouchListener { _, event -> handleTouchEvent(event); true }
    }

    private fun handleTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isPressed = true
                events.accept(ViewEvent.Button(KeyEvent.ACTION_DOWN, 0, true))
            }
            MotionEvent.ACTION_UP -> {
                isPressed = false
                events.accept(ViewEvent.Button(KeyEvent.ACTION_UP, 0, false))
            }
        }
    }

    override fun getEvents(): Observable<ViewEvent.Button> = events
}