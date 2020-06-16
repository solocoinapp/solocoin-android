package app.solocoin.solocoin.util

import android.app.ActivityManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import androidx.work.WorkManager
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.services.FusedLocationService
import app.solocoin.solocoin.ui.SplashActivity
import app.solocoin.solocoin.worker.LegalChecker
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonElement
import com.instacart.library.truetime.TrueTime
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.lang.Math.toRadians
import java.util.*
import kotlin.math.*


/**
 * Created by Aditya Sonel on 22/04/20.
 */
class GlobalUtils {
    companion object {

        /**
         * Public function for detecting is network available or not
         */
        fun isNetworkAvailable(context: Context): Boolean {
            var isConnected = false
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            if (activeNetwork != null && activeNetwork.isConnected)
                isConnected = true
            return isConnected
        }

        /**
         * For starting desired activity as a new task,
         * intent_flags are added for clear back stack.
         * @param context: context of current fragment/activity
         * @param intent: activity class need to start
         *
         * Note: call finish() method after this function execution
         */
        fun startActivityAsNewStack(intent: Intent, context: Context) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }

        /**
         * For closing keyboard explicitly
         * @param context: context of current activity/fragment
         * @param view: keyboard is currently attached to
         */
        fun closeKeyboard(context: Context, view: View) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }

        private const val SESSION_PING_REQUEST = "app.solocoin.solocoin.api.session.ping"
        private const val NOTIFICATION_PING_REQUEST = "app.solocoin.solocoin.api.notification"
        @InternalCoroutinesApi
        @ExperimentalCoroutinesApi
        fun logout(
            context: Context,
            activity: FragmentActivity?
        ) {
            sharedPrefs?.clearSession()
            FirebaseAuth.getInstance().signOut()
            context.cacheDir.deleteRecursively()
            activity?.let {
                try {
                    // Stopping Session Ping Worker
                    WorkManager.getInstance(activity.applicationContext)
                        .cancelUniqueWork(SESSION_PING_REQUEST)
                    // Stopping Check-In Notification Worker
                    WorkManager.getInstance(activity.applicationContext)
                        .cancelUniqueWork(NOTIFICATION_PING_REQUEST)
                    // Stopping Fused Location Service
                    activity.stopService(Intent(activity, FusedLocationService::class.java))

                } catch (e: Exception) {
                    //Log.wtf("Application Logout", "Unable to close services.")
                }
            }
            val intent = Intent(context, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }

        fun isLocationPermissionGranted(context: Context): Boolean {
            return ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun isStoragePermissionGranted(context: Context): Boolean {
            return ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun formattedHomeDuration(t: Long?): String {
            return if (t != null) {
                val minutes = (t / 60 % 60).toInt()
                val hours = (t / (60 * 60) % 24).toInt()
                val days = (t / (60 * 60 * 24)).toInt()
                "$days d $hours h $minutes m"
            } else {
                "0d 0m 0s"
            }
        }

        val THRESHOLD: Double = 20.0  // in meters
        val STATUS_HOME = "home"
        val STATUS_AWAY = "away"

        @InternalCoroutinesApi
        @ExperimentalCoroutinesApi
        fun getSessionType(context: Context): String? {
            var sessionType: String? = null
            val checker = LegalChecker(context)
            if (checker.isCheating()) {
                return STATUS_AWAY
            }
            sharedPrefs?.let {
                val currentLat = it.currentLat
                val currentLong = it.currentLong
                val userLat = it.userLat
                val userLong = it.userLong
                sessionType = currentLat?.let {
                    currentLong?.let {
                        userLat?.let {
                            userLong?.let {
                                val dist = haversineFormula(
                                    currentLat.toDouble(),
                                    currentLong.toDouble(),
                                    userLat.toDouble(),
                                    userLong.toDouble()
                                )
                                //Log.wtf("Global Utils", "$dist")
                                return if (dist <= THRESHOLD) {
                                    STATUS_HOME
                                } else {
                                    STATUS_AWAY
                                }
                            }
                        }
                    }
                }
            }

            return sessionType
        }

        private fun haversineFormula(
            lat1: Double,
            lng1: Double,
            lat2: Double,
            lng2: Double
        ): Double {
            val r = 6371 // average radius of the earth in km
            val dLat = toRadians(lat2 - lat1)
            val dLon = toRadians(lng2 - lng1)
            val a =
                sin(dLat / 2) * sin(dLat / 2) + (cos(toRadians(lat1)) * cos(toRadians(lat2)) * sin(
                    dLon / 2
                ) * sin(dLon / 2))
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return (r * c * 1000) // in meters
        }

        /**
         * For creating notification for user
         */
        fun notifyUser(
            notificationId: Int,
            channelId: String,
            appContext: Context,
            pendingIntent: PendingIntent,
            priority: Int,
            title: String,
            content: String
        ) {
            val builder = NotificationCompat.Builder(appContext, channelId)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(title)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(priority)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(appContext)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId, builder.build())
            }
        }

        @InternalCoroutinesApi
        @ExperimentalCoroutinesApi
        fun isServiceRunning(context: Context, serviceClass: Class<Any>): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
            for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            return false
        }

        fun verifyDeviceTimeConfig(): Boolean {
            if (TrueTime.isInitialized()) {
                val trueTime = TrueTime.now().time
                val deviceTime = Calendar.getInstance().timeInMillis
                if (abs(deviceTime - trueTime) / 10000 > 0L) {
                    return false
                }
            }
            return true
        }

        fun loadImageNetworkCacheVisibility(url: String?, view: ImageView) {
            Picasso.get()
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .fit()
                .into(view, object : Callback {
                    override fun onSuccess() {
                        view.visibility = View.VISIBLE
                    }

                    override fun onError(e: Exception?) {
                        Picasso.get()
                            .load(url)
                            .fit()
                            .into(view, object : Callback {
                                override fun onSuccess() {
                                    view.visibility = View.VISIBLE
                                }

                                override fun onError(e: Exception?) {
                                    view.visibility = View.GONE
                                }

                            })
                    }

                })
        }

        fun loadImageNetworkCachePlaceholder(url: String?, view: ImageView) {
            Picasso.get()
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .fit()
                .into(view, object : Callback {
                    override fun onSuccess() {
                        view.alpha = 1f
                    }

                    override fun onError(e: Exception?) {
                        Picasso.get()
                            .load(url)
                            .fit()
                            .into(view, object : Callback {
                                override fun onSuccess() {
                                    view.alpha = 1f
                                }

                                override fun onError(e: Exception?) {
                                    view.alpha = 0.5f
                                    view.setImageResource(R.drawable.badge)
                                }

                            })
                    }

                })
        }

        fun parseJsonNullFieldValue(field: JsonElement?): JsonElement? {
            field?.let {
                if (it.isJsonNull)
                    return null
//                if(it.isJsonArray && it.asJsonArray.size()==0)
//                    return null
//                if(it.isJsonObject && it.asJsonObject.size()==0)
//                    return null
                return it
            }
            return null
        }
    }
}