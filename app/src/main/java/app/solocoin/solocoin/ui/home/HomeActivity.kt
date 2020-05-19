package app.solocoin.solocoin.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.repo.NoConnectivityException
import app.solocoin.solocoin.ui.auth.LoginSignupViewModel
import app.solocoin.solocoin.util.enums.Status
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel

import android.app.AlarmManager

import androidx.core.content.ContextCompat.getSystemService

import android.os.SystemClock

import android.app.PendingIntent

import android.content.Intent

import android.R.attr.fragment
import android.app.Notification
import androidx.core.content.ContextCompat
import app.solocoin.solocoin.NotificationAlarmReceiver


@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class HomeActivity : AppCompatActivity() {

    private var alarmManager: AlarmManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setSupportActionBar(toolbar)
        bottom_nav_view.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottom_nav_view.selectedItemId = R.id.nav_home

        alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager;
        // Manage notification checking

    }

    // https://gist.github.com/BrandonSmith/6679223

    private fun scheduleNotification(delay: Int, info: String) {
        val notification: Notification? = getNotification(info);
        val notificationIntent = Intent(this,  NotificationAlarmReceiver::class.java)
        notificationIntent.putExtra("notification-id", 1)
        notificationIntent.putExtra("notification", notification)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val futureInMillis: Long = SystemClock.elapsedRealtime() + delay
        val alarmManager: AlarmManager =
            ContextCompat.getSystemService(this, AlarmManager::class.java) as AlarmManager
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent)
    }

    private fun getNotification(content: String): Notification? {
        val builder: Notification.Builder = Notification.Builder(this)
        builder.setContentTitle("Solocoin Says...")
        builder.setContentText(content)
        builder.setSmallIcon(app.solocoin.solocoin.R.drawable.app_icon)
        return builder.build()
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                toolbar.title = getString(R.string.home)
                openFragment(HomeFragment.instance())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_wallet -> {
                toolbar.title = getString(R.string.wallet)
                openFragment(WalletFragment.instance())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_leaderboard -> {
                toolbar.title = getString(R.string.leaderboard)
                openFragment(LeaderboardFragment.instance())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
                toolbar.title = getString(R.string.profile)
                openFragment(ProfileFragment.instance())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment, fragment.tag)
        transaction.commit()
    }

    override fun onBackPressed() {
        if (bottom_nav_view.selectedItemId != R.id.nav_home) {
            bottom_nav_view.selectedItemId = R.id.nav_home
        } else {
            super.onBackPressed()
        }
    }
}
