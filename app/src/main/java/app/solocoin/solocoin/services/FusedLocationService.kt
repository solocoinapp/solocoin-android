package app.solocoin.solocoin.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*

/**
   * Created by Ankur Kumar on 08/05/20
 */

class FusedLocationService: Service() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var location: Location
    private lateinit var locationRequest: LocationRequest


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        getLastLocation()
        return START_NOT_STICKY
    }

    fun getLastLocation(){
        fusedLocationProviderClient.lastLocation
            .addOnCompleteListener{ taskLocation ->
                if (taskLocation.isSuccessful && taskLocation.result != null){
                    location = taskLocation.result!!                    /** Get location coordinates here if last location is known*/

                }
                else {
                    fetchNewLocation()
                }
            }

    }

    fun fetchNewLocation() {


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {

                location = locationResult.lastLocation                 /** If last location is not known then new location is fetched here*/
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }


}