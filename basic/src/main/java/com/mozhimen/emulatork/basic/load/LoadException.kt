package com.mozhimen.emulatork.basic.load

/**
 * @ClassName GameLoaderException
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class LoadException constructor(val sLoadError: SLoadError) : RuntimeException("Game Loader error: $sLoadError")
