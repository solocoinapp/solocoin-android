package app.solocoin.solocoin.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.ui.home.HomeActivity
import com.google.android.gms.location.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * Created by Ankur Kumar on 08/05/20
 */

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class FusedLocationService: Service() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocation: Location
    private lateinit var mLocationRequest: LocationRequest

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationUpdates()
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        try{
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            mLocationRequest = LocationRequest.create().apply {
                interval = 10 * 60 * 1000
                fastestInterval = 5 * 60 * 1000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
        } catch (exception: Exception){
            // Create an explicit intent for an Activity in your app
            val intent = Intent(applicationContext, HomeActivity::class.java)
            val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

            val builder = NotificationCompat.Builder(applicationContext, "1")
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("Important Update")
                .setContentText("Your location data was unable to be processed. Please check App Permissions in Settings to receive rewards.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(applicationContext)) {
                // notificationId is a unique int for each notification that you must define
                notify(1, builder.build())
            }
        }

    }

    private val mLocationListener = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            locationResult?.let {
                mLocation = it.lastLocation
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
        Log.wtf(TAG, "Location : (" + location.longitude + location.latitude + ")")
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
    }
}