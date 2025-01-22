package com.mozhimen.emulatork.common.android

import androidx.fragment.app.FragmentActivity
import com.mozhimen.basick.utilk.android.view.UtilKDecorViewWrapper

/**
 * @ClassName ImmersiveActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
abstract class ImmersiveFragmentActivity : FragmentActivity() {
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus)
            UtilKDecorViewWrapper.applyImmersive(this)
    }
}
