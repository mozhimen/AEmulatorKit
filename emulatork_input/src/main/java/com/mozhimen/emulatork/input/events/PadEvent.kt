package com.mozhimen.emulatork.input.events

/**
 * @ClassName PadEvent
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
sealed class PadEvent: HapticEvent {
    data class Button(
        val action: Int,
        val keycode: Int,
        override val haptic: Boolean
    ): PadEvent(), HapticEvent

    data class Stick(
        val source: Int,
        val xAxis: Float,
        val yAxis: Float,
        override val haptic: Boolean
    ): PadEvent(), HapticEvent
}