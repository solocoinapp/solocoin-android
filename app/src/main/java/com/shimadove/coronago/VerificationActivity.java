package com.shimadove.coronago;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import android.widget.Button;
import android.widget.ImageView;

import com.shimadove.coronago.databinding.ActivityVerificationBinding;

public class VerificationActivity extends AppCompatActivity {
    private static final String TAG = "PhoneAuthActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    private boolean mVerificationInProgress = false;
    private String mVerificationId;

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private EditText mPhoneNumberField;
    private EditText mVerificationField;
    TextView mCountyList;
    private ProgressBar progressBar;

    private String countryCode, enteredCode;
    private Button mStartButton;
    private boolean codeSent = false;
    private ImageView imageBanner;

    private FirebaseAuth mAuth;
    ActivityVerificationBinding binding;
    EditText phno,otpEnter;
    TextView enterNum, sendmsg;
    ImageButton btn_proceed;
    String verificationID;
    PhoneAuthProvider.ForceResendingToken token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_verification);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        initView();
        initMode();
    }

    private void initMode() {

    }

    private void initView() {

    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
        setContentView(R.layout.activity_verification);

        mAuth = FirebaseAuth.getInstance();
        phno =  findViewById(R.id.phno);
//        otpEnter= findViewById(R.id.otpEnter);
        progressBar = findViewById(R.id.progressBar);
        btn_proceed = findViewById(R.id.btn_proceed);
//        sendmsg = findViewById(R.id.sendmsg);

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phNum = "+91" + phno.getText().toString();
                Log.d("TAG","onClick: Phone NO-> " + phNum);
                if (!phNum.isEmpty() && phNum.length()==13){
                    progressBar.setVisibility(View.VISIBLE);
                    sendmsg.setText("Sending OTP...");
                    sendmsg.setVisibility(View.VISIBLE);
                    requestOTP(phNum);
                }else{
                    phno.setError("Phone number is not Valid.");
                }
            }
        });
    }

    private void requestOTP(String phNum) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phNum, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                progressBar.setVisibility(View.GONE);
                sendmsg.setVisibility(View.GONE);
                otpEnter.setVisibility(View.VISIBLE);
                verificationID=s;
                token=forceResendingToken;
            }
            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressBar.setVisibility(View.GONE);
                sendmsg.setVisibility(View.GONE);
//                Toast.makeText(VerificationActivity.this, "Cannot Create Account " + e.getMessage(), Toast.LENGTH_SHORT ).show();
                Log.e("LoginActivity", "Failed Registration", e);
            }
        });
    }
}
