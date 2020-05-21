package app.solocoin.solocoin

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager


// https://gist.github.com/BrandonSmith/6679223

class NotificationAlarmReceiver : BroadcastReceiver() {

    public var NOTIFICATION_ID = "notification-id"
    public var NOTIFICATION = "notification"

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification = intent.getParcelableExtra(NOTIFICATION)
        val id: Int = intent.getIntExtra(NOTIFICATION_ID, 0)
        notificationManager.notify(id, notification)
    }
}
