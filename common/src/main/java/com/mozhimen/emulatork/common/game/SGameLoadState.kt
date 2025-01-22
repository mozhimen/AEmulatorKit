package com.mozhimen.emulatork.common.game

/**
 * @ClassName SGameLoadState
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/31
 * @Version 1.0
 */
sealed class SGameLoadState {
    object LoadingCore : SGameLoadState()
    object LoadingGame : SGameLoadState()
    class LoadReady(val gameBundle: GameBundle) : SGameLoadState()
}