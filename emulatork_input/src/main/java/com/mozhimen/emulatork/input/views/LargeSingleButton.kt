package com.mozhimen.emulatork.input.views

import android.content.Context
import android.util.AttributeSet
import com.mozhimen.emulatork.input.R
import com.mozhimen.emulatork.input.views.bases.BaseSingleButton

/**
 * @ClassName LargeSingleButton
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
class LargeSingleButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseSingleButton(context, attrs, defStyleAttr) {

    init {
        setBackgroundResource(R.drawable.large_button_selector)
    }
}
