package com.mozhimen.emulatork.input.type

import com.mozhimen.emulatork.input.key.InputKey

/**
 * @ClassName InputClassUnknown
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
object InputTypeUnknown : InputType {
    override fun getInputKeys(): Set<InputKey> {
        return emptySet()
    }

    override fun getAxesMap(): Map<Int, Int> {
        return emptyMap()
    }
}
