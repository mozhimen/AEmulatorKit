package com.mozhimen.emulatork.basic.save.sync

import android.app.Activity
import android.content.Context
import com.mozhimen.emulatork.basic.core.CoreID
import com.mozhimen.emulatork.basic.storage.StorageDirectoriesManager

/**
 * @ClassName SaveSyncManagerImpl
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class SaveSyncManagerImpl(
    private val appContext: Context,
    private val storageDirectoriesManager: StorageDirectoriesManager
) : SaveSyncManager {
    override fun getProvider(): String = ""

    override fun getSettingsActivity(): Class<out Activity>? = null

    override fun isSupported(): Boolean = false

    override fun isConfigured(): Boolean = false

    override fun getLastSyncInfo(): String = ""

    override fun getConfigInfo(): String = ""

    override suspend fun sync(cores: Set<CoreID>) {}

    override fun computeSavesSpace() = ""

    override fun computeStatesSpace(coreID: CoreID) = ""
}
