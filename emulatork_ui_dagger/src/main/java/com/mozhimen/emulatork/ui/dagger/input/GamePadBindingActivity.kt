package com.mozhimen.emulatork.ui.dagger.input
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mozhimen.emulatork.ui.input.GamePadBindingActivity
import com.mozhimen.emulatork.ui.input.InputDeviceManager
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * @ClassName GamePadBindingActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/21
 * @Version 1.0
 */
class GamePadBindingActivity :GamePadBindingActivity(), HasFragmentInjector, HasSupportFragmentInjector {
    @Inject
    lateinit var inputDeviceManager: InputDeviceManager

    override fun inputDeviceManager(): InputDeviceManager {
        return inputDeviceManager
    }

    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var frameworkFragmentInjector: DispatchingAndroidInjector<android.app.Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? =
        supportFragmentInjector

    override fun fragmentInjector(): AndroidInjector<android.app.Fragment>? =
        frameworkFragmentInjector


    @dagger.Module
    abstract class Module
}