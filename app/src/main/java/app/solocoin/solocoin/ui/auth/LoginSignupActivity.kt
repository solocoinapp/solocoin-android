package app.solocoin.solocoin.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.repo.NoConnectivityException
import app.solocoin.solocoin.ui.SplashActivity
import app.solocoin.solocoin.util.AppDialog
import app.solocoin.solocoin.util.GlobalUtils
import app.solocoin.solocoin.util.enums.Status
import com.bigbangbutton.editcodeview.EditCodeListener
import com.bigbangbutton.editcodeview.EditCodeView
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.JsonObject
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import kotlinx.android.synthetic.main.activity_login_signup.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class LoginSignupActivity : AppCompatActivity(), View.OnClickListener, EditCodeListener {

    private val TAG = LoginSignupActivity::class.simpleName

    private val viewModel: LoginSignupViewModel by viewModel()

    private lateinit var countryCodePicker: CountryCodePicker
    private lateinit var etMobileNumber: EditText
    private lateinit var otpView: EditCodeView

    private lateinit var otpCode: String
    private lateinit var countryCode: String
    private lateinit var mobileNumber: String

    private var isOtpSent = false
    private val otpTimeout = 60L

    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var storedVerificationId: String

    private lateinit var mFirebaseAuth: FirebaseAuth
    private val loadingDialog = AppDialog.instance()

    private val timer = object: CountDownTimer(otpTimeout*1000, 1000) {
        override fun onFinish() {
            tv_resend_otp?.isEnabled = true
            tv_resend_otp?.isClickable = true
            tv_resend_otp.text = getString(R.string.resend_otp)
        }

        override fun onTick(p0: Long) {
            tv_resend_otp?.text = getString(R.string.resend_otp_timer, p0 / 1000)
        }
    }

    private val authCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        // This callback will be invoked in two situations:
        // 1 - Instant verification. In some cases the phone number can be instantly
        //     verified without needing to send or enter a verification code.
        // 2 - Auto-retrieval. On some devices Google Play services can automatically
        //     detect the incoming verification SMS and perform verification without
        //     user action.
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        // This callback is invoked in an invalid request for verification is made,
        // for instance if the the phone number format is not valid.
        override fun onVerificationFailed(e: FirebaseException) {
            loadingDialog.dismiss()

            val errorDialog = AppDialog.instance("Error!", getString(R.string.error_msg), null)
            errorDialog.show(supportFragmentManager, errorDialog.tag)
        }

        // The SMS verification code has been sent to the provided phone number, we
        // now need to ask the user to enter the code and then construct a credential
        // by combining the code with a verification ID.
        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            storedVerificationId = verificationId
            resendToken = token

            isOtpSent = true

            layout_otp?.visibility = VISIBLE
            tv_get_otp?.text = getString(R.string.verify_otp)

            timer.start()

            Toast.makeText(this@LoginSignupActivity, getString(R.string.otp_sent_success), Toast.LENGTH_SHORT).show()
            loadingDialog.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_signup)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mFirebaseAuth = FirebaseAuth.getInstance()

        countryCodePicker = findViewById(R.id.country_code_picker)
        etMobileNumber = findViewById(R.id.et_mobile_number)
        otpView = findViewById(R.id.otp_view)
        otpView.setEditCodeListener(this)

        tv_resend_otp?.setOnClickListener(this)
        tv_get_otp?.setOnClickListener(this)
        tv_change_number?.setOnClickListener(this)

    }

    // initialize phone_auth_provider sent otp
    // if resendToken == null, fresh otp sent
    // else force resend otp
    private fun handleAuth(resendToken: PhoneAuthProvider.ForceResendingToken?) {
        countryCode = countryCodePicker.selectedCountryCodeWithPlus
        mobileNumber = etMobileNumber.text.toString()

        if (mobileNumber.length < 10) {
            etMobileNumber.error = getString(R.string.error_mobile_no)
            return
        }

        val confirmDialog = AppDialog.instance("Send OTP",
            "Verify the mobile number ${countryCode+mobileNumber} and confirm",
            object: AppDialog.AppDialogListener{
                override fun onClickConfirm() {
                    tv_change_number?.visibility = VISIBLE
                    loadingDialog.show(supportFragmentManager, loadingDialog.tag)

                    if (resendToken == null) {
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(countryCode+mobileNumber, otpTimeout, TimeUnit.SECONDS, this@LoginSignupActivity, authCallbacks)
                    } else {
                        otpView.clearCode()
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(countryCode+mobileNumber, otpTimeout, TimeUnit.SECONDS, this@LoginSignupActivity, authCallbacks, resendToken)
                    }
                }

                override fun onClickCancel() {}
            }, "Confirm")
        confirmDialog.show(supportFragmentManager, confirmDialog.tag)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_resend_otp -> {
                handleAuth(resendToken)
            }

            R.id.tv_get_otp -> {
                if (isOtpSent) {
                    if (otpCode.length < 6) {
                        Toast.makeText(this, "Please check OTP again!", Toast.LENGTH_SHORT).show()
                        return
                    }
                    signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(storedVerificationId, otpCode))
                } else {
                    handleAuth(null)
                }
            }

            R.id.tv_change_number -> {
                val confirmDialog = AppDialog.instance("Confirm",
                    "Entered mobile number ${countryCode+mobileNumber} is not your number?",
                    object: AppDialog.AppDialogListener{
                        override fun onClickConfirm() {
                            changeNumberResetUi()
                        }

                        override fun onClickCancel() {}
                    }, "Yes")
                confirmDialog.show(supportFragmentManager, confirmDialog.tag)
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        if (mFirebaseAuth.currentUser != null) {
            sharedPrefs?.clearSession()
            mFirebaseAuth.signOut()
        }

        mFirebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    val uid = task.result!!.user!!.uid
                    Log.wtf(TAG, "user-id: $uid")

                    task.result?.user?.getIdToken(true)?.addOnCompleteListener { task1 ->
                        sharedPrefs?.idToken = task1.result?.token
                        sharedPrefs?.countryCode = countryCode
                        sharedPrefs?.mobileNumber = mobileNumber
                        Log.wtf(TAG, sharedPrefs?.idToken)
                        val body = JsonObject()
                        val user = JsonObject()
                        user.addProperty("country_code", sharedPrefs?.countryCode)
                        user.addProperty("mobile", sharedPrefs?.mobileNumber)
                        user.addProperty("id_token", sharedPrefs?.idToken)
                        user.addProperty("uid", uid)
                        body.add("user", user)

                        viewModel.mobileLogin(body).observe(this, Observer {
                            it?.let { resource ->
                                when (resource.status) {
                                    Status.SUCCESS -> {
                                        loadingDialog.dismiss()
                                        Log.d(TAG, "mobilelogin-rc: ${resource.code}, ${resource}")

                                        if (resource.code == 200) {
                                            //existing-user

                                            sharedPrefs?.authToken = resource.data!!.get("auth_token").asString
                                            //get-user-data
                                            viewModel.userData().observe(this, Observer {res ->
                                                res?.let { resource ->
                                                    when (resource.status) {
                                                        Status.SUCCESS -> {
                                                            if (resource.code == 200) {
                                                                sharedPrefs?.userLat = resource.data?.get("lat")?.asString
                                                                sharedPrefs?.userLong = resource.data?.get("lng")?.asString
                                                                sharedPrefs?.name = resource.data?.get("name")?.asString

                                                                GlobalUtils.startActivityAsNewStack(Intent(this, SplashActivity::class.java), this)
                                                                finish()
                                                            } else {
                                                                Toast.makeText(this, getString(R.string.error_msg), Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                        Status.ERROR -> {
                                                            if (resource.exception is NoConnectivityException) {
                                                                Toast.makeText(this, resource.exception.message, Toast.LENGTH_SHORT).show()
                                                            } else {
                                                                Toast.makeText(this, getString(R.string.error_msg), Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                        Status.LOADING -> {}
                                                    }
                                                }
                                            })
                                            //get-user-data

                                            //existing-user
                                        } else if (resource.code == 401) {
                                            //new-user
                                            GlobalUtils.startActivityAsNewStack(Intent(this, MarkLocationActivity::class.java), this)
                                            finish()
                                            //new-user
                                        }
                                    }
                                    Status.ERROR -> {
                                        loadingDialog.dismiss()

                                        if (resource.exception is NoConnectivityException) {
                                            Toast.makeText(this, resource.exception.message, Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(this, getString(R.string.error_msg), Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    Status.LOADING -> {}
                                }
                            }
                        })
                    }
                } else {
                    loadingDialog.dismiss()

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, getString(R.string.error_wrong_otp), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, getString(R.string.error_msg), Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    override fun onCodeReady(code: String?) {
        otpCode = code!!
        GlobalUtils.closeKeyboard(this, otpView)
    }

    private fun changeNumberResetUi() {
        timer.cancel()
        isOtpSent = false
        layout_otp?.visibility = GONE

        otp_view.clearCode()
        et_mobile_number.setText("")
        tv_get_otp.text = getString(R.string.get_otp)
        tv_resend_otp.text = ""

        tv_change_number.visibility = GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (sharedPrefs?.authToken != null || FirebaseAuth.getInstance().currentUser != null) {
            val confirmDialog = AppDialog.instance(getString(R.string.confirm), getString(R.string.clear_current_session), object: AppDialog.AppDialogListener {
                override fun onClickConfirm() {
                    GlobalUtils.logout(applicationContext, null)
                    finish()
                }
                override fun onClickCancel() {}
            }, getString(R.string.okay), getString(R.string.cancel))
            confirmDialog.show(supportFragmentManager, confirmDialog.tag)
        } else {
            super.onBackPressed()
        }
    }
}
