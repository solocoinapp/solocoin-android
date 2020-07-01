package app.solocoin.solocoin.ui.home

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import app.solocoin.solocoin.NotificationAlarmReceiver
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.services.FusedLocationService
import app.solocoin.solocoin.util.GlobalUtils
import app.solocoin.solocoin.worker.NotificationPingWorker
import app.solocoin.solocoin.worker.SessionPingWorker
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit


@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@RequiresApi(Build.VERSION_CODES.M)
class HomeActivity : AppCompatActivity() {

    private var alarmManager: AlarmManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setSupportActionBar(toolbar)
        bottom_nav_view.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottom_nav_view.selectedItemId = R.id.nav_home

        sharedPrefs?.let {
            if (it.recentCheckTime < it.recentNotifTime) {
                it.periodValid =
                    it.recentNotifTime + 30 * 60 * 1000 >= Calendar.getInstance().timeInMillis
//            Log.v(TAG, "Period valid: " + it.periodValid);
                it.recentCheckTime = Calendar.getInstance().timeInMillis
            }
        }

        // First adding notification channels to notification manager
        createNotificationChannels()

        // Starting fused location service
        // TODO : Setup permission request for Fused Location service properly
        checkPermissionForLocation()

        // Starting Session Ping API worker
        startSessionPingManager()

        // Starting Check-In Notification worker
        startNotificationPingManager()

        alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        // Manage notification checking

    }

    // https://gist.github.com/BrandonSmith/6679223
    private fun scheduleNotification(delay: Int, info: String) {
        val notification: Notification? = getNotification(info)
        val notificationIntent = Intent(this, NotificationAlarmReceiver::class.java)
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
        builder.setSmallIcon(R.drawable.app_icon)
        return builder.build()
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
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
                R.id.nav_milestones -> {
                    toolbar.title = getString(R.string.milestones)
                    openFragment(MilestonesFragment.instance())
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

    // Creates notification channels for the app
    private fun createNotificationChannels() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannels = mutableListOf<NotificationChannel>().apply {
                var importance = NotificationManager.IMPORTANCE_HIGH
                val channelPermission =
                    NotificationChannel("1", "Solocoin Permissions", importance).apply {
                        description = "Ask user for location related permissions."
                    }
                val channelCheckIn =
                    NotificationChannel("2", "Solocoin Check-In", importance).apply {
                        description = "To check user presence near phone"
                    }
                add(channelCheckIn)
                add(channelPermission)

                importance = NotificationManager.IMPORTANCE_DEFAULT
                val channelRegUpdates =
                    NotificationChannel("3", "Solocoin Regular Updates", importance).apply {
                        description = "To give user updates through fcm etc."
                    }
                add(channelRegUpdates)
            }

            // Register the channels with the system
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
                createNotificationChannels(notificationChannels)
            }
        }
    }

    private fun checkPermissionForLocation() {

        var permissionsArray = arrayOf<String>()

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            permissionsArray =
                permissionsArray.plus(android.Manifest.permission.ACCESS_FINE_LOCATION)

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            permissionsArray =
                permissionsArray.plus(android.Manifest.permission.ACCESS_COARSE_LOCATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            )
                permissionsArray =
                    permissionsArray.plus(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        when (permissionsArray.count()) {
            0 -> startFusedLocationService()
            else -> ActivityCompat.requestPermissions(
                this,
                permissionsArray,
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            when {
//                grantResults.isEmpty() -> Log.d(TAG, "User Interaction Cancelled")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> startFusedLocationService()
                else -> {
//                    Log.d(TAG, "Permissions Denied by User")
                        Toast.makeText(this, "Please provide location permission.", Toast.LENGTH_SHORT);
//                        TODO("Show message when user denies location permissions or user interaction is cancelled")
                    }
                }
            }
        }

    private fun startFusedLocationService() {
        if (!GlobalUtils.isServiceRunning(applicationContext, FusedLocationService.javaClass)) {
//            Log.wtf(TAG, "Starting the fused location service.")
            val intent = Intent(applicationContext, FusedLocationService::class.java)
//            applicationScope.launch {
                startService(intent)
//            }
        } else {
//            Log.wtf(TAG, "Fused location service already running")
        }
    }

    /*
     * Returns the state of the work performed by work manager using unique work identifier .
     * States of Work Manager : STOPPED, RUNNING, ENQUEUED.
     */
    private fun getStateOfWork(workUniqueName: String): WorkInfo.State {
        return try {
            if (WorkManager.getInstance(application)
                    .getWorkInfosForUniqueWork(workUniqueName)
                    .get().size > 0
            ) {
                WorkManager.getInstance(application)
                    .getWorkInfosForUniqueWork(workUniqueName).get()[0].state
            } else {
                WorkInfo.State.CANCELLED
            }
        } catch (e: ExecutionException) {
            e.printStackTrace()
            WorkInfo.State.CANCELLED
        } catch (e: InterruptedException) {
            e.printStackTrace()
            WorkInfo.State.CANCELLED
        }
    }

    /*
     * Generates new periodic work request with unique work identifier = 'SESSION_PING_REQUEST'
     * @see companion object of the class for constant identifiers.
     */
    private fun createSessionPingWorkRequest() {
        val periodicWorkRequest =
            PeriodicWorkRequest.Builder(SessionPingWorker::class.java, 15, TimeUnit.MINUTES)
                .build()
        WorkManager.getInstance(application).enqueueUniquePeriodicWork(
            SESSION_PING_REQUEST,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    /*
     * Once user reaches 'HomeActivity' worker manager service is executed through this function
     * in current view model. The state of the work request is checked using its unique identifier
     * 'SESSION_PING_MANAGER'. In case, work request is already enqueued then new work request is
     * not generated else new work request is created.
     */
    private fun startSessionPingManager() {
        if (getStateOfWork(SESSION_PING_REQUEST) != WorkInfo.State.ENQUEUED && getStateOfWork(
                SESSION_PING_REQUEST
            ) != WorkInfo.State.RUNNING
        ) {
            applicationScope.launch {
                createSessionPingWorkRequest()
            }
//            Log.wtf(SESSION_PING_MANAGER, ": Server Started !!")
        } else {
//            Log.wtf(SESSION_PING_MANAGER, ": Server Already Working !!")
        }
    }

    // this code is pretty much the exact same as the one for the session
    private fun createNotificationWorkRequest() {
        val periodicWorkRequest =
            PeriodicWorkRequest.Builder(NotificationPingWorker::class.java, 1, TimeUnit.HOURS)
                .build()
        WorkManager.getInstance(application).enqueueUniquePeriodicWork(
            NOTIFICATION_PING_REQUEST,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    // check if already running, if not, launch work request
    private fun startNotificationPingManager() {
        if (getStateOfWork(NOTIFICATION_PING_REQUEST) != WorkInfo.State.ENQUEUED && getStateOfWork(
                NOTIFICATION_PING_REQUEST
            ) != WorkInfo.State.RUNNING
        ) {
            applicationScope.launch {
                createNotificationWorkRequest()
            }
        }
    }

    override fun onBackPressed() {
        if (bottom_nav_view.selectedItemId != R.id.nav_home) {
            bottom_nav_view.selectedItemId = R.id.nav_home
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private val TAG = HomeActivity::class.java.simpleName
        private const val PERMISSION_REQUEST_CODE = 34
        private const val SESSION_PING_REQUEST = "app.solocoin.solocoin.api.session.ping"
        private const val SESSION_PING_MANAGER: String = "SESSION_PING_MANAGER"
        private const val NOTIFICATION_PING_REQUEST = "app.solocoin.solocoin.api.notification"
        private val applicationScope = CoroutineScope(Dispatchers.Default)
    }
}
