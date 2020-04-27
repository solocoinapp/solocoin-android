package app.solocoin.solocoin.ui.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import app.solocoin.solocoin.R
import app.solocoin.solocoin.util.AppDialog
import app.solocoin.solocoin.util.GlobalUtils
import com.bigbangbutton.editcodeview.EditCodeListener
import com.bigbangbutton.editcodeview.EditCodeView
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_login_signup.*
import java.util.concurrent.TimeUnit

class LoginSignupActivity : AppCompatActivity(), View.OnClickListener, EditCodeListener {

    private lateinit var etMobileNumber: EditText
    private lateinit var otpView: EditCodeView

    private var otpCode = ""
    private var isOtpSent = false
    private var isOtpCodeReady = false

    private val loadingDialog = AppDialog.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_signup)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etMobileNumber = findViewById(R.id.et_mobile_number)
        otpView = findViewById(R.id.otp_view)
        otpView.setEditCodeListener(this)

        tv_resend_otp?.setOnClickListener(this)
        tv_get_otp?.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    val authCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            if (e is FirebaseAuthInvalidCredentialsException) {

            } else if (e is FirebaseTooManyRequestsException) {

            }
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
//            storedVerificationId = verificationId
//            resendToken = token
        }
    }

    private fun handleAuth() {
        val mobileNumber = etMobileNumber.text.toString()
        if (mobileNumber.length < 10) {
            etMobileNumber.error = getString(R.string.error_mobile_no)
            return
        }

        val fm = supportFragmentManager.beginTransaction().addToBackStack(null)
        loadingDialog.show(fm, "dialog")

        PhoneAuthProvider.getInstance().verifyPhoneNumber(mobileNumber, 60, TimeUnit.SECONDS, this, authCallbacks)

//        Handler().postDelayed({
//            isOtpSent = true
//
//            layout_otp?.visibility = VISIBLE
//            tv_get_otp?.text = getString(R.string.verify_otp)
//
//            object : CountDownTimer(60000, 1000) {
//                override fun onTick(millisUntilFinished: Long) {
//                    tv_resend_otp?.text = getString(R.string.resend_otp_timer, millisUntilFinished / 1000)
//                }
//
//                override fun onFinish() {
//                    tv_resend_otp?.isEnabled = true
//                    tv_resend_otp?.isClickable = true
//                    tv_resend_otp.text = getString(R.string.resend_otp)
//                }
//            }.start()
//
//            Toast.makeText(this, "Otp sent successfully", Toast.LENGTH_SHORT).show()
//            loadingDialog.dismiss()
//        }, 2400)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_resend_otp -> {
                Toast.makeText(this, "resend otp", Toast.LENGTH_SHORT).show()
            }
            R.id.tv_get_otp -> {
                if (isOtpSent) {
                    if (isOtpCodeReady) {
                        Log.d("xoxo", "otp entered, verify!")
                    } else {
                        Toast.makeText(this, "Please check OTP again!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    handleAuth()
                }
            }
        }
    }

    override fun onCodeReady(code: String?) {
        isOtpCodeReady = true
        otpCode = code!!
        GlobalUtils.closeKeyboard(this, otpView)
    }
}
