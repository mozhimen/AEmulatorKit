package com.mozhimen.emulatork.common.dagger.android

import android.content.Context
import dagger.android.support.AndroidSupportInjection
import com.mozhimen.emulatork.common.android.RecyclerViewFragment
/**
 * @ClassName RecyclerViewFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/21
 * @Version 1.0
 */
open class DaggerRecyclerViewFragment: RecyclerViewFragment() {
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}