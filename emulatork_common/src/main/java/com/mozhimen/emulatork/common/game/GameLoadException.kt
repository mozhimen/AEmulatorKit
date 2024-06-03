package com.mozhimen.emulatork.common.game

/**
 * @ClassName GameLoaderException
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class GameLoadException constructor(val sGameLoadError: SGameLoadError) : RuntimeException("Game Loader error: $sGameLoadError")
