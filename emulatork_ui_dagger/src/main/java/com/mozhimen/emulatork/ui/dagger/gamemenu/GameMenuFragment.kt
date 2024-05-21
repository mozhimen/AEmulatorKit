package com.mozhimen.emulatork.ui.dagger.gamemenu

import android.content.Context
import com.mozhimen.emulatork.ui.gamemenu.GameMenuFragment
import dagger.android.support.AndroidSupportInjection
/**
 * @ClassName GameMenuFragment
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/5/20 22:12
 * @Version 1.0
 */
 class GameMenuFragment : GameMenuFragment() {
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    @dagger.Module
    class Module
}