package app.solocoin.solocoin.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.ui.auth.CreateProfileActivity
import app.solocoin.solocoin.ui.auth.MarkLocationActivity
import app.solocoin.solocoin.ui.auth.OnboardActivity
import app.solocoin.solocoin.ui.home.HomeActivity
import app.solocoin.solocoin.util.GlobalUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi


@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this) { pendingDynamicLinkData ->
                    // Get deep link from result (may be null if no link is found)
                    var deepLink: Uri? = null
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.link
                        val s = deepLink.toString().substring(getString(R.string.app_link).length)
                        Log.e("Tagkarandeep3", "old_user_id:"+s+"newuserid:"+ sharedPrefs?.mobileNumber)
                    }
                    // Handle the deep link. For example, open the linked
                    // content, or apply promotional credit to the user's
                    // account.
                    // ...
                    // ...
                }
                .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }

        Handler().postDelayed({
            if (FirebaseAuth.getInstance().currentUser?.uid != null) {
                if (sharedPrefs?.userLat == null || sharedPrefs?.userLong == null) {
                    GlobalUtils.startActivityAsNewStack(Intent(this, MarkLocationActivity::class.java), this)
                    finish()
                } else if (sharedPrefs?.name == null) {
                    GlobalUtils.startActivityAsNewStack(Intent(this, CreateProfileActivity::class.java), this)
                    finish()
                } else {
                    GlobalUtils.startActivityAsNewStack(Intent(this, HomeActivity::class.java), this)
                    finish()
                }
            } else {
                GlobalUtils.startActivityAsNewStack(Intent(this, OnboardActivity::class.java), this)
                finish()
            }
        }, 500)
    }

}
