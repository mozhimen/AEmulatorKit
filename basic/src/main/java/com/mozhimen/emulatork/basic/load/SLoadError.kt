package com.mozhimen.emulatork.basic.load

/**
 * @ClassName SGameLoaderError
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/31
 * @Version 1.0
 */
sealed class SLoadError {
    object GLIncompatible : SLoadError()
    object Generic : SLoadError()
    object LoadCore : SLoadError()
    object LoadGame : SLoadError()
    object Saves : SLoadError()
    object UnsupportedArchitecture : SLoadError()
    data class MissingBiosFiles(val missingFiles: List<String>) : SLoadError()
}
