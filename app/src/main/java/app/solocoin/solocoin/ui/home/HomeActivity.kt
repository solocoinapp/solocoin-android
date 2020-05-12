package app.solocoin.solocoin.ui.home

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.repo.NoConnectivityException
import app.solocoin.solocoin.services.FusedLocationService
import app.solocoin.solocoin.ui.auth.LoginSignupViewModel
import app.solocoin.solocoin.util.enums.Status
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class HomeActivity : AppCompatActivity() {

    private val TAG = HomeActivity::class.java.simpleName

    private val PERMISSION_REQUEST_CODE = 34

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setSupportActionBar(toolbar)
        bottom_nav_view.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottom_nav_view.selectedItemId = R.id.nav_home

        checkPermissionForLocation()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE){
            when {
                grantResults.isEmpty() -> Log.d(TAG, "User Interaction Cancelled")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> startFusedLocationService()
                else -> Log.d(TAG, "Permissions Denied by User")

                // TODO Show message when user denies location permissions or user interaction is cancelled
            }
        }
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

        if (permissionsArray.count() == 0) {
            startFusedLocationService()

        } else {
            ActivityCompat.requestPermissions(this, permissionsArray, PERMISSION_REQUEST_CODE)
        }
    }

    private fun startFusedLocationService(){
        val intent = Intent(this, FusedLocationService::class.java)
        startService(intent)
    }

    override fun onBackPressed() {
        if (bottom_nav_view.selectedItemId != R.id.nav_home) {
            bottom_nav_view.selectedItemId = R.id.nav_home
        } else {
            super.onBackPressed()
        }
    }
}
