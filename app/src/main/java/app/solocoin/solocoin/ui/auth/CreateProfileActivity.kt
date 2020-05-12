package app.solocoin.solocoin.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.repo.NoConnectivityException
import app.solocoin.solocoin.ui.home.HomeActivity
import app.solocoin.solocoin.util.AppDialog
import app.solocoin.solocoin.util.GlobalUtils
import app.solocoin.solocoin.util.enums.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_create_profile.*
import kotlinx.android.synthetic.main.activity_mark_location.btn_confirm
import kotlinx.android.synthetic.main.activity_mark_location.toolbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel


@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class CreateProfileActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = CreateProfileActivity::class.simpleName

    private val viewModel: CreateProfileViewModel by viewModel()
    private var loadingDialog = AppDialog.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //setup-tnc-text
        val terms: String = getString(R.string.tag_tnc)
        val ss = SpannableString(terms)
        val tncStart = terms.indexOf("Terms")
        val ppStart = terms.indexOf("Privacy")
        val tncSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_tnc))))
            }
        }
        val ppSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_pp))))
            }
        }
        ss.setSpan(tncSpan, tncStart, tncStart + 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(ppSpan, ppStart, ppStart + 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_tnc.text = ss
        tv_tnc.movementMethod = LinkMovementMethod.getInstance()
        //setup-tnc-text

        btn_confirm.setOnClickListener(this)
    }

    private fun doMobileSignup(body: JsonObject) {
        viewModel.mobileSignup(body).observe(this, Observer {resource ->
            when(resource.status) {
                Status.SUCCESS -> {
                    loadingDialog.dismiss()

                    Log.d(TAG, "code: ${resource.code}")
                    if (resource.code == 200) {
                        val authToken = resource.data?.get("auth_token")?.asString
                        val id = resource.data?.get("id")?.asString
                        if (authToken != null) {
                            sharedPrefs?.authToken = authToken
                            sharedPrefs?.id = id

                            GlobalUtils.startActivityAsNewStack(Intent(this@CreateProfileActivity, HomeActivity::class.java), this@CreateProfileActivity)
                            finish()
                        } else {
                            Toast.makeText(this@CreateProfileActivity, getString(R.string.error_msg), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@CreateProfileActivity, getString(R.string.error_msg), Toast.LENGTH_SHORT).show()
                    }
                }
                Status.ERROR -> {
                    loadingDialog.dismiss()

                    if (resource.exception is NoConnectivityException) {
                        Toast.makeText(this@CreateProfileActivity, resource.exception.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@CreateProfileActivity, getString(R.string.error_msg), Toast.LENGTH_SHORT).show()
                    }
                }
                Status.LOADING -> {
                    loadingDialog.show(supportFragmentManager, loadingDialog.tag)
                }
            }
        })
    }

    override fun onClick(p0: View?) {
        val name = et_name.text.toString()
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter name, to continue!", Toast.LENGTH_SHORT).show()
        } else {
            sharedPrefs?.name = name

            val body = JsonObject()
            val user = JsonObject()
            user.addProperty("name", sharedPrefs?.name)
            user.addProperty("country_code", sharedPrefs?.countryCode)
            user.addProperty("mobile", sharedPrefs?.mobileNumber)
            user.addProperty("uid", FirebaseAuth.getInstance().uid)
            user.addProperty("id_token", sharedPrefs?.idToken)
            body.add("user", user)
            doMobileSignup(body)
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
                GlobalUtils.logout(this@CreateProfileActivity)
                finish()
            }
            override fun onClickCancel() {}
        }, getString(R.string.okay), getString(R.string.cancel))
        confirmDialog.show(supportFragmentManager, confirmDialog.tag)
    }
}
