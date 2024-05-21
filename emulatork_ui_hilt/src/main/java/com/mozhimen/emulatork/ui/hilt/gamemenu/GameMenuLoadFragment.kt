package com.mozhimen.emulatork.ui.hilt.gamemenu

import com.mozhimen.emulatork.basic.saves.StatesManager
import com.mozhimen.emulatork.basic.saves.StatesPreviewManager
import com.mozhimen.emulatork.ui.gamemenu.AbsGameMenuLoadFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * @ClassName GameMenuLoadFragment
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/5/20 22:15
 * @Version 1.0
 */
@AndroidEntryPoint
class GameMenuLoadFragment : AbsGameMenuLoadFragment() {
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

//    @dagger.Module
//    class Module
}