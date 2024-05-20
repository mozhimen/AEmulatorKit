package com.mozhimen.emulatork.basic.dagger.core

import android.content.SharedPreferences
import com.mozhimen.emulatork.basic.core.CoreVariablesManager
import dagger.Lazy

/**
 * @ClassName CoreVariablesManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class CoreVariablesManagerDagger(private val sharedPreferences: Lazy<SharedPreferences>) : CoreVariablesManager(lazy { sharedPreferences.get() })
