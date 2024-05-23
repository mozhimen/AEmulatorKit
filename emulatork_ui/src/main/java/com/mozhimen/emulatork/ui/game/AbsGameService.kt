package com.mozhimen.emulatork.ui.game

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.mozhimen.emulatork.basic.game.db.entities.Game
import com.mozhimen.emulatork.ext.library.NotificationsManager

/**
 * @ClassName GameService
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
abstract class AbsGameService : Service() {

    abstract fun gameActivityClazz(): Class<*>

    private val binder = NotificationServiceBinder()

    inner class NotificationServiceBinder : Binder() {
        fun getService(): AbsGameService {
            return this@AbsGameService
        }
    }

    class GameServiceController(
        private val intent: Intent,
        private val connection: ServiceConnection
    ) {
        fun stopService(context: Context) {
            context.unbindService(connection)
            context.stopService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val game = kotlin.runCatching {
            intent.extras?.getSerializable(EXTRA_GAME) as Game?
        }.getOrNull()

        displayNotification(game)
        return START_NOT_STICKY
    }

    private fun displayNotification(game: Game?) {
        val notificationsManager = NotificationsManager(applicationContext, gameActivityClazz()).gameRunningNotification(game)
        startForeground(NotificationsManager.GAME_RUNNING_NOTIFICATION_ID, notificationsManager)
    }

    private fun hideNotification() {
        NotificationManagerCompat.from(this).cancel(NotificationsManager.GAME_RUNNING_NOTIFICATION_ID)
    }

    override fun onDestroy() {
        hideNotification()
    }

    companion object {
        private val EXTRA_GAME = "EXTRA_GAME"

        fun startService(context: Context, serviceClass: Class<out AbsGameService>, game: Game): GameServiceController {
            val intent = Intent(context, serviceClass).apply {
                putExtra(EXTRA_GAME, game)
            }

            val connection = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    // Do nothing
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    // Do nothing
                }
            }

            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            context.startService(intent)

            return GameServiceController(intent, connection)
        }

        fun stopService(
            context: Context,
            serviceController: GameServiceController?
        ): GameServiceController? {
            serviceController?.stopService(context)
            return null
        }
    }
}
