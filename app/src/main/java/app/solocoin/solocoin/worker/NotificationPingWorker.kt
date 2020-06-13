package app.solocoin.solocoin.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.ui.home.HomeActivity
import app.solocoin.solocoin.util.GlobalUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.core.KoinComponent
import java.util.*

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@RequiresApi(Build.VERSION_CODES.N)
class NotificationPingWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams), KoinComponent {

    override suspend fun doWork(): Result {
        return generateNotification()
    }

    private fun generateNotification(): Result {
//        Log.wtf(NOTIFY_CALL, "Creating Notification")

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationID,
            Intent(applicationContext, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                putExtra("from_checkin", true)
            },
            0
        )

        GlobalUtils.notifyUser(
            notificationID,
            channelId,
            applicationContext,
            pendingIntent,
            NotificationManager.IMPORTANCE_HIGH,
            applicationContext.getString(R.string.notif_checkin),
            applicationContext.getString(R.string.notif_checkin_desc)
        )

        sharedPrefs?.let {
            it.recentNotifTime = Calendar.getInstance().timeInMillis
        }

        return Result.success()
    }

    companion object {
        private val TAG: String? = NotificationPingWorker::class.java.simpleName
        private val NOTIFY_CALL: String =
            NotificationPingWorker::class.java.simpleName + " NOTIFY_CALL"
        private const val notificationID = 402;
        private const val channelId = "2"
    }
}