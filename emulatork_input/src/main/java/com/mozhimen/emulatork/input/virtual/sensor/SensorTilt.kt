package com.mozhimen.emulatork.input.virtual.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import com.mozhimen.basick.utilk.android.hardware.UtilKSensorManager
import com.mozhimen.basick.utilk.android.view.UtilKDisplay
import com.mozhimen.basick.utilk.kotlin.math.UtilKMathInterpolation
import com.mozhimen.basick.utilk.kotlin.properties.UtilKReadWriteProperty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.sign

/**
 * @ClassName TiltSensor
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class SensorTilt(private val context: Context) : SensorEventListener {

    companion object {
        const val SKIPPED_MEASUREMENTS = 1
        const val MEASUREMENTS_BUFFER_SIZE = 5
        val MAX_MAX_ROTATION = Math.toRadians(20.0).toFloat()
        val MIN_MAX_ROTATION = Math.toRadians(2.5).toFloat()
    }

    ////////////////////////////////////////////////////////////////////////////////

    private val sensorManager = UtilKSensorManager.get(context)

    private val restOrientationsBuffer = mutableListOf<FloatArray>()
    private var restOrientation: FloatArray? = null

    private val tiltEvents = MutableStateFlow<FloatArray?>(null)

    private val rotationMatrix = FloatArray(9)
    private val remappedRotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private var maxRotation: Float = MAX_MAX_ROTATION
    private var deadZone: Float = 0.1f * maxRotation

    ////////////////////////////////////////////////////////////////////////////////

    var shouldRun: Boolean by UtilKReadWriteProperty.onChangeObservable(false) { onRunStateChanged() }

    var isAllowedToRun: Boolean by UtilKReadWriteProperty.onChangeObservable(false) { onRunStateChanged() }

    ////////////////////////////////////////////////////////////////////////////////

    init {
        setSensitivity(0.5f)
    }

    ////////////////////////////////////////////////////////////////////////////////

    fun getTiltEvents(): Flow<FloatArray> = tiltEvents.filterNotNull()

    fun setSensitivity(sensitivity: Float) {
        maxRotation = UtilKMathInterpolation.get_ofLinear(sensitivity, MAX_MAX_ROTATION, MIN_MAX_ROTATION)
        deadZone = maxRotation * 0.1f
        com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.d(TAG,"Setting tilt sensitivity max angle: ${Math.toDegrees(maxRotation.toDouble())}")
    }

    fun isAvailable(): Boolean {
        return sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR) != null
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing here
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            onNewRotationVector(event.values)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    private fun onRunStateChanged() {
        if (shouldRun && isAllowedToRun) {
            start()
        } else if (shouldRun && !isAllowedToRun) {
            pause()
        } else {
            stop()
        }
    }

    private fun start() {
        sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)?.also { magneticField ->
            sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    private fun pause() {
        sensorManager.unregisterListener(this)
    }

    private fun stop() {
        pause()
        restOrientation = null
        restOrientationsBuffer.clear()
    }

    private fun onNewRotationVector(rotationVector: FloatArray) {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)

        val (xAxis, yAxis) = getAxisRemapForDisplayRotation()

        SensorManager.remapCoordinateSystem(rotationMatrix, xAxis, yAxis, remappedRotationMatrix)
        SensorManager.getOrientation(remappedRotationMatrix, orientationAngles)

        val xRotation = chooseBestAngleRepresentation(orientationAngles[1], Math.PI.toFloat())
        val yRotation = chooseBestAngleRepresentation(orientationAngles[2], Math.PI.toFloat())

        if (restOrientation == null && restOrientationsBuffer.size < MEASUREMENTS_BUFFER_SIZE) {
            restOrientationsBuffer.add(floatArrayOf(yRotation, xRotation))
        } else if (restOrientation == null && restOrientationsBuffer.size >= MEASUREMENTS_BUFFER_SIZE) {
            val restMeasurements = restOrientationsBuffer.drop(1)
            restOrientation = floatArrayOf(
                restMeasurements.map { it[0] }.sum() / restMeasurements.size,
                restMeasurements.map { it[1] }.sum() / restMeasurements.size
            )
        } else {
            val x = clamp(applyDeadZone(yRotation - restOrientation!![0], deadZone) / (maxRotation))
            val y = clamp(-applyDeadZone(xRotation - restOrientation!![1], deadZone) / (maxRotation))
            tiltEvents.value = (floatArrayOf(x, y))
        }
    }

    private fun getAxisRemapForDisplayRotation(): Pair<Int, Int> {
        return when (UtilKDisplay.getRotation_ofDef(context)) {
            Surface.ROTATION_0 -> SensorManager.AXIS_X to SensorManager.AXIS_Y
            Surface.ROTATION_90 -> SensorManager.AXIS_Y to SensorManager.AXIS_MINUS_X
            Surface.ROTATION_270 -> SensorManager.AXIS_MINUS_Y to SensorManager.AXIS_X
            Surface.ROTATION_180 -> SensorManager.AXIS_MINUS_X to SensorManager.AXIS_MINUS_Y
            else -> SensorManager.AXIS_X to SensorManager.AXIS_Y
        }
    }

    private fun chooseBestAngleRepresentation(x: Float, offset: Float): Float {
        return sequenceOf(x, x + offset, x - offset).minByOrNull { abs(it) }!!
    }

    private fun applyDeadZone(x: Float, deadzone: Float): Float {
        return if (abs(x) < deadzone) {
            0f
        } else x - sign(x) * deadzone
    }

    private fun clamp(x: Float): Float {
        return maxOf(minOf(x, 1f), -1f)
    }
}
