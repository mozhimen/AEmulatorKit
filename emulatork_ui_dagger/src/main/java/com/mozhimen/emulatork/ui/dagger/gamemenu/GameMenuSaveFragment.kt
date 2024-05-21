package com.mozhimen.emulatork.ui.dagger.gamemenu

import android.content.Context
import com.mozhimen.emulatork.basic.saves.StatesManager
import com.mozhimen.emulatork.basic.saves.StatesPreviewManager
import com.mozhimen.emulatork.ui.gamemenu.AbsGameMenuSaveFragment
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * @ClassName GameMenuSaveFragment
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/5/20 22:17
 * @Version 1.0
 */
class GameMenuSaveFragment : AbsGameMenuSaveFragment() {
    @Inject
    lateinit var statesManager: StatesManager

    @Inject
    lateinit var statesPreviewManager: StatesPreviewManager

    override fun statesManager(): StatesManager {
        return statesManager
    }

    override fun statesPreviewManager(): StatesPreviewManager {
        return statesPreviewManager
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    @dagger.Module
    class Module

}