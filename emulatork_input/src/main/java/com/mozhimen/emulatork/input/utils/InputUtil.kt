package com.mozhimen.emulatork.input.utils

import com.mozhimen.emulatork.input.key.InputKey
import com.mozhimen.emulatork.input.key.InputKeyRetro

/**
 * @ClassName InputUtils
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
internal fun inputKeySetOf(vararg keyCodes: Int): Set<InputKey> =
    inputKeyListOf(*keyCodes).toSet()

internal fun inputKeyListOf(vararg keyCodes: Int): List<InputKey> =
    keyCodes.map(::InputKey)

internal fun inputKeyRetroListOf(vararg keyCodes: Int): List<InputKeyRetro> =
    keyCodes.map(::InputKeyRetro)

internal fun intKeyCodePair2inputKeyPair(vararg bindings: Pair<Int, Int>) =
    bindings.associate {
        InputKey(it.first) to InputKeyRetro(it.second)
    }


