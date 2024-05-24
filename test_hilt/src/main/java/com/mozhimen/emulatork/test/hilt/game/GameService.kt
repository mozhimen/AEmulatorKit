package com.mozhimen.emulatork.test.hilt.game

import com.mozhimen.emulatork.ui.game.AbsGameService

/**
 * @ClassName GameService
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/21
 * @Version 1.0
 */
class GameService : AbsGameService() {
    override fun  gameActivityClazz(): Class<*> {
        return GameActivity::class.java
    }
}