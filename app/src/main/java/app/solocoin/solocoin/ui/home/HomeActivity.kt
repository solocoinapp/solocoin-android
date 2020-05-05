package app.solocoin.solocoin.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import app.solocoin.solocoin.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        bottom_nav_view.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottom_nav_view.selectedItemId = R.id.nav_home
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

    override fun onBackPressed() {
        if (bottom_nav_view.selectedItemId != R.id.nav_home) {
            bottom_nav_view.selectedItemId = R.id.nav_home
        } else {
            super.onBackPressed()
        }
    }
}
