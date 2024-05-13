package com.mozhimen.emulatork.util.coroutines

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @ClassName LifecycleUtil
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
fun Fragment.launchOnState(state: Lifecycle.State, block: suspend () -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(state) {
            block()
        }
    }
}

fun LifecycleOwner.launchOnState(state: Lifecycle.State, block: suspend () -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(state) {
            block()
        }
    }
}

fun LifecycleOwner.launchOnState(
    context: CoroutineContext = EmptyCoroutineContext,
    state: Lifecycle.State,
    block: suspend () -> Unit
) {
    lifecycleScope.launch(context) {
        repeatOnLifecycle(state) {
            block()
        }
    }
}
