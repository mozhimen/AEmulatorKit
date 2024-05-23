package com.mozhimen.emulatork.ui.dagger.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mozhimen.emulatork.basic.storage.StorageDirectoriesManager
import com.mozhimen.emulatork.ui.dagger.works.WorkLibraryIndex
import com.mozhimen.emulatork.ui.settings.AbsStorageFrameworkPickerActivity
import com.mozhimen.emulatork.ui.works.AbsWorkLibraryIndex
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * @ClassName StorageFrameworkPickerLauncher
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/21
 * @Version 1.0
 */
class StorageFrameworkPickerActivity :AbsStorageFrameworkPickerActivity(), HasFragmentInjector, HasSupportFragmentInjector {
    @Inject
    lateinit var storageDirectoriesManager: StorageDirectoriesManager
    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var frameworkFragmentInjector: DispatchingAndroidInjector<android.app.Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? = supportFragmentInjector

    override fun fragmentInjector(): AndroidInjector<android.app.Fragment>? = frameworkFragmentInjector

    override fun directoriesManager(): StorageDirectoriesManager {
        return storageDirectoriesManager
    }

    override fun workLibraryIndexClazz(): Class<out AbsWorkLibraryIndex> {
        return WorkLibraryIndex::class.java
    }
}