package com.mozhimen.emulatork.input.key

import com.mozhimen.emulatork.input.key.InputKey

/**
 * @ClassName InputClass
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
interface InputKeyMap {
    fun getInputKeys(): Set<InputKey>
    fun getAxesMap(): Map<Int, Int>
}