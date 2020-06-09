package app.solocoin.solocoin.services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import app.solocoin.solocoin.R
import app.solocoin.solocoin.ui.home.HomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class FirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        message.data.isNotEmpty().let {
            if (!message.data.isNullOrEmpty()) {
                sendNotification(message.data["message"].toString())
            }
        }
        message.notification?.let {
            sendNotification(it.body)
        }
    }

    private fun sendNotification(message: String?) {

        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val builder = NotificationCompat.Builder(this, "1")
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle("Important Update")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(3, builder.build())
        }
    }


}