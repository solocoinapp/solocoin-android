package app.solocoin.solocoin.ui.home

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import app.solocoin.solocoin.R
import app.solocoin.solocoin.services.FusedLocationService
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setSupportActionBar(toolbar)
        bottom_nav_view.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottom_nav_view.selectedItemId = R.id.nav_home

        // TODO : Setup permission request for Fused Location service properly
        checkPermissionForLocation()
        viewModel.startSessionPingManager()
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
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
            R.id.nav_leaderboard -> {
                toolbar.title = getString(R.string.leaderboard)
                openFragment(LeaderboardFragment.instance())
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

    private fun checkPermissionForLocation(){

        var permissionsArray = arrayOf<String>()

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissionsArray = permissionsArray.plus(android.Manifest.permission.ACCESS_FINE_LOCATION)

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissionsArray = permissionsArray.plus(android.Manifest.permission.ACCESS_COARSE_LOCATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissionsArray = permissionsArray.plus(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
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

    private fun startFusedLocationService(){
        Log.wtf(TAG, "Starting the fused location service.")
        val intent = Intent(this, FusedLocationService::class.java)
        applicationScope.launch {
            startService(intent)
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
        private val applicationScope = CoroutineScope(Dispatchers.Default)
    }
}
