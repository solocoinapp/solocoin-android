package app.solocoin.solocoin.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.ui.SplashActivity
import app.solocoin.solocoin.worker.LegalChecker
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.lang.Math.toRadians
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


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

        @InternalCoroutinesApi
        @ExperimentalCoroutinesApi
        fun logout(context: Context) {
            sharedPrefs?.clearSession()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }

        fun isLocationPermissionGranted(context: Context): Boolean {
            return ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }

        fun isStoragePermissionGranted(context: Context): Boolean {
            return ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
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
            var checker = LegalChecker(context);
            if(checker.isCheating()){
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
                                return if (dist <= THRESHOLD)
                                    STATUS_HOME
                                else
                                    STATUS_AWAY
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
    }
}