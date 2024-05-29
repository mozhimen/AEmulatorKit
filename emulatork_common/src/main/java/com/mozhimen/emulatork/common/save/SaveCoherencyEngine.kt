package com.mozhimen.emulatork.common.save

import com.mozhimen.emulatork.core.ECoreId
import com.mozhimen.emulatork.basic.game.db.entities.Game

/**
 * @ClassName SavesCoherencyEngine
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
/*
   Why does this class exist? Because shit happens and we want to make sure we are prepared.
   This is the issue:

   User enables auto-save, plays, disables auto-save, plays for 10h, saves in game, re-enables
   auto-save and loses 10h worth of game.

   If we detect a more recent SRAM file, we basically avoid loading the state. This is also handy,
   if different cores share the same SRAM file.

    为什么这个类存在?
    因为糟糕的事情发生了，我们要确保我们准备好了。
    这就是问题所在:用户开启自动保存，游戏，关闭自动保存，游戏10小时，保存在游戏中，重新开启自动保存，失去10小时的游戏价值。
    如果我们检测到一个较新的SRAM文件，我们基本上会避免加载状态。
    如果不同的内核共享相同的SRAM文件，这也很方便。
*/
class SaveCoherencyEngine(val saveManager: SaveManager, val saveStateManager: SaveStateManager) {
    companion object {
        private const val TOLERANCE = 30L * 1000L
    }

    //////////////////////////////////////////////////////////////////////////

    suspend fun shouldDiscardAutoSaveState(game: Game, coreID: com.mozhimen.emulatork.core.ECoreId): Boolean {
        val autoSRAM = saveManager.getSaveRAMInfo(game)
        val autoSave = saveStateManager.getAutoSaveInfo(game, coreID)
        return autoSRAM.exists && autoSave.exists && autoSRAM.date > autoSave.date + TOLERANCE
    }
}

