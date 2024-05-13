package com.mozhimen.emulatork.basic.game

/**
 * @ClassName GameLoaderException
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class GameLoaderException(val error: GameLoaderError) : RuntimeException("Game Loader error: $error")

sealed class GameLoaderError {
    object GLIncompatible : GameLoaderError()
    object Generic : GameLoaderError()
    object LoadCore : GameLoaderError()
    object LoadGame : GameLoaderError()
    object Saves : GameLoaderError()
    object UnsupportedArchitecture : GameLoaderError()
    data class MissingBiosFiles(val missingFiles: List<String>) : GameLoaderError()
}
