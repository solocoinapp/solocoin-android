package app.solocoin.solocoin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bigbangbutton.editcodeview.EditCodeListener;
import com.bigbangbutton.editcodeview.EditCodeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import app.solocoin.solocoin.api.APIClient;
import app.solocoin.solocoin.api.APIService;
import app.solocoin.solocoin.api.PostUser;
import app.solocoin.solocoin.api.PostUserLogin;
import app.solocoin.solocoin.api.UserLogin;
import app.solocoin.solocoin.api.UserSignUp;
import app.solocoin.solocoin.app.SharedPref;
import app.solocoin.solocoin.databinding.ActivityPhone2VerificationBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
//import app.solocoin.solocoin.databinding.ActivityVerificationBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

@SuppressLint("LogNotTimber")
public class Phone2Verification extends AppCompatActivity {

//    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
//    private static final int STATE_INITIALIZED = 1;
//    private static final int STATE_CODE_SENT = 2;
//    private static final int STATE_VERIFY_FAILED = 3;
//    private static final int STATE_VERIFY_SUCCESS = 4;
//    private static final int STATE_SIGNIN_FAILED = 5;
//    private static final int STATE_SIGNIN_SUCCESS = 6;
//    private boolean mVerificationInProgress = false;
//    private String mVerificationId;
//    private PhoneAuthProvider.ForceResendingToken mResendToken;
//private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private static final String TAG = "PhoneAuthActivity";

    ActivityPhone2VerificationBinding binding;

    private String otpBySystem;
    private String phoneNo;
    private String OtpEnteredByUser;
    private String countryCode, enteredCode;
    private boolean codeSent = false;
    private boolean timeout=false;
    private boolean incorrect=false;
    private String verificationID;
    private String firebaseUid;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken token;

    private SharedPref sharedPref;
    private APIService apiService;

    private EditText mPhoneNumberField;
    private EditText mVerificationField;
    private TextView mCountyList;
    private ProgressBar progressBar;
    private Button mStartButton;
    private ImageView imageBanner;
    private Button verifyBtn;
    private EditText phno, otpEnter;
    private TextView enterNum, sendmsg;
    private TextView resend;
    private TextView tandc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding = DataBindingUtil.setContentView( this, R.layout.activity_phone2_verification);
        if (savedInstanceState != null) {
            onRestoreInstanceState( savedInstanceState );
        }
        apiService = APIClient.getRetrofitInstance(this).create(APIService.class);
        sharedPref=SharedPref.getInstance(this);
        resend=binding.textView7;
        verifyBtn=binding.verifyBtn;
        progressBar=binding.progressBar;
        progressBar.setVisibility(View.GONE);
        phno = binding.phno;
        phoneNo = getIntent().getStringExtra("PHONE_NO");
        //phno.setText(phoneNo);
        phno.getText().clear();
        phno.append(phoneNo);
        final EditCodeView editCodeView = findViewById(R.id.edit_code);
        String s;
        editCodeView.setEditCodeListener(code -> {
            //This function gives the complete number inputted
            OtpEnteredByUser=code;
        });
        //OtpEnteredByUser=editCodeView.getCode();
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timeout||incorrect){
                    Toast.makeText(Phone2Verification.this,"Resending OTP..",Toast.LENGTH_SHORT).show();
                    timeout=false;
                    incorrect=false;
                    sendVerificationCodeToUser(phoneNo);
                }
            }
        });
        sendVerificationCodeToUser(phoneNo);
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code=OtpEnteredByUser;
                if(code != null){
                    if(code.isEmpty()||code.length()<6){
                        Toast.makeText(Phone2Verification.this,"Wrong OTP..",Toast.LENGTH_SHORT).show();
                        editCodeView.requestFocus();
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        verifyCode(code);
                    }
                } else {
                    Toast.makeText(Phone2Verification.this, "Please check your code.", Toast.LENGTH_LONG).show();
                }

            }
        });
        tandc=binding.textView8;
        String terms=tandc.getText().toString();
        SpannableString ss= new SpannableString(terms);
        int termstart=terms.indexOf("Terms of Service");
        int pristart=terms.indexOf("Privacy Policy");
        ClickableSpan ToS = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent=new Intent(getApplicationContext(),ToS.class);
                startActivity(intent);
            }
        };
        ClickableSpan Policy = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent=new Intent(getApplicationContext(),Policy.class);
                startActivity(intent);
            }
        };
        ss.setSpan(ToS,termstart,termstart+16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(Policy,pristart,pristart+14,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tandc.setText(ss);
        tandc.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                 phoneNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            otpBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code!=null){
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(Phone2Verification.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String otpByUser) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpBySystem, otpByUser);
            signInWithPhoneAuthCredential(credential);
        } catch (Exception ex) {
            Log.d(TAG, "Error: " + ex.getMessage());
            if (ex.getMessage() != null && ex.getMessage().contains("Cannot create PhoneAuthCredential")) {
                Toast.makeText(this, "Otp validation error, please contact team!!!", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Please try again!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth= FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener( Phone2Verification.this, task -> {
                    if (task.isSuccessful()) {
                        //TODO - make isNewUser == false in if statement

                        String uid = task.getResult().getUser().getUid();
                        task.getResult().getUser().getIdToken(true).addOnCompleteListener(task1 -> {
                            String idToken = task1.getResult().getToken();
                            sharedPref.setIdToken(idToken);
                            JsonObject body = new JsonObject();
                            JsonObject user = new JsonObject();
                            user.addProperty("country_code", sharedPref.getCountryCode());
                            user.addProperty("mobile", sharedPref.getPhoneNumber());
                            user.addProperty("uid", uid);
                            user.addProperty("id_token", idToken);
                            body.add("user", user);
                            Log.d("xoxo, new user checks", "the raw body being sent is " + body.toString());
//                            Log.d("xoxo, body", body.toString());

                            apiService.doMobileLogin(body).enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(@NonNull Call<JsonObject> call,@NonNull Response<JsonObject> response) {
                                    if (response.code() == 200) {
                                        //existing user case

                                        JsonObject responseBody = response.body();
                                        String authToken = responseBody.get("auth_token").getAsString();
                                        authToken="Bearer " + authToken;
                                        sharedPref.setAuthtoken(authToken);

                                        Toast.makeText(getApplicationContext(), "Proud to be SOLO!" , Toast.LENGTH_SHORT).show();
                                        Intent intent =new Intent(Phone2Verification.this, HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();

                                    } else if (response.code() == 401) {
                                        //new user case
                                        Toast.makeText(getApplicationContext(),"Welcome to SOLOCOIN!!", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(Phone2Verification.this, CreateProfileActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<JsonObject> call,@NonNull Throwable t) {
                                    Toast.makeText(Phone2Verification.this, "Please try again!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                        // TODO: Put the phone number information and the country code info.
                        //FirebaseUser user = task.getResult( ).getUser( );
                        // ...
                    } else {
                        if (task.getException( ) instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(Phone2Verification.this, "Invalid OTP. Please enter OTP again.",Toast.LENGTH_SHORT).show();
                            incorrect=true;
                        }
                    }
                })
                .addOnFailureListener(e -> {
//                    Toast.makeText(Phone2Verification.this, "Please try again!", Toast.LENGTH_SHORT).show();
                });
    }
}