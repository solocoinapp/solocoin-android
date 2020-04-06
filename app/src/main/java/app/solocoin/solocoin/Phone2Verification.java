package app.solocoin.solocoin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

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
    //private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String otpBySystem;
    private EditText mPhoneNumberField;
    private EditText mVerificationField;
    TextView mCountyList;
    private ProgressBar progressBar;
    String phoneNo;
    String OtpEnteredByUser;
    private String countryCode, enteredCode;
    private Button mStartButton;
    private boolean codeSent = false;
    private ImageView imageBanner;
    private Button verifyBtn;
    private FirebaseAuth mAuth;
    ActivityPhone2VerificationBinding binding;
    EditText phno, otpEnter;
    TextView enterNum, sendmsg;
    TextView resend;
    TextView tandc;
    boolean timeout=false;
    boolean incorrect=false;
    String verificationID;
    PhoneAuthProvider.ForceResendingToken token;
    private SharedPref sharedPref;
    APIService apiService;
    String firebaseUid;

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
        final EditCodeView editCodeView = (EditCodeView) findViewById(R.id.edit_code);
        String s;
        editCodeView.setEditCodeListener(new EditCodeListener() {
            @Override
            public void onCodeReady(String code) {
                //This function gives the complete number inputted
                OtpEnteredByUser=code;
            }
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
                Log.d(TAG,"the otp typed by user: " + code);
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
                 ""+phoneNo,        // Phone number to verify
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
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpBySystem, otpByUser);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth= FirebaseAuth.getInstance();
        mAuth.signInWithCredential( credential )
                .addOnCompleteListener( Phone2Verification.this, new OnCompleteListener<AuthResult>( ) {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful( )) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.d("signInWithCredential:success");
                            //TODO - make isNewUser == false in if statement

                            if(isNewUser()==false){
                                Toast.makeText(getApplicationContext(), "the current user is an exisiting one. \n" , Toast.LENGTH_SHORT).show();
                                Intent intent =new Intent(getApplicationContext(),HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(),"This is a new user. \n", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(getApplicationContext(),CreateProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            // TODO: Put the phone number information and the country code info.
                            //FirebaseUser user = task.getResult( ).getUser( );
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(Phone2Verification.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            //Log.w( TAG, "signInWithCredential:failure", task.getException( ) );

                            if (task.getException( ) instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(Phone2Verification.this, "Invalid OTP. Please enter OTP again.",Toast.LENGTH_SHORT).show();
                                incorrect=true;
                            }
                        }
                    }
                } );
    }

    String id_token;
    boolean isnewuser;
    private boolean isNewUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        firebaseUid = currentUser.getUid();
        currentUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()){
                            id_token= task.getResult().getToken();
                            //String authtoken = "Bearer " + id_token;
                            sharedPref.setIdToken(id_token);
                            Timber.d("the firebase id token is: "+id_token + "\n");
                        }
                        else{
                            Timber.d("there is an issue with the firebase id token.");
                        }
                    }
                });
        String mobile = sharedPref.getPhoneNumber();
        String cc = sharedPref.getCountryCode();
        String id_token = sharedPref.getIdToken();
        UserLogin userLogin = new UserLogin(id_token,firebaseUid,mobile,cc);
        PostUserLogin postUserLogin=new PostUserLogin(userLogin);
        Gson gson =new Gson();
        String json= gson.toJson(postUserLogin);
        Timber.d(json);
        JsonObject body =new JsonObject();
        JsonElement element = gson.toJsonTree(postUserLogin);
        body=element.getAsJsonObject();
        apiService.doMobileLogin(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    JsonObject userresponse = response.body();
                    String authtoken = userresponse.get("auth_token").getAsString();
                    authtoken = "Bearer " + authtoken;
                    sharedPref.setAuthtoken(authtoken);
                    Timber.d("auth_token is: " + authtoken);
                    isnewuser = false; //not a new user
                } else {
                    Timber.d("This is a new user. Response code is: " + response.code()+"\n");
                    isnewuser = true;
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                isnewuser=true;
            }
        });
        Timber.d("The user is a new user: " + String.valueOf(isnewuser));
        return isnewuser;
    }

}