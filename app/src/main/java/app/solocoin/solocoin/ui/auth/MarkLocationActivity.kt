package app.solocoin.solocoin.ui.auth

import android.content.Intent
import android.content.IntentSender
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.repo.NoConnectivityException
import app.solocoin.solocoin.ui.home.HomeActivity
import app.solocoin.solocoin.util.AppDialog
import app.solocoin.solocoin.util.GlobalUtils
import app.solocoin.solocoin.util.enums.Status
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.gson.JsonObject
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_mark_location.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.IOException
import java.util.*

/**
 * on-continue click, check for username
 * if null redirect to create account
 * else to home activity
 */

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class MarkLocationActivity : AppCompatActivity(), PermissionListener, View.OnClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private val TAG = MarkLocationActivity::class.simpleName

    private val viewModel: MarkYourLocationViewModel by viewModel()

    private lateinit var  dexterInstance: DexterBuilder

    private var mapFragment: SupportMapFragment? = null
    private var mMap: GoogleMap? = null

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocation: Location

    private var mMarkerOption: MarkerOptions? = null
    private var isMarkerAdded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_location)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupPermissionEngine()
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        mMarkerOption = MarkerOptions()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationRequest = LocationRequest.create().apply {
            interval = 1000 * 60 * 5
            fastestInterval = 1000 * 60 * 2
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        btn_confirm.setOnClickListener(this)
        tip_current_location?.setEndIconOnClickListener {
            if (GlobalUtils.isLocationPermissionGranted(this)) {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationListener, Looper.getMainLooper())
                Toast.makeText(this, getString(R.string.refreshing_location), Toast.LENGTH_SHORT).show()
            } else {
                dexterInstance.check()
            }
        }
    }

    private fun setupPermissionEngine() {
        dexterInstance = Dexter.withContext(this)
            .withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(this)
            .onSameThread()
    }

    override fun onStart() {
        super.onStart()
        dexterInstance.check()
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        if (p0 != null) {
            mMap?.clear()
            isMarkerAdded = false
        }
        return false
    }

    override fun onMapClick(p0: LatLng?) {
        mMap?.clear()

        mMarkerOption?.title("You are here")
        mMarkerOption?.position(p0!!)
        mMap?.addMarker(mMarkerOption)
        isMarkerAdded = true
    }

    private val mOnMapReadyCallback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        mMap?.isMyLocationEnabled = true

        mMap?.setOnMapClickListener(this)
        mMap?.setOnMarkerClickListener(this)
    }

    private fun geocoder() {
        val addresses: List<Address>
        val gc = Geocoder(this, Locale.getDefault())

        try {
            addresses = gc.getFromLocation(mLocation.latitude, mLocation.longitude, 1)
            val city: String = addresses[0].locality
            val state: String = addresses[0].adminArea
            val country: String = addresses[0].countryName
            et_location.setText(getString(R.string.current_address, city, state, country))
        } catch (e: IOException) {}
    }

    private val mLocationListener = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
            if (p0 != null) {
                mLocation = p0.lastLocation
                val latLng = LatLng(mLocation.latitude, mLocation.longitude)
                mMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))

                geocoder()
            }
        }
    }

    private fun displayLocationSettingsRequest() {
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        builder.setAlwaysShow(true)
        builder.setNeedBle(true)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
//        task.addOnSuccessListener {locationSettingsResponse ->}
        task.addOnFailureListener {exception ->
            if (exception is ResolvableApiException){
                try {
                    exception.startResolutionForResult(this, 102)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "PendingIntent unable to execute request.")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 102 && resultCode != RESULT_OK) {
            displayLocationSettingsRequest()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
        mapFragment?.getMapAsync(mOnMapReadyCallback)

        //location-update
        displayLocationSettingsRequest()
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationListener, Looper.getMainLooper())
        //location-update
    }

    override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {
        p1?.continuePermissionRequest()
    }

    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
        if (p0!!.isPermanentlyDenied) {
            val permDialog = AppDialog.instance(getString(R.string.permission), getString(R.string.tag_permission), object: AppDialog.AppDialogListener {
                override fun onClickConfirm() {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
                override fun onClickCancel() {}
            }, getString(R.string.okay), getString(R.string.cancel))
            permDialog.show(supportFragmentManager, permDialog.tag)
        }
    }

    private fun doApiUserUpdate(body: JsonObject) {
        viewModel.userUpdate(body).observe(this@MarkLocationActivity, androidx.lifecycle.Observer { res ->
            res?.let { resource ->
                when(resource.status) {
                    Status.SUCCESS -> {
                        GlobalUtils.startActivityAsNewStack(Intent(this@MarkLocationActivity, HomeActivity::class.java), this@MarkLocationActivity)
//                        finish()
                    }
                    Status.ERROR -> {
                        if (resource.exception is NoConnectivityException) {
                            Toast.makeText(this@MarkLocationActivity, resource.exception.message, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@MarkLocationActivity, getString(R.string.error_msg), Toast.LENGTH_SHORT).show()
                        }
                    }
                    Status.LOADING -> {}
                }
            }
        })
    }

    override fun onClick(p0: View?) {
        if (GlobalUtils.isLocationPermissionGranted(this)) {
            if (isMarkerAdded) {
                val confirmDialog = AppDialog.instance(getString(R.string.confirm), "Do you want to continue with marked location?", object: AppDialog.AppDialogListener {
                    override fun onClickConfirm() {
                        sharedPrefs?.userLat = mMarkerOption!!.position.latitude.toString()
                        sharedPrefs?.userLong = mMarkerOption!!.position.longitude.toString()

                        if (sharedPrefs?.authToken == null) {
                            GlobalUtils.startActivityAsNewStack(Intent(applicationContext, CreateProfileActivity::class.java), applicationContext)
                            finish()
                        } else {
                            val body = JsonObject()
                            val user = JsonObject()
                            user.addProperty("lat", sharedPrefs?.userLat)
                            user.addProperty("lng", sharedPrefs?.userLong)
                            body.add("user", user)
                            doApiUserUpdate(body)
                        }
                    }
                    override fun onClickCancel() {}
                }, getString(R.string.okay), getString(R.string.cancel))
                confirmDialog.show(supportFragmentManager, confirmDialog.tag)
            } else {
                sharedPrefs?.userLat = mLocation.latitude.toString()
                sharedPrefs?.userLong = mLocation.longitude.toString()

                if (sharedPrefs?.authToken == null) {
                    GlobalUtils.startActivityAsNewStack(Intent(applicationContext, CreateProfileActivity::class.java), applicationContext)
                    finish()
                } else {
                    val body = JsonObject()
                    val user = JsonObject()
                    user.addProperty("lat", sharedPrefs?.userLat)
                    user.addProperty("lng", sharedPrefs?.userLong)
                    body.add("user", user)
                    doApiUserUpdate(body)
                }
            }
        } else {
            dexterInstance.check()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val confirmDialog = AppDialog.instance(getString(R.string.confirm), getString(R.string.clear_current_session), object: AppDialog.AppDialogListener {
            override fun onClickConfirm() {
                GlobalUtils.logout(applicationContext, null)
                finish()
            }
            override fun onClickCancel() {}
        }, getString(R.string.okay), getString(R.string.cancel))
        confirmDialog.show(supportFragmentManager, confirmDialog.tag)
    }

    override fun onDestroy() {
        super.onDestroy()
        mFusedLocationClient.removeLocationUpdates(mLocationListener)
    }
}
