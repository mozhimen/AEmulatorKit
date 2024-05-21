package com.mozhimen.emulatork.ui.dagger.game

import android.content.Context
import dagger.android.support.AndroidSupportInjection
import com.mozhimen.emulatork.ui.game.RecyclerViewFragment
/**
 * @ClassName RecyclerViewFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/21
 * @Version 1.0
 */
open class RecyclerViewFragment:RecyclerViewFragment() {
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}