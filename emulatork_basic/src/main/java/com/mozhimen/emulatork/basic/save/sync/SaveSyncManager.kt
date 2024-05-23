package com.mozhimen.emulatork.basic.save.sync

import android.app.Activity
import com.mozhimen.emulatork.basic.core.CoreID

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
    suspend fun sync(cores: Set<CoreID>)
    fun computeSavesSpace(): String
    fun computeStatesSpace(core: CoreID): String
}
