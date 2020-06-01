package app.solocoin.solocoin.ui.home

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.work.*
import app.solocoin.solocoin.NotificationAlarmReceiver
import app.solocoin.solocoin.R
import app.solocoin.solocoin.services.FusedLocationService
import app.solocoin.solocoin.util.GlobalUtils
import app.solocoin.solocoin.worker.SessionPingWorker
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit


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

        // TODO : Setup permission request for Fused Location service properly
        checkPermissionForLocation()
        startSessionPingManager()
        alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        // Manage notification checking

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("1", "Solocoin", importance).apply {
                description = "Solocoin"
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
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
                grantResults.isEmpty() -> Log.d(TAG, "User Interaction Cancelled")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> startFusedLocationService()
                else -> {
                    Log.d(TAG, "Permissions Denied by User")
                    TODO("Show message when user denies location permissions or user interaction is cancelled")
                }
            }
        }
    }

    private fun startFusedLocationService() {
        if (!GlobalUtils.isServiceRunning(applicationContext, FusedLocationService::class.java)) {
            Log.wtf(TAG, "Starting the fused location service.")
            val intent = Intent(applicationContext, FusedLocationService::class.java)
            applicationScope.launch {
                startService(intent)
            }
        } else {
            Log.wtf(TAG, "Fused location service already running")
        }
    }

    /*
     * Generates new periodic work request with unique work identifier = 'SESSION_PING_REQUEST'
     * @see companion object of the class for constant identifiers.
     */
    private fun createWorkRequest() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED).build()
        val periodicWorkRequest =
            PeriodicWorkRequest.Builder(SessionPingWorker::class.java, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(application).enqueueUniquePeriodicWork(
            SESSION_PING_REQUEST,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    /*
     * Returns the state of the work performed by work manager using unique work identifier .
     * States of Work Manager : STOPPED, RUNNING, ENQUEUED.
     */
    private fun getStateOfWork(): WorkInfo.State {
        return try {
            if (WorkManager.getInstance(application)
                    .getWorkInfosForUniqueWork(SESSION_PING_REQUEST)
                    .get().size > 0
            ) {
                WorkManager.getInstance(application)
                    .getWorkInfosForUniqueWork(SESSION_PING_REQUEST).get()[0].state
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
     * Once user reaches 'HomeActivity' worker manager service is executed through this function
     * in current view model. The state of the work request is checked using its unique identifier
     * 'SESSION_PING_MANAGER'. In case, work request is already enqueued then new work request is
     * not generated else new work request is created.
     */
    private fun startSessionPingManager() {
        if (getStateOfWork() != WorkInfo.State.ENQUEUED && getStateOfWork() != WorkInfo.State.RUNNING) {
            applicationScope.launch {
                createWorkRequest()
            }
            Log.wtf(SESSION_PING_MANAGER, ": Server Started !!")
        } else {
            Log.wtf(SESSION_PING_MANAGER, ": Server Already Working !!")
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
        private const val SESSION_PING_REQUEST = "app.solocoin.solocoin.api.v1"
        private const val SESSION_PING_MANAGER: String = "SESSION_PING_MANAGER"
        private val applicationScope = CoroutineScope(Dispatchers.Default)
    }
}
