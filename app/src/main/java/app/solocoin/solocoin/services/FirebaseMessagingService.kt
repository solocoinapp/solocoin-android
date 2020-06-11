package app.solocoin.solocoin.services

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.ui.home.HomeActivity
import app.solocoin.solocoin.util.GlobalUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@RequiresApi(Build.VERSION_CODES.N)
class FirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        message.data.isNotEmpty().let {
            if (!message.data.isNullOrEmpty()) {
                sendNotification(message.data["message"].toString())
            }
        }
        message.notification?.body?.let {
            sendNotification(it)
        }
    }

    private fun sendNotification(message: String) {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_ONE_SHOT
        )

        GlobalUtils.notifyUser(
            notificationId,
            channelId,
            this,
            pendingIntent,
            NotificationManager.IMPORTANCE_DEFAULT,
            "Hello! " + sharedPrefs?.name,
            message
        )
    }

    companion object {
        private const val notificationId = 342
        private const val channelId = "3"
    }
}