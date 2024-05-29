package com.mozhimen.emulatork.common.save.sync

import android.app.Activity
import android.content.Context
import com.mozhimen.emulatork.core.ECoreId
import com.mozhimen.emulatork.basic.storage.StorageProvider

/**
 * @ClassName SaveSyncManagerImpl
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class SaveSyncManagerImpl(
    private val appContext: Context,
    private val storageProvider: StorageProvider
) : SaveSyncManager {
    override fun getProvider(): String = ""

    override fun getSettingsActivity(): Class<out Activity>? = null

    override fun isSupported(): Boolean = false

    override fun isConfigured(): Boolean = false

    override fun getLastSyncInfo(): String = ""

    override fun getConfigInfo(): String = ""

    override suspend fun sync(cores: Set<com.mozhimen.emulatork.core.ECoreId>) {}

    override fun computeSavesSpace() = ""

    override fun computeStatesSpace(coreID: com.mozhimen.emulatork.core.ECoreId) = ""
}
