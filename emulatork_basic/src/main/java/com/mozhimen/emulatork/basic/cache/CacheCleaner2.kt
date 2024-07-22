package com.mozhimen.emulatork.basic.cache

import com.mozhimen.basick.utilk.kotlin.gigaBytes
import com.mozhimen.basick.utilk.kotlin.megaBytes
import com.mozhimen.iok.cache.BaseIOKCacheCleaner

/**
 * @ClassName CacheCleaner2
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/7/22
 * @Version 1.0
 */
object CacheCleaner2 : BaseIOKCacheCleaner() {
    override val MIN_CACHE_LIMIT: Long
        get() = 64L.megaBytes()
    override val MAX_CACHE_LIMIT: Long
        get() = 10L.gigaBytes()
}