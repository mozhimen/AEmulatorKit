package com.mozhimen.emulatork.ext.game

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.mozhimen.emulatork.basic.game.db.RetrogradeDatabase
import com.mozhimen.emulatork.basic.game.db.entities.Game
import com.mozhimen.emulatork.basic.game.review.GameReviewManager
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.ext.game.crash.GameCrashActivity
import kotlinx.coroutines.delay
import com.mozhimen.emulatork.ext.works.WorkScheduler
import com.mozhimen.emulatork.ui.works.AbsWorkSaveSync
import com.mozhimen.emulatork.ui.works.AbsWorkStorageCacheCleaner

/**
 * @ClassName GameLaunchTaskHandler
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class GameLaunchTaskHandler(
    private val gameReviewManager: GameReviewManager,
    private val retrogradeDb: RetrogradeDatabase
) {

    fun handleGameStart(context: Context) {
        cancelBackgroundWork(context)
    }

    suspend fun handleGameFinish(
        enableRatingFlow: Boolean,
        activity: Activity,
        resultCode: Int,
        data: Intent?,
        workSaveSyncClazz: Class<out AbsWorkSaveSync>,
        workStorageCacheCleanerClazz: Class<out AbsWorkStorageCacheCleaner>
    ) {
        rescheduleBackgroundWork(activity.applicationContext, workSaveSyncClazz, workStorageCacheCleanerClazz)
        when (resultCode) {
            Activity.RESULT_OK -> handleSuccessfulGameFinish(activity, enableRatingFlow, data)
            BaseGameActivity.RESULT_ERROR -> handleUnsuccessfulGameFinish(
                activity,
                data?.getStringExtra(BaseGameActivity.PLAY_GAME_RESULT_ERROR)!!,
                null
            )

            BaseGameActivity.RESULT_UNEXPECTED_ERROR -> handleUnsuccessfulGameFinish(
                activity,
                activity.getString(R.string.lemuroid_crash_disclamer),
                data?.getStringExtra(BaseGameActivity.PLAY_GAME_RESULT_ERROR)
            )
        }
    }

    private fun cancelBackgroundWork(context: Context) {
        WorkScheduler.cancelAutoWork(context)
        WorkScheduler.cancelManualWork(context)
        WorkScheduler.cancelCleanCacheLRU(context)
    }

    private fun rescheduleBackgroundWork(context: Context, workSaveSyncClazz: Class<out AbsWorkSaveSync>, workStorageCacheCleanerClazz: Class<out AbsWorkStorageCacheCleaner>) {
        // Let's slightly delay the sync. Maybe the user wants to play another game.
        WorkScheduler.enqueueAutoWork(workSaveSyncClazz, context, 5)
        WorkScheduler.enqueueCleanCacheLRU(workStorageCacheCleanerClazz, context)
    }

    private fun handleUnsuccessfulGameFinish(
        activity: Activity,
        message: String,
        messageDetail: String?
    ) {
        GameCrashActivity.launch(activity, message, messageDetail)
    }

    private suspend fun handleSuccessfulGameFinish(
        activity: Activity,
        enableRatingFlow: Boolean,
        data: Intent?
    ) {
        val duration = data?.extras?.getLong(BaseGameActivity.PLAY_GAME_RESULT_SESSION_DURATION)
            ?: 0L
        val game = data?.extras?.getSerializable(BaseGameActivity.PLAY_GAME_RESULT_GAME) as Game

        updateGamePlayedTimestamp(game)
        if (enableRatingFlow) {
            displayReviewRequest(activity, duration)
        }
    }

    private suspend fun displayReviewRequest(activity: Activity, durationMillis: Long) {
        delay(500)
        gameReviewManager.launchReviewFlow(activity, durationMillis)
    }

    private suspend fun updateGamePlayedTimestamp(game: Game) {
        retrogradeDb.gameDao().update(game.copy(lastPlayedAt = System.currentTimeMillis()))
    }
}
