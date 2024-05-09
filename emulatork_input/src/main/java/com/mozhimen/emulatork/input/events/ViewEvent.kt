package com.mozhimen.emulatork.input.events

/**
 * @ClassName ViewEvent
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
sealed class ViewEvent {
    data class Button(val action: Int, val index: Int, val haptic: Boolean)
    data class Stick(val xAxis: Float, val yAxis: Float, val haptic: Boolean)
}
