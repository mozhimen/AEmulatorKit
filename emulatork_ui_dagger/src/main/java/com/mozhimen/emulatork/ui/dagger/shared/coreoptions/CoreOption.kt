package com.mozhimen.emulatork.ui.dagger.shared.coreoptions

import com.mozhimen.emulatork.basic.core.CoreVariable
import com.swordfish.libretrodroid.Variable
import java.io.Serializable

/**
 * @ClassName CoreOption
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
data class CoreOption(
    val variable: CoreVariable,
    val name: String,
    val optionValues: List<String>
) : Serializable {

    companion object {
        fun fromLibretroDroidVariable(variable: Variable): CoreOption {
            val name = variable.description?.split(";")?.get(0)!!
            val values = variable.description?.split(";")?.get(1)?.trim()?.split('|') ?: listOf()
            val coreVariable = CoreVariable(variable.key!!, variable.value!!)
            return CoreOption(coreVariable, name, values)
        }
    }
}
