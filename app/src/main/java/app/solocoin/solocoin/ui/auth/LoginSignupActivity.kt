package app.solocoin.solocoin.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import app.solocoin.solocoin.R
import app.solocoin.solocoin.util.AppDialog
import app.solocoin.solocoin.util.enums.DialogType
import kotlinx.android.synthetic.main.activity_login_signup.*

class LoginSignupActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_signup)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tv_get_otp?.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(p0: View?) {
        val dialog = AppDialog.instance()
        val fm = supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
        dialog.show(fm, "dialog")
        Handler().postDelayed({
            dialog.dismiss()
        }, 2400)
    }
}
