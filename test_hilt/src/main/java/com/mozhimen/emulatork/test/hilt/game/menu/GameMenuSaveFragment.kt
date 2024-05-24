package com.mozhimen.emulatork.test.hilt.game.menu

import android.content.Context
import com.mozhimen.emulatork.basic.save.SaveStateManager
import com.mozhimen.emulatork.basic.save.SaveStatePreviewManager
import com.mozhimen.emulatork.ui.game.menu.AbsGameMenuSaveFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * @ClassName GameMenuSaveFragment
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/5/20 22:17
 * @Version 1.0
 */
@AndroidEntryPoint
class GameMenuSaveFragment : AbsGameMenuSaveFragment() {
    @Inject
    lateinit var saveStateManager: SaveStateManager

    @Inject
    lateinit var saveStatePreviewManager: SaveStatePreviewManager

    override fun statesManager(): SaveStateManager {
        return saveStateManager
    }

    override fun statesPreviewManager(): SaveStatePreviewManager {
        return saveStatePreviewManager
    }

//    @dagger.Module
//    class Module

}