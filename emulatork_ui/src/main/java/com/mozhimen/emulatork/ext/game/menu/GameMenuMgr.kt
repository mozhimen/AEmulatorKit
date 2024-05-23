package com.mozhimen.emulatork.ext.game.menu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import androidx.preference.SwitchPreference
import com.mozhimen.emulatork.basic.core.CoreID
import com.mozhimen.emulatork.basic.game.system.GameSystemCoreConfig
import com.mozhimen.emulatork.basic.game.db.entities.Game
import com.mozhimen.emulatork.basic.save.SaveInfo
import com.mozhimen.emulatork.basic.save.SaveStatePreviewManager
import com.mozhimen.emulatork.ui.R
import com.mozhimen.basick.utilk.android.util.dp2pxI
import com.mozhimen.emulatork.basic.game.menu.GameMenuContract
import java.text.SimpleDateFormat

/**
 * @ClassName GameMenuHelper
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
object GameMenuMgr {

    fun setupAudioOption(screen: PreferenceScreen, audioEnabled: Boolean) {
        val preference = screen.findPreference<SwitchPreference>(MUTE)
        preference?.isChecked = !audioEnabled
    }

    fun setupFastForwardOption(
        screen: PreferenceScreen,
        fastForwardEnabled: Boolean,
        fastForwardSupported: Boolean
    ) {
        val preference = screen.findPreference<SwitchPreference>(FAST_FORWARD)
        preference?.isChecked = fastForwardEnabled
        preference?.isVisible = fastForwardSupported
    }

    fun setupSaveOption(
        screen: PreferenceScreen,
        systemCoreConfig: GameSystemCoreConfig
    ) {
        val savesOption = screen.findPreference<Preference>(SECTION_SAVE_GAME)
        savesOption?.isVisible = systemCoreConfig.statesSupported

        val loadOption = screen.findPreference<Preference>(SECTION_LOAD_GAME)
        loadOption?.isVisible = systemCoreConfig.statesSupported
    }

    fun setupSettingsOption(
        screen: PreferenceScreen,
        systemCoreConfig: GameSystemCoreConfig
    ) {
        screen.findPreference<Preference>(SECTION_CORE_OPTIONS)?.isVisible = sequenceOf(
            systemCoreConfig.systemExposedSettings.isNotEmpty(),
            systemCoreConfig.exposedAdvancedSettings.isNotEmpty(),
            systemCoreConfig.controllerConfigs.values.any { it.size > 1 }
        ).any { it }
    }

    fun setupChangeDiskOption(
        activity: Activity?,
        screen: PreferenceScreen,
        currentDisk: Int,
        numDisks: Int
    ) {
        val changeDiskPreference = screen.findPreference<ListPreference>(SECTION_CHANGE_DISK)
        changeDiskPreference?.isVisible = numDisks > 1

        changeDiskPreference?.entries = (0 until numDisks)
            .map {
                screen.context.resources.getString(
                    R.string.game_menu_change_disk_disk,
                    (it + 1).toString()
                )
            }
            .toTypedArray()

        changeDiskPreference?.entryValues = (0 until numDisks)
            .map { it.toString() }
            .toTypedArray()

        changeDiskPreference?.setValueIndex(currentDisk)
        changeDiskPreference?.setOnPreferenceChangeListener { _, newValue ->
            val resultIntent = Intent().apply {
                putExtra(GameMenuContract.RESULT_CHANGE_DISK, (newValue as String).toInt())
            }
            setResultAndFinish(activity, resultIntent)
            true
        }
    }

    fun addSavePreference(
        screen: PreferenceScreen,
        index: Int,
        saveStateInfo: SaveInfo,
        bitmap: Bitmap?
    ) {
        screen.addPreference(
            Preference(screen.context).apply {
                this.key = "pref_game_save_$index"
                this.summary = getDateString(saveStateInfo)
                this.title = context.getString(R.string.game_menu_state, (index + 1).toString())
                this.icon = BitmapDrawable(screen.context.resources, bitmap)
            }
        )
    }

    fun addLoadPreference(
        screen: PreferenceScreen,
        index: Int,
        saveStateInfo: SaveInfo,
        bitmap: Bitmap?
    ) {
        screen.addPreference(
            Preference(screen.context, null).apply {
                this.key = "pref_game_load_$index"
                this.summary = getDateString(saveStateInfo)
                this.isEnabled = saveStateInfo.exists
                this.title = context.getString(R.string.game_menu_state, (index + 1).toString())
                this.icon = BitmapDrawable(screen.context.resources, bitmap)
            }
        )
    }

    fun onPreferenceTreeClicked(activity: Activity?, preference: Preference?): Boolean {
        return when (preference?.key) {
            "pref_game_save_0" -> handleSaveAction(activity, 0)
            "pref_game_save_1" -> handleSaveAction(activity, 1)
            "pref_game_save_2" -> handleSaveAction(activity, 2)
            "pref_game_save_3" -> handleSaveAction(activity, 3)
            "pref_game_load_0" -> handleLoadAction(activity, 0)
            "pref_game_load_1" -> handleLoadAction(activity, 1)
            "pref_game_load_2" -> handleLoadAction(activity, 2)
            "pref_game_load_3" -> handleLoadAction(activity, 3)
            "pref_game_mute" -> {
                val currentValue = (preference as SwitchPreference).isChecked
                val resultIntent = Intent().apply {
                    putExtra(GameMenuContract.RESULT_ENABLE_AUDIO, !currentValue)
                }
                setResultAndFinish(activity, resultIntent)
                true
            }
            "pref_game_fast_forward" -> {
                val currentValue = (preference as SwitchPreference).isChecked
                val resultIntent = Intent().apply {
                    putExtra(GameMenuContract.RESULT_ENABLE_FAST_FORWARD, currentValue)
                }
                setResultAndFinish(activity, resultIntent)
                true
            }
            "pref_game_reset" -> {
                val resultIntent = Intent().apply {
                    putExtra(GameMenuContract.RESULT_RESET, true)
                }
                setResultAndFinish(activity, resultIntent)
                true
            }
            "pref_game_quit" -> {
                val resultIntent = Intent().apply {
                    putExtra(GameMenuContract.RESULT_QUIT, true)
                }
                setResultAndFinish(activity, resultIntent)
                true
            }
            "pref_game_edit_touch_controls" -> {
                val resultIntent = Intent().apply {
                    putExtra(GameMenuContract.RESULT_EDIT_TOUCH_CONTROLS, true)
                }
                setResultAndFinish(activity, resultIntent)
                true
            }
            else -> false
        }
    }

    private fun handleSaveAction(activity: Activity?, index: Int): Boolean {
        val resultIntent = Intent().apply {
            putExtra(GameMenuContract.RESULT_SAVE, index)
        }
        setResultAndFinish(activity, resultIntent)
        return true
    }

    private fun handleLoadAction(activity: Activity?, index: Int): Boolean {
        val resultIntent = Intent().apply {
            putExtra(GameMenuContract.RESULT_LOAD, index)
        }
        setResultAndFinish(activity, resultIntent)
        return true
    }

    private fun setResultAndFinish(activity: Activity?, resultIntent: Intent) {
        activity?.setResult(Activity.RESULT_OK, resultIntent)
        activity?.finish()
    }

    private fun getDateString(saveInfo: SaveInfo): String {
        val formatter = SimpleDateFormat.getDateTimeInstance()
        return if (saveInfo.exists) {
            formatter.format(saveInfo.date)
        } else {
            ""
        }
    }

    suspend fun getSaveStateBitmap(
        context: Context,
        saveStatePreviewManager: SaveStatePreviewManager,
        saveStateInfo: SaveInfo,
        game: Game,
        coreID: CoreID,
        index: Int
    ): Bitmap? {
        if (!saveStateInfo.exists) return null
        val imageSize = 96f.dp2pxI()
        return saveStatePreviewManager.getPreviewForSlot(game, coreID, index, imageSize)
    }

    const val FAST_FORWARD = "pref_game_fast_forward"
    const val MUTE = "pref_game_mute"
    const val SECTION_CORE_OPTIONS = "pref_game_section_core_options"
    const val SECTION_CHANGE_DISK = "pref_game_section_change_disk"
    const val SECTION_SAVE_GAME = "pref_game_section_save"
    const val SECTION_LOAD_GAME = "pref_game_section_load"
}
