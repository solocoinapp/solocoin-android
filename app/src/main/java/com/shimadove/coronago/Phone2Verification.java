package com.shimadove.coronago;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bigbangbutton.editcodeview.EditCodeListener;
import com.bigbangbutton.editcodeview.EditCodeView;
import com.bigbangbutton.editcodeview.EditCodeWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.shimadove.coronago.databinding.ActivityPhone2VerificationBinding;
//import com.shimadove.coronago.databinding.ActivityVerificationBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Phone2Verification extends AppCompatActivity {
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
    ActivityPhone2VerificationBinding binding;
    EditText phno, otpEnter;
    TextView enterNum, sendmsg;
    //ImageButton btn_proceed;

    String verificationID;
    PhoneAuthProvider.ForceResendingToken token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding = DataBindingUtil.setContentView( this, R.layout.activity_phone2_verification);
        if (savedInstanceState != null) {
            onRestoreInstanceState( savedInstanceState );
        }
        //progressBar.setVisibility(View.GONE);
        progressBar=binding.progressBar;
        progressBar.setVisibility(View.GONE);
        phno = binding.phno;
        initView( );
        initMode( );
        EditCodeView editCodeView = (EditCodeView) findViewById(R.id.edit_code);
        String s;
        editCodeView.setEditCodeListener(new EditCodeListener() {
            @Override
            public void onCodeReady(String code) {
                //This function gives the complete number inputted
            }
        });
        editCodeView.setEditCodeWatcher(new EditCodeWatcher() {
            @Override
            public void onCodeChanged(String code) {

            }
        });
    }

    private void initMode() {
        Intent intent = getIntent();
        String phnum=intent.getStringExtra(Phone1Verification.PHONE_NO);
        phno.setText(phnum);
    }

    private void initView() {

    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState( savedInstanceState );
        mVerificationInProgress = savedInstanceState.getBoolean( KEY_VERIFY_IN_PROGRESS );
        setContentView( R.layout.activity_phone2_verification);

        mAuth = FirebaseAuth.getInstance( );
        phno = findViewById( R.id.phno );
//        otpEnter= findViewById(R.id.otpEnter);
        //progressBar = findViewById( R.id.progressBar );
        //btn_proceed = findViewById( R.id.btn_proceed );
//        sendmsg = findViewById(R.id.sendmsg);
    }

    private void requestOTP(String phNum) {
        PhoneAuthProvider.getInstance( ).verifyPhoneNumber( phNum, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks( ) {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent( s, forceResendingToken );
                //progressBar.setVisibility( View.GONE );
//                sendmsg.setVisibility(View.GONE);
//                otpEnter.setVisibility(View.VISIBLE);
                verificationID = s;
                token = forceResendingToken;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut( s );
                Toast.makeText( Phone2Verification.this, "TimeOut", Toast.LENGTH_SHORT ).show( );
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText( Phone2Verification.this, "Create Account", Toast.LENGTH_SHORT ).show( );
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
//                progressBar.setVisibility( View.GONE );
//                sendmsg.setVisibility(View.GONE);
//                Toast.makeText(VerificationActivity.this, "Cannot Create Account " + e.getMessage(), Toast.LENGTH_SHORT ).show();
                System.out.println( "LoginActivity Failed Registration: " + e );
            }
        } );
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential( credential )
                .addOnCompleteListener( this, new OnCompleteListener<AuthResult>( ) {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful( )) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d( TAG, "signInWithCredential:success" );

                            FirebaseUser user = task.getResult( ).getUser( );
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w( TAG, "signInWithCredential:failure", task.getException( ) );
                            if (task.getException( ) instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                } );
    }
}