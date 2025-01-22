package com.mozhimen.emulatork.ui.hilt.game.menu

import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import com.mozhimen.emulatork.ui.hilt.R
import com.mozhimen.emulatork.ui.game.menu.AbsGameMenuFragment
import com.mozhimen.emulatork.ext.input.MenuMgr
import dagger.hilt.android.AndroidEntryPoint

/**
 * @ClassName GameMenuFragment
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/5/20 22:12
 * @Version 1.0
 */
@AndroidEntryPoint
 class GameMenuFragment : AbsGameMenuFragment() {

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (MenuMgr.onPreferenceTreeClicked(activity, preference))
            return true

        when (preference?.key) {
            "pref_game_section_save" -> {
                findNavController().navigate(R.id.game_menu_save)
            }
            "pref_game_section_load" -> {
                findNavController().navigate(R.id.game_menu_load)
            }
            "pref_game_section_core_options" -> {
                findNavController().navigate(R.id.game_menu_core_options)
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

//    @dagger.Module
//    class Module
}