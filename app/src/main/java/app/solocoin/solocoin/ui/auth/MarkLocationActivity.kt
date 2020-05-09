package app.solocoin.solocoin.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.ui.home.HomeActivity
import app.solocoin.solocoin.ui.home.HomeFragment
import app.solocoin.solocoin.util.AppDialog
import app.solocoin.solocoin.util.GlobalUtils
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
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


/**
 * on-continue click, check for username
 * if null redirect to create account
 * else to home activity
 */

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class MarkLocationActivity : AppCompatActivity(), PermissionListener, View.OnClickListener {

    private val TAG = MarkLocationActivity::class.simpleName
    private var dexterInstance: DexterBuilder ?= null

    private var mapFragment: SupportMapFragment? = null
    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_location)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupPermissionEngine()
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        btn_confirm.setOnClickListener(this)
    }

    private fun setupPermissionEngine() {
        dexterInstance = Dexter.withContext(this)
            .withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(this)
            .onSameThread()
    }

    override fun onStart() {
        super.onStart()
        dexterInstance?.check()
    }

    private val mOnMapReadyCallback =
        OnMapReadyCallback { googleMap ->
            mMap = googleMap
            mMap?.isMyLocationEnabled = true
        }

    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
        mapFragment?.getMapAsync(mOnMapReadyCallback)
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

    override fun onClick(p0: View?) {
        if (GlobalUtils.isLocationPermissionGranted(this)) {
            if (sharedPrefs?.userLat == null || sharedPrefs?.userLong == null) {
                Toast.makeText(this, "Please add your location first!", Toast.LENGTH_SHORT).show()
                return
            }
            if (sharedPrefs?.name == null) {
                GlobalUtils.startActivityAsNewStack(Intent(this, CreateProfileActivity::class.java), this)
                finish()
            } else {
                GlobalUtils.startActivityAsNewStack(Intent(this, HomeActivity::class.java), this)
                finish()
            }
        } else {
            dexterInstance?.check()
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
                GlobalUtils.logout(applicationContext)
                finish()
            }
            override fun onClickCancel() {}
        }, getString(R.string.okay), getString(R.string.cancel))
        confirmDialog.show(supportFragmentManager, confirmDialog.tag)
    }
}
