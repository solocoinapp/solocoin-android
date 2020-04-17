package app.solocoin.solocoin;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bigbangbutton.editcodeview.EditCodeView;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import app.solocoin.solocoin.api.APIClient;
import app.solocoin.solocoin.api.APIService;
import app.solocoin.solocoin.app.SharedPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("LogNotTimber")
public class Phone2Verification extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = Phone2Verification.class.getSimpleName();

    private String phoneNumber = "";
    private String verificationId = "";
    private PhoneAuthProvider.ForceResendingToken forceResendToken = null;

    private EditCodeView etOtp;

    private SharedPref sharedPref;
    private APIService apiService;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_phone2_verification);

        apiService = APIClient.getRetrofitInstance(this).create(APIService.class);
        sharedPref = SharedPref.getInstance(this);

        findViewById(R.id.tv_resend_otp).setOnClickListener(this);
        findViewById(R.id.btn_verify_otp).setOnClickListener(this);

        progressBar = findViewById(R.id.progress_bar);

        EditText etPhoneNumber = findViewById(R.id.et_phone_number);
        phoneNumber = getIntent().getStringExtra("PHONE_NO");
        etPhoneNumber.setText(phoneNumber);
        sendVerificationCodeToUser(phoneNumber, forceResendToken);

        etOtp = findViewById(R.id.et_otp);
        etOtp.requestFocus();

        setupTNC();
    }

    private void setupTNC() {
        TextView tvTnc = findViewById(R.id.tv_tnc);
        String terms = tvTnc.getText().toString();
        SpannableString ss = new SpannableString(terms);
        int termstart = terms.indexOf("Terms of Service");
        int pristart = terms.indexOf("Privacy Policy");
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
        tvTnc.setText(ss);
        tvTnc.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void sendVerificationCodeToUser(String phoneNo, PhoneAuthProvider.ForceResendingToken token) {
        if (token == null) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNo,120, TimeUnit.SECONDS, this, phoneAuthCallback);
        } else {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNo,120, TimeUnit.SECONDS, this, phoneAuthCallback, token);
        }
        progressBar.setVisibility(View.VISIBLE);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String vid, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            verificationId = vid;
            forceResendToken = token;
            progressBar.setVisibility(View.GONE);
            Toast.makeText(Phone2Verification.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Crashlytics.logException(e);
            Toast.makeText(Phone2Verification.this, "Some error occurred, please try again!!!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            Crashlytics.log(0, TAG, "onCodeAutoRetrievalTimeOut: " + s);
            Toast.makeText(Phone2Verification.this,"OTP time out has occurred. Please request for OTP again.",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    };

    private void verifyCode(String verificationId, String codeSent) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, codeSent);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            sharedPref.clearSession();
            FirebaseAuth.getInstance().signOut();
        }

        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener( Phone2Verification.this, task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                        sharedPref.setSessionType("home");

                        String uid = task.getResult().getUser().getUid();
                        if (uid != null) {
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

                                apiService.doMobileLogin(body).enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(@NonNull Call<JsonObject> call,@NonNull Response<JsonObject> response) {
                                        Crashlytics.log(0, TAG, "response: " + response.code());
                                        Log.d(TAG, "reponse: " + response.body() + " / "+ response.code());
                                        progressBar.setVisibility(View.GONE);
                                        if (response.code() == 200) {
                                            //existing user case

                                            JsonObject responseBody = response.body();
                                            String authToken = responseBody.get("auth_token").getAsString();
                                            authToken = "Bearer " + authToken;
                                            sharedPref.setAuthToken(authToken);
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
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<JsonObject> call,@NonNull Throwable t) {
                                        Crashlytics.logException(t);
                                        Log.d(TAG, "failure: " + t.getMessage());
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Crashlytics.logException(e);
                    Log.d(TAG, "firebase-failure: " + e.toString());
                    progressBar.setVisibility(View.GONE);
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(getApplicationContext(), "Please resend OTP again!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "Something went wrong, please try again!!!", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_resend_otp) {
            sendVerificationCodeToUser(phoneNumber, forceResendToken);
        } else if (view.getId() == R.id.btn_verify_otp) {
            if (!verificationId.equals("") && etOtp.getCode().length() == 6) {
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(verificationId, etOtp.getCode());
            } else {
                Toast.makeText(this, "Please check otp again!!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
