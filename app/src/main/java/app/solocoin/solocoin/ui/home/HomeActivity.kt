package app.solocoin.solocoin.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.ui.auth.OnboardActivity
import app.solocoin.solocoin.util.GlobalUtils.Companion.startActivityAsNewStack
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (sharedPrefs?.authToken == null) {
            startActivityAsNewStack(this, OnboardActivity::class.java)
            finish()
            return
        }
    }
}
