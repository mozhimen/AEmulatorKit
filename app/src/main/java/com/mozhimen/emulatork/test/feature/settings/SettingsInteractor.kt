package com.mozhimen.emulatork.test.feature.settings

import android.content.Context

/**
 * @ClassName SettingsInteractor
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class SettingsInteractor(private val context: Context) {

    fun changeLocalStorageFolder() {
        StorageFrameworkPickerLauncher.pickFolder(context)
    }
}