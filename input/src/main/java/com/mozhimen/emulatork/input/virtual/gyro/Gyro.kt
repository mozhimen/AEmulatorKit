package com.mozhimen.emulatork.input.virtual.gyro

import com.swordfish.radialgamepad.library.RadialGamePad

/**
 * @ClassName TiltTracker
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
interface Gyro {
    fun updateTracking(xTilt: Float, yTilt: Float, pads: Sequence<RadialGamePad>)
    fun stopTracking(pads: Sequence<RadialGamePad>)
    fun trackedIds(): Set<Int>
}
