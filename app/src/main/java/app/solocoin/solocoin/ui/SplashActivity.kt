package app.solocoin.solocoin.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.ui.auth.MarkLocationActivity
import app.solocoin.solocoin.ui.auth.OnboardActivity
import app.solocoin.solocoin.ui.home.HomeActivity
import app.solocoin.solocoin.util.GlobalUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi


@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        /**
         * todo: experimental handler added, to be remove in production
         */
        Handler().postDelayed({
            if (sharedPrefs?.authToken != null) {
                if (sharedPrefs?.userLat == null || sharedPrefs?.userLong == null) {
                    GlobalUtils.startActivityAsNewStack(Intent(this, MarkLocationActivity::class.java), this)
                    finish()
                } else {
                    GlobalUtils.startActivityAsNewStack(Intent(this, HomeActivity::class.java), this)
                    finish()
                }
            } else {
                GlobalUtils.startActivityAsNewStack(Intent(this, OnboardActivity::class.java), this)
                finish()
            }
        }, 200)
    }
}
