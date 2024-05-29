package com.mozhimen.emulatork.ui.hilt.settings

import com.mozhimen.emulatork.basic.storage.StorageProvider
import com.mozhimen.emulatork.test.hilt.works.WorkLibraryIndex
import com.mozhimen.emulatork.ui.settings.AbsStorageFrameworkPickerActivity
import com.mozhimen.emulatork.ui.works.AbsWorkLibraryIndex
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * @ClassName StorageFrameworkPickerLauncher
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/21
 * @Version 1.0
 */
@AndroidEntryPoint
class StorageFrameworkPickerActivity :AbsStorageFrameworkPickerActivity() {
    @Inject
    lateinit var storageProvider: StorageProvider

    override fun directoriesManager(): StorageProvider {
        return storageProvider
    }

    override fun workLibraryIndexClazz(): Class<out AbsWorkLibraryIndex> {
        return WorkLibraryIndex::class.java
    }
}