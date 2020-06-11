package app.solocoin.solocoin.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.ui.home.HomeActivity
import app.solocoin.solocoin.util.GlobalUtils
import com.google.android.gms.location.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * Created by Ankur Kumar on 08/05/20
 */

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@RequiresApi(Build.VERSION_CODES.N)
class FusedLocationService : Service() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocation: Location
    private lateinit var mLocationRequest: LocationRequest

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        mFusedLocationClient.removeLocationUpdates(mLocationListener)
        //Log.wtf(TAG, "Stopping Fused location service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationUpdates()
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            mLocationRequest = LocationRequest.create().apply {
                interval = 10 * 60 * 1000
                fastestInterval = 5 * 60 * 1000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
        } catch (e: Exception) {
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                Intent(applicationContext, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                },
                0
            )
            GlobalUtils.notifyUser(
                notificationId,
                channelId,
                applicationContext,
                pendingIntent,
                NotificationManager.IMPORTANCE_HIGH,
                "Location Permission Request",
                "Your location data was unable to be processed. Please check App Permissions in Settings to receive rewards."
            )
        }
    }

    private val mLocationListener = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            locationResult?.let {
                mLocation = it.lastLocation
//                if(mLocation.isFromMockProvider){
//                    sharedPrefs?.let{
//                        it.mock = true;
//                    }
//                }
                updateSharedPrefs(mLocation)
            }
        }
    }

    private fun startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationListener,
            Looper.getMainLooper()
        )
    }

    /*
     * Updates user current location in shared preferences.
     * Required by work manager at regular interval.
     */
    private fun updateSharedPrefs(location: Location) {
        //Log.wtf(TAG, "Location : (" + location.longitude + location.latitude + ")")
        sharedPrefs?.let {
            it.currentLat = location.latitude.toString()
            it.currentLong = location.longitude.toString()
            if (it.userLat == null) {
                it.userLat = location.latitude.toString()
                it.userLong = location.longitude.toString()
            }
        }
    }

    companion object {
        private val TAG = FusedLocationService::class.simpleName
        private const val notificationId = 321
        private const val channelId = "1"
    }
}