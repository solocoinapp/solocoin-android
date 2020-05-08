package app.solocoin.solocoin.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import app.solocoin.solocoin.R
import app.solocoin.solocoin.util.AppDialog
import app.solocoin.solocoin.util.GlobalUtils
import kotlinx.android.synthetic.main.activity_mark_location.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class MarkLocationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_location)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
