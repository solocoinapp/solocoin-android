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
import kotlinx.android.synthetic.main.activity_get_free_coins.*
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
//            Log.wtf(TAG + " Mobile SignUp", "$resource")
            when(resource.status) {
                Status.SUCCESS -> {
                    loadingDialog.dismiss()

                    Log.wtf(TAG, "code: ${resource.code}")
                    if (resource.code == 200) {
                        val authToken =
                            GlobalUtils.parseJsonNullFieldValue(resource.data?.get("auth_token"))?.asString
                        val id =
                            GlobalUtils.parseJsonNullFieldValue(resource.data?.get("id"))?.asString
                        if (authToken != null) {
                            sharedPrefs?.authToken = authToken
                            sharedPrefs?.id = id

                            // Update user data at Api after successful SignUp
                            val _body = JsonObject()
                            //val user = JsonObject()
                            _body.addProperty("name", sharedPrefs?.name)
                            _body.addProperty("mobile", sharedPrefs?.mobileNumber)
                            _body.addProperty("lat", sharedPrefs?.userLat)
                            _body.addProperty("lng", sharedPrefs?.userLong)
                            //_body.add("user", user)
                            doApiUserUpdate(_body)

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

    private fun doApiUserUpdate(body: JsonObject) {
        Log.i("userupdate","body:"+body)
        Log.i("userupdate","auth:"+ sharedPrefs?.authToken)
        viewModel.userUpdate(body).observe(this@CreateProfileActivity, Observer { res ->
//            Log.wtf(TAG + " User Update", "$res")
            res?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        sharedPrefs?.visited = arrayListOf(true, true, true)
                        credit_refer_bonus(refer_code.text.toString())
                        /*GlobalUtils.startActivityAsNewStack(
                            Intent(
                                this@CreateProfileActivity,
                                HomeActivity::class.java
                            ), this@CreateProfileActivity
                        )
                        
                        finish()*/
                    }
                    Status.ERROR -> {
                        if (resource.exception is NoConnectivityException) {
                            Toast.makeText(
                                this@CreateProfileActivity,
                                resource.exception.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@CreateProfileActivity,
                                getString(R.string.error_msg),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    Status.LOADING -> {
                    }
                }
            }
        })
    }
    private fun credit_refer_bonus(refercode:String){
        val newusertoken= sharedPrefs?.authToken!!.substring(7)
        viewModel.refer_and_earn(newusertoken,refercode).observe(this, Observer { response ->
            Log.d(TAG,"refercodetest:"+response)
            Log.d(TAG,"refercodetest:"+response.status)

            Log.d(TAG, "refercodetest:"+sharedPrefs?.authToken)
            Log.d(TAG, "refercodetestx:"+newusertoken)
            Log.d(TAG, "refercodetest:"+refercode)
            when (response.status) {
                Status.SUCCESS -> {
                    when(response.code){
                        200->{
                        Toast.makeText(applicationContext,"Refer Code Redeemed successfully!",Toast.LENGTH_LONG).show()
                        }
                    }
                    GlobalUtils.startActivityAsNewStack(
                            Intent(
                                    this@CreateProfileActivity,
                                    HomeActivity::class.java
                            ), this@CreateProfileActivity
                    )
                }
                Status.ERROR -> {

                }
                Status.LOADING -> {
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

            if (sharedPrefs?.authToken != null) {
                body.addProperty("name", sharedPrefs?.name)
                body.addProperty("mobile", sharedPrefs?.mobileNumber)
                body.addProperty("lat", sharedPrefs?.userLat)
                body.addProperty("lng", sharedPrefs?.userLong)
               // body.add("user", user)

                doApiUserUpdate(body)


            } else {
                user.addProperty("name", sharedPrefs?.name)
                user.addProperty("country_code", sharedPrefs?.countryCode)
                user.addProperty("mobile", sharedPrefs?.mobileNumber)
                user.addProperty("uid", FirebaseAuth.getInstance().uid)
                user.addProperty("id_token", sharedPrefs?.idToken)
                body.add("user", user)
                doMobileSignup(body)
            }
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
                GlobalUtils.logout(this@CreateProfileActivity, null)
                finish()
            }
            override fun onClickCancel() {}
        }, getString(R.string.okay), getString(R.string.cancel))
        confirmDialog.show(supportFragmentManager, confirmDialog.tag)
    }
}
