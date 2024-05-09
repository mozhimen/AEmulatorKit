package com.mozhimen.emulatork.input.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.jakewharton.rxrelay2.PublishRelay
import com.mozhimen.emulatork.input.R
import com.mozhimen.emulatork.input.events.ViewEvent
import com.mozhimen.emulatork.input.interfaces.ButtonEventsSource
import io.reactivex.Observable
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * @ClassName ActionButtons
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
class ActionButtons @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), ButtonEventsSource {

    private val events: PublishRelay<ViewEvent.Button> = PublishRelay.create()

    private var spacing: Float = 0.1f
    private var rows: Int = 2
    private var cols: Int = 2
    private var rotateButtons: Float = 0.0f
    private var supportsMultipleInputs: Boolean = false

    private var rotatedSize: Float = 0f
    private var notRotatedSize: Float = 0f

    private var xPadding: Float = 0f
    private var yPadding: Float = 0f
    private var buttonDrawableSize: Int = 0
    private var buttonSize: Int = 0

    private val buttonsPressed = mutableSetOf<Int>()

    private var pressedDrawable: Drawable?
    private var normalDrawable: Drawable?

    private val touchRotationMatrix = Matrix()

    init {
        pressedDrawable = retrieveDrawable(R.drawable.action_pressed)
        normalDrawable = retrieveDrawable(R.drawable.action_normal)

        context.theme.obtainStyledAttributes(attrs, R.styleable.ActionButtons, defStyleAttr, 0)?.let {
            initializeFromAttributes(it)
        }
    }

    override fun getEvents(): Observable<ViewEvent.Button> = events

    private fun initializeFromAttributes(a: TypedArray) {
        supportsMultipleInputs = a.getBoolean(R.styleable.ActionButtons_multipleInputs, false)
        rows = a.getInt(R.styleable.ActionButtons_rows, 2)
        cols = a.getInt(R.styleable.ActionButtons_cols, 2)
        spacing = a.getFloat(R.styleable.ActionButtons_spacing, 0.1f)
        rotateButtons = a.getFloat(R.styleable.ActionButtons_rotateButtons, 0.0f)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val notRotatedWidth = getSize(
            MeasureSpec.getMode(widthMeasureSpec),
            MeasureSpec.getSize(widthMeasureSpec)
        ).toFloat()

        val notRotatedHeight = getSize(
            MeasureSpec.getMode(heightMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        ).toFloat()

        notRotatedSize = minOf(notRotatedWidth, notRotatedHeight)

        val radians = Math.toRadians(rotateButtons.toDouble()).toFloat()

        rotatedSize = (notRotatedSize) / (cos(radians) + sin(radians))

        buttonSize = minOf(rotatedSize / rows, rotatedSize / cols).roundToInt()
        buttonDrawableSize = (buttonSize * 0.9).roundToInt()

        val buttonSizePadding = (buttonSize - buttonDrawableSize) * 0.5f
        val rotationPadding = (notRotatedSize - rotatedSize) * 0.5f
        xPadding = buttonSizePadding + rotationPadding + buttonSize * 0.5f * (abs(cols - maxOf(rows, cols)))
        yPadding = buttonSizePadding + rotationPadding + buttonSize * 0.5f * (abs(rows - maxOf(rows, cols)))

        setMeasuredDimension(notRotatedSize.roundToInt(), notRotatedSize.roundToInt())
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
        touchRotationMatrix.setRotate(rotateButtons, width / 2f, height / 2f)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        canvas.rotate(-rotateButtons, width / 2f, height / 2f)

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val index = toIndex(row, col)

                val drawable = if (index in buttonsPressed) { pressedDrawable } else { normalDrawable }

                drawable?.let {
                    val height = buttonDrawableSize
                    val width = buttonDrawableSize
                    val left = (xPadding + col * buttonSize).toInt()
                    val top = (yPadding + row * buttonSize).toInt()

                    Timber.d("Drawing drawable: $width $height $left $top")

                    drawable.setBounds(left, top, left + width, top + height)
                    drawable.draw(canvas)
                }
            }
        }

        canvas.restore()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_MOVE -> {
                handleTouchEvent(event.x, event.y)
                return true
            }
            MotionEvent.ACTION_UP -> {
                allKeysReleased()
                invalidate()
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    private fun handleTouchEvent(originalX: Float, originalY: Float) {
        val point = floatArrayOf(originalX, originalY)

        touchRotationMatrix.mapPoints(point)

        val x = (point[0] - xPadding)
        val y = (point[1] - yPadding)

        val isXInRange = x in (0f..notRotatedSize)
        val isYInRange = y in (0f..notRotatedSize)

        if (isXInRange && isYInRange) {
            val col = floor(x / buttonSize).toInt()
            val row = floor(y / buttonSize).toInt()

            if (col in (0 until cols) && row in (0 until rows)) {
                val index = toIndex(row, col)

                if (buttonsPressed.contains(index).not()) {
                    if (supportsMultipleInputs.not()) {
                        allKeysReleased()
                    }
                    onKeyPressed(index)
                }
                postInvalidate()
            }
        }
    }

    private fun toIndex(row: Int, col: Int) = row * cols + col

    private fun onKeyPressed(index: Int) {
        buttonsPressed.add(index)
        events.accept(ViewEvent.Button(KeyEvent.ACTION_DOWN, index, true))
    }

    private fun allKeysReleased() {
        buttonsPressed.map { events.accept(ViewEvent.Button(KeyEvent.ACTION_UP, it, false)) }
        buttonsPressed.clear()
    }

    private fun retrieveDrawable(drawableId: Int): Drawable? {
        return AppCompatResources.getDrawable(context, drawableId)
    }
}
