package com.mozhimen.emulatork.input.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.jakewharton.rxrelay2.PublishRelay
import com.mozhimen.emulatork.input.R
import com.mozhimen.emulatork.input.events.ViewEvent
import com.mozhimen.emulatork.input.interfaces.StickEventsSource
import io.reactivex.Observable
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * @ClassName DirectionPad
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
class DirectionPad @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), StickEventsSource {

    companion object {

        private const val DRAWABLE_SIZE_SCALING = 0.75
        private const val BUTTON_COUNT = 8
        private const val ROTATE_BUTTONS = 22.5f
        private const val SINGLE_BUTTON_ANGLE = 360f / BUTTON_COUNT

        const val BUTTON_RIGHT = 0
        const val BUTTON_DOWN_RIGHT = 1
        const val BUTTON_DOWN = 2
        const val BUTTON_DOWN_LEFT = 3
        const val BUTTON_LEFT = 4
        const val BUTTON_UP_LEFT = 5
        const val BUTTON_UP = 6
        const val BUTTON_UP_RIGHT = 7
    }

    private var deadZone: Float = 0f
    private var buttonCenterDistance: Float = 0f

    private val touchRotationMatrix = Matrix()
    private var radius: Int = 0

    private val normalDrawables: Map<Int, Drawable?> = initNormalDrawables()
    private val pressedDrawables: Map<Int, Drawable?> = initPressedDrawables()

    private val events: PublishRelay<ViewEvent.Stick> = PublishRelay.create()

    private var currentIndex: Int? = null

    private var drawableSize: Int = 0

    init {
        val padStyleable = R.styleable.DirectionPad
        val defaultStyle = R.style.default_directionpad
        context.theme.obtainStyledAttributes(attrs, padStyleable, defStyleAttr, defaultStyle).let {
            initializeFromAttributes(it)
        }
    }

    override fun getEvents(): Observable<ViewEvent.Stick> = events.distinctUntilChanged()

    private fun initializeFromAttributes(a: TypedArray) {
        deadZone = a.getFloat(R.styleable.DirectionPad_deadZone, 0f)
        buttonCenterDistance = a.getFloat(R.styleable.DirectionPad_buttonCenterDistance, 0f)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = getSize(MeasureSpec.getMode(widthMeasureSpec), MeasureSpec.getSize(widthMeasureSpec))
        val height = getSize(MeasureSpec.getMode(heightMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))

        val diameter = minOf(width, height)
        setMeasuredDimension(diameter, diameter)
        radius = diameter / 2

        drawableSize = (radius * DRAWABLE_SIZE_SCALING).roundToInt()
    }

    private fun getSize(widthMode: Int, widthSize: Int): Int {
        return when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            else -> minOf(resources.getDimension(R.dimen.default_dial_size).toInt(), widthSize)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        touchRotationMatrix.reset()
        touchRotationMatrix.setRotate(ROTATE_BUTTONS)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val pressedButtons = convertDiagonals(currentIndex)

        for (i in 0..BUTTON_COUNT) {
            val cAngle = SINGLE_BUTTON_ANGLE * i

            val isPressed = i in pressedButtons

            getStateDrawable(i, isPressed)?.let {
                val height = drawableSize
                val width = drawableSize
                val angle = Math.toRadians((cAngle - ROTATE_BUTTONS + SINGLE_BUTTON_ANGLE / 2f).toDouble())
                val left = (radius * buttonCenterDistance * cos(angle) + radius).toInt() - width / 2
                val top = (radius * buttonCenterDistance * sin(angle) + radius).toInt() - height / 2

                it.setBounds(left, top, left + width, top + height)
                it.draw(canvas)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_MOVE -> {
                handleTouchEvent(event.x - radius, event.y - radius)
                return true
            }
            MotionEvent.ACTION_UP -> {
                currentIndex = null
                events.accept(ViewEvent.Stick(0.0f, 0.0f, false))
                invalidate()
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    private fun handleTouchEvent(originalX: Float, originalY: Float) {
        val point = floatArrayOf(originalX, originalY)

        if (isOutsideDeadzone(point[0], point[1])) {
            touchRotationMatrix.mapPoints(point)
            val x = point[0]
            val y = point[1]

            val angle = (atan2(y, x) * 180 / Math.PI + 360f) % 360f
            val index = floor(angle / SINGLE_BUTTON_ANGLE).toInt()

            currentIndex = index
            events.accept(ViewEvent.Stick(
                cos(index * Math.toRadians(SINGLE_BUTTON_ANGLE.toDouble())).toFloat(),
                sin(index * Math.toRadians(SINGLE_BUTTON_ANGLE.toDouble())).toFloat(),
                true
            ))

            postInvalidate()
        }
    }

    private fun isOutsideDeadzone(x: Float, y: Float): Boolean {
        return x * x + y * y > radius * deadZone * radius * deadZone
    }

    private fun getStateDrawable(buttonIndex: Int, isPressed: Boolean): Drawable? {
        val drawables = if (isPressed) { pressedDrawables } else { normalDrawables }
        return drawables[buttonIndex]
    }

    private fun initNormalDrawables(): Map<Int, Drawable?> {
        return mapOf(
            BUTTON_RIGHT to ContextCompat.getDrawable(context, R.drawable.direction_right_normal),
            BUTTON_UP to ContextCompat.getDrawable(context, R.drawable.direction_up_normal),
            BUTTON_LEFT to ContextCompat.getDrawable(context, R.drawable.direction_left_normal),
            BUTTON_DOWN to ContextCompat.getDrawable(context, R.drawable.direction_down_normal)
        )
    }

    private fun initPressedDrawables(): Map<Int, Drawable?> {
        return mapOf(
            BUTTON_RIGHT to ContextCompat.getDrawable(context, R.drawable.direction_right_pressed),
            BUTTON_UP to ContextCompat.getDrawable(context, R.drawable.direction_up_pressed),
            BUTTON_LEFT to ContextCompat.getDrawable(context, R.drawable.direction_left_pressed),
            BUTTON_DOWN to ContextCompat.getDrawable(context, R.drawable.direction_down_pressed)
        )
    }

    private fun convertDiagonals(currentIndex: Int?): Set<Int> {
        return when (currentIndex) {
            BUTTON_DOWN_RIGHT -> setOf(BUTTON_DOWN, BUTTON_RIGHT)
            BUTTON_DOWN_LEFT -> setOf(BUTTON_DOWN, BUTTON_LEFT)
            BUTTON_UP_LEFT -> setOf(BUTTON_UP, BUTTON_LEFT)
            BUTTON_UP_RIGHT -> setOf(BUTTON_UP, BUTTON_RIGHT)
            null -> setOf()
            else -> setOf(currentIndex)
        }
    }
}
