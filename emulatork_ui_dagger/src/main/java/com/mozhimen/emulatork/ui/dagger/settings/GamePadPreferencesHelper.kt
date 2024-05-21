package com.mozhimen.emulatork.ui.dagger.settings
import com.mozhimen.emulatork.ui.settings.GamePadPreferencesHelper
/**
 * @ClassName GamePadPreferencesHelper
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/5/20 22:36
 * @Version 1.0
 */
class GamePadPreferencesHelper(
    inputDeviceManager: com.mozhimen.emulatork.ui.input.InputDeviceManager,
    isLeanback: Boolean
) :GamePadPreferencesHelper(inputDeviceManager, isLeanback){
    @dagger.Module
    class Module
}