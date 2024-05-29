package com.mozhimen.emulatork.common.save.sync

import android.app.Activity
import com.mozhimen.emulatork.core.ECoreId

/**
 * @ClassName SaveSyncManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
interface SaveSyncManager {
    fun getProvider(): String
    fun getSettingsActivity(): Class<out Activity>?
    fun isSupported(): Boolean
    fun isConfigured(): Boolean
    fun getLastSyncInfo(): String
    fun getConfigInfo(): String
    suspend fun sync(cores: Set<com.mozhimen.emulatork.core.ECoreId>)
    fun computeSavesSpace(): String
    fun computeStatesSpace(core: com.mozhimen.emulatork.core.ECoreId): String
}
