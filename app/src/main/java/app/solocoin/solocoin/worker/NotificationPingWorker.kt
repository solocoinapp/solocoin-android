package app.solocoin.solocoin.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.repo.SolocoinRepository
import app.solocoin.solocoin.ui.home.HomeActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

// author: Vijay Daita

// Gives notification ping

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class NotificationPingWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams), KoinComponent {

    private val repository: SolocoinRepository by inject()
    private val notificationID = 402;

    override fun doWork(): Result {
        return doApiCall()
    }

    private fun doApiCall(): Result {
        Log.d(API_CALL, "Creating Notification")
        val builder = NotificationCompat.Builder(applicationContext)
        builder.setSmallIcon(R.drawable.app_icon)
        builder.setContentTitle(applicationContext.getString(R.string.notif_checkin))
        builder.setContentText(applicationContext.getString(R.string.notif_checkin_desc))

        val regIntent = Intent(applicationContext, HomeActivity.javaClass)
        regIntent.putExtra("from_checkin", true)
        val pendingIntent =
            PendingIntent.getActivity(applicationContext, notificationID, regIntent, 0)

        builder.setContentIntent(pendingIntent)

        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("2", "Solocoin Check-in", importance).apply {
                description = "Solocoin Check-in"
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationID, builder.build())

        sharedPrefs?.let {
            val time = Calendar.getInstance().get(Calendar.MILLISECOND)
            it.recentNotifTime = time.toLong()
        }

        return Result.success()
    }

    override fun onStopped() {
        Log.wtf(TAG, "Stopping Notification Worker")
        super.onStopped()
    }

    companion object {
        private val TAG: String? = SessionPingWorker::class.java.simpleName
        private val API_CALL: String = SessionPingWorker::class.java.simpleName + " API_CALL"
//        /*
//         * Avoid notification for fused location service start on first time user open home activity
//         */
//        @JvmStatic private var firstTime: Boolean = true
    }
}