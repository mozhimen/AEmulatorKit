package com.mozhimen.emulatork.test.shared

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView

/**
 * @ClassName SquaredMaterialCardView
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class SquaredMaterialCardView : MaterialCardView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}