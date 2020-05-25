package app.solocoin.solocoin.services

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import com.google.android.gms.location.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * Created by Ankur Kumar on 08/05/20
 */

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
object FusedLocationService : Service() {

    private val TAG = FusedLocationService::class.simpleName

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocation: Location
    private lateinit var mLocationRequest: LocationRequest

    /*
     * To handle case when service is already running
     * @ref : https://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android
     */
    @JvmStatic
    var isRunning: Boolean = false


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationUpdates()
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationRequest = LocationRequest.create().apply {
            interval = 10 * 60 * 1000
            fastestInterval = 5 * 60 * 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        // TODO : check whether Settings for the LocationRequest are available or not
    }

    private val mLocationListener = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            isRunning = true
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
}