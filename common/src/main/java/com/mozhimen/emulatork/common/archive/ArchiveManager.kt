package com.mozhimen.emulatork.common.archive

import android.app.Activity
import com.mozhimen.emulatork.core.type.ECoreType

/**
 * @ClassName SaveSyncManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
interface ArchiveManager {
    fun getProvider(): String
    fun getSettingsActivity(): Class<out Activity>?
    fun isSupported(): Boolean
    fun isConfigured(): Boolean
    fun getLastSyncInfo(): String
    fun getConfigInfo(): String
    suspend fun sync(eCoreTypes: Set<ECoreType>)
    fun computeSavesSpace(): String
    fun computeStatesSpace(eCoreType: ECoreType): String
}
