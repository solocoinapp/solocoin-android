package app.solocoin.solocoin.ui.home

import android.app.PendingIntent
import android.content.Intent
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import app.solocoin.solocoin.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.InternalCoroutinesApi
class MyMessagingService : FirebaseMessagingService() {
//    val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @InternalCoroutinesApi
    override fun onMessageReceived(remotemessage: RemoteMessage) {
        super.onMessageReceived(remotemessage)
        shownotification(remotemessage?.notification?.title.toString(),
            remotemessage?.notification?.body.toString()
        )
    }
    @InternalCoroutinesApi
    fun shownotification(title:String, message:String) {
      
        val i = Intent(this, HomeActivity::class.java)
        val pi = PendingIntent.getActivity(this, 1, i, PendingIntent.FLAG_UPDATE_CURRENT)
        val simplenotification = NotificationCompat.Builder(this, "first")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.app_icon)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()
        val manager = NotificationManagerCompat.from(this)
        manager.notify(System.currentTimeMillis().toInt(), simplenotification)

    }
}