package com.mozhimen.emulatork.ui.hilt.settings

import com.mozhimen.emulatork.basic.storage.DirectoriesManager
import com.mozhimen.emulatork.ui.settings.StorageFrameworkPickerLauncher
import javax.inject.Inject

/**
 * @ClassName StorageFrameworkPickerLauncher
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/21
 * @Version 1.0
 */
class StorageFrameworkPickerLauncher :StorageFrameworkPickerLauncher() {
    @Inject
    lateinit var directoriesManager: DirectoriesManager

    override fun directoriesManager(): DirectoriesManager {
        return directoriesManager
    }
}