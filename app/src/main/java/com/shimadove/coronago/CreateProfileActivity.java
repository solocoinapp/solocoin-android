package com.shimadove.coronago;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shimadove.coronago.api.APIClient;
import com.shimadove.coronago.api.APIService;
import com.shimadove.coronago.api.PostUser;
import com.shimadove.coronago.api.RetrofitListener;
import com.shimadove.coronago.api.UserSignUp;
import com.shimadove.coronago.app.SharedPref;
import com.shimadove.coronago.databinding.ActivityCreateProfileBinding;
import com.shimadove.coronago.ui.home.HomeActivity;
import com.shimadove.coronago.viewmodel.CreateProfileViewModel;

import java.util.concurrent.TimeUnit;

public class CreateProfileActivity extends AppCompatActivity implements CreateProfileViewModel.CreateProfileInterface {

    //public SharedPreferences sharedPreferences;
    //public SharedPreferences.Editor preferencesEditor;
    private SharedPref sharedPref;
    public final String PREFERENCES_FILE = "information";
    private String phoneNumber, firebaseUid, countryCode, username;
    private String id_token;
    ActivityCreateProfileBinding binding;
    CreateProfileViewModel viewModel;
    private RetrofitListener retrofitListener;
    //private UserSignUp userSignUp;

    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        apiService = APIClient.getRetrofitInstance(this).create(APIService.class);

         binding = DataBindingUtil.setContentView(this,R.layout.activity_create_profile);
         viewModel = new ViewModelProvider(this).get(CreateProfileViewModel.class);
         binding.getRoot();
         binding.setCreateProfileViewModel(viewModel);
         viewModel.setCreateProfileInterface(this);
        retrofitListener=new RetrofitListener();
        sharedPref = SharedPref.getInstance(this);

//        Intent intent = getIntent();
//        phoneNumber = intent.getStringExtra("phoneNumber");
        username = sharedPref.getUsername();
        phoneNumber = sharedPref.getPhoneNumber();
        countryCode = sharedPref.getCountryCode();
        // TODO: do something special if phoneNumber or country code is null.
        binding.usernameField.setText(sharedPref.getUsername());
         // Check for User already created
        JsonObject mobileLoginBody = new JsonObject();
        mobileLoginBody.addProperty("mobile", phoneNumber);
        apiService.doMobileLogin(mobileLoginBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                sharedPref.setHttpResponse(response.code());
                if(response.code() == 200){
                    Intent intent1 = new Intent(CreateProfileActivity.this, HomeActivity.class);
                    startActivity(intent1);
                }
                else{
                    Toast.makeText(CreateProfileActivity.this,"Existing user check fail.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Would a 404 land here? - No since 404 is still a value returned.
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUid = currentUser.getUid();
        currentUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()){
                            id_token= task.getResult().getToken();
                            String authtoken = "Bearer " + id_token;
                            //sharedPref.setAuthtoken(authtoken);
                            Timber.d("the firebase id token is: "+id_token + "\n" + "the auth_token passed is: " + authtoken + "\n");
                        }
                        else{
                            Timber.d("there is an issue with the firebase id token.");
                        }
                    }
                });
    }

    private void createProfile(String username, String phoneNumber, String uid){
        JsonObject body = new JsonObject();
        UserSignUp user = new UserSignUp(id_token,uid,username,phoneNumber,countryCode);

        PostUser postUser = new PostUser(user);
        Gson gson =new Gson();
        String json= gson.toJson(postUser);
        Timber.d(json);

        JsonElement element = gson.toJsonTree(postUser);
        body=element.getAsJsonObject();
        apiService.doMobileSignup(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code()==200){
                    //Toast.makeText(CreateProfileActivity.this,"No issue at backend.",Toast.LENGTH_SHORT).show();
                    //Timber.d("No issue at backend.");
                    JsonObject userresponse = response.body();
                    String authtoken = userresponse.get("auth_token").getAsString();
                    authtoken = "Bearer " + authtoken;
                    sharedPref.setAuthtoken(authtoken);
                    Timber.d("auth_token is: " + authtoken);
                    retrofitListener.onSuccess(response.code());
                }
                else if (response.code()==400){
                    String errormsg=response.errorBody().toString();
                    Timber.d("Unable to create profile: " + errormsg);
                    retrofitListener.onFailure(response.code());
                }
                else{
                    //Timber.d("Issue at backend");
                    String errormsg = response.errorBody().toString();
                    Timber.d("username is incorrect: " + errormsg);
                    retrofitListener.onFailure(response.code());
                }
                onCreateProfileSuccess();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //Log.d(TAG,"Some error occured.");
                Timber.d(t, "Error.");
                //retrofitListener.onFailure();
                onCreateProfileFailed();
            }
        });
    }

    @Override
    public void onCreateProfileSuccess() {
        Toast.makeText(CreateProfileActivity.this, "Successful profile creation", Toast.LENGTH_SHORT).show();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        final PeriodicWorkRequest sendSession = new PeriodicWorkRequest
                .Builder(SessionWorker.class,15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance().enqueue(sendSession);
        startActivity(new Intent(CreateProfileActivity.this,Welcome.class));
    }

    @Override
    public void onCreateProfileFailed() {
        Toast.makeText(CreateProfileActivity.this, "Failure in profile creation", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onContinueClicked() {
        //sharedPref.setEmail("email");
        //Toast.makeText(CreateProfileActivity.this, "Please enter a username.", Toast.LENGTH_SHORT).show();
        // create profile for server.
        TextView name = findViewById(R.id.usernameField);
        sharedPref.setUsername(name.getText().toString());
        viewModel.username= name.getText().toString();
        createProfile(viewModel.username, phoneNumber, firebaseUid);
        //startActivity(new Intent(CreateProfileActivity.this,Welcome.class));
    }


//    @Override
//    public void onMaleClicked() {
//        //sharedPref.setGender("M");
//    }

//    @Override
//    public void onFemaleClicked() {
//        //sharedPref.setGender("F");
//    }

    //As no skip button for now
//    @Override
//    public void onSkip() {
//        //String username = RandomStringUtils.randomAlphanumeric(20).toUpperCase();
//        //createProfile(username, phoneNumber, firebaseUid);
//        //startActivity(new Intent(CreateProfileActivity.this,Welcome.class));
//    }
}
