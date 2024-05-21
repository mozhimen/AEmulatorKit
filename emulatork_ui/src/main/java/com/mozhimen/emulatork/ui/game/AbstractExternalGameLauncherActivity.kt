package com.mozhimen.emulatork.ui.game

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import com.mozhimen.basick.utilk.android.app.showAlertDialog
import com.mozhimen.basick.utilk.android.content.get_of_config_longAnimTime
import com.mozhimen.basick.utilk.android.content.get_of_config_mediumAnimTime
import com.mozhimen.basick.utilk.androidx.lifecycle.runOnLifecycleState
import com.mozhimen.basick.utilk.kotlinx.coroutines.launchSafe
import com.mozhimen.emulatork.basic.core.CoresSelection
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.ui.main.GameLaunchTaskHandler
import com.mozhimen.emulatork.basic.android.ImmersiveFragmentActivity
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.mozhimen.emulatork.ui.library.PendingOperationsMonitor

/**
 * @ClassName ExternalGameLauncherActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
/**
 * This activity is used as an entry point when launching games from external shortcuts. This activity
 * still runs in the main process so it can peek into background job status and wait for them to
 * complete.
 */
@OptIn(FlowPreview::class)
abstract class AbstractExternalGameLauncherActivity : ImmersiveFragmentActivity() {

    //    @Inject
//    lateinit var retrogradeDatabase: RetrogradeDatabase
    abstract fun retrogradeDatabase(): RetrogradeDatabase

    //    @Inject
//    lateinit var gameLaunchTaskHandler: GameLaunchTaskHandler
    abstract fun gameLaunchTaskHandler(): GameLaunchTaskHandler

    //    @Inject
//    lateinit var coresSelection: CoresSelection
    abstract fun coresSelection(): CoresSelection

    //    @Inject
//    lateinit var gameLauncher: GameLauncher
    abstract fun gameLauncher(): GameLauncher

    abstract fun gameActivityClazz(): Class<*>

    private val loadingState = MutableStateFlow(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_loading)
        if (savedInstanceState == null) {

            val gameId = intent.data?.pathSegments?.let { it[it.size - 1].toInt() }!!

            lifecycleScope.launch {
                loadingState.value = true
                try {
                    loadGame(gameId)
                } catch (e: Throwable) {
                    displayErrorMessage()
                }
                loadingState.value = false
            }

            runOnLifecycleState(Lifecycle.State.RESUMED) {
                initializeLoadingFlow(loadingState)
            }
        }
    }

    private suspend fun initializeLoadingFlow(loadingSubject: MutableStateFlow<Boolean>) {
        loadingSubject
            .debounce(get_of_config_longAnimTime().toLong())
            .collect {
                findViewById<View>(R.id.progressBar).isVisible = it
            }
    }

    private suspend fun loadGame(gameId: Int) {
        waitPendingOperations()

        val game = retrogradeDatabase().gameDao().selectById(gameId)
            ?: throw IllegalArgumentException("Game not found: $gameId")

        delay(get_of_config_mediumAnimTime().toLong())

        gameLauncher().launchGameAsync(
            this,
            gameActivityClazz(),
            game,
            true,
            false//            TVHelper.isTV(applicationContext)
        )
    }

    private suspend fun waitPendingOperations() {
        getLoadingLiveData()
            .asFlow()
            .filter { !it }
            .first()
    }

    private fun displayErrorMessage() {
        showAlertDialog(R.string.game_loader_error_load_game, R.string.ok) { finish() }
    }

    private fun getLoadingLiveData(): LiveData<Boolean> {
        return PendingOperationsMonitor(applicationContext).anyOperationInProgress()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            BaseGameActivity.REQUEST_PLAY_GAME -> {
                val isLeanback = data?.extras?.getBoolean(BaseGameActivity.PLAY_GAME_RESULT_LEANBACK) == true

                GlobalScope.launchSafe {
                    if (isLeanback) {
//                        ChannelUpdateWork.enqueue(applicationContext)
                    }
                    gameLaunchTaskHandler().handleGameFinish(false, this@AbstractExternalGameLauncherActivity, resultCode, data)
                    finish()
                }
            }
        }
    }
}
