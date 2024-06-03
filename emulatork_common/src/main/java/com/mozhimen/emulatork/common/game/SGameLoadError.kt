package com.mozhimen.emulatork.common.game

/**
 * @ClassName SGameLoaderError
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/31
 * @Version 1.0
 */
sealed class SGameLoadError {
    object GLIncompatible : SGameLoadError()
    object Generic : SGameLoadError()
    object LoadCore : SGameLoadError()
    object LoadGame : SGameLoadError()
    object Saves : SGameLoadError()
    object UnsupportedArchitecture : SGameLoadError()
    data class MissingBiosFiles(val missingFiles: List<String>) : SGameLoadError()
}
