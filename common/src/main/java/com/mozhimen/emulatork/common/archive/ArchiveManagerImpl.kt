package com.mozhimen.emulatork.common.archive

import android.app.Activity
import android.content.Context
import com.mozhimen.emulatork.core.type.ECoreType
import com.mozhimen.emulatork.basic.storage.StorageDirProvider

/**
 * @ClassName SaveSyncManagerImpl
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class ArchiveManagerImpl(
    private val appContext: Context,
    private val storageProvider: StorageDirProvider
) : ArchiveManager {
    override fun getProvider(): String = ""

    override fun getSettingsActivity(): Class<out Activity>? = null

    override fun isSupported(): Boolean = false

    override fun isConfigured(): Boolean = false

    override fun getLastSyncInfo(): String = ""

    override fun getConfigInfo(): String = ""

    override suspend fun sync(eCoreTypes: Set<ECoreType>) {}

    override fun computeSavesSpace() = ""

    override fun computeStatesSpace(eCoreType: ECoreType) = ""
}
