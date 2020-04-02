package com.shimadove.coronago;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.shimadove.coronago.api.APIClient;
import com.shimadove.coronago.api.APIService;
import com.shimadove.coronago.app.SharedPref;
import com.shimadove.coronago.databinding.ActivityCreateProfileBinding;
import com.shimadove.coronago.viewmodel.CreateProfileViewModel;

import org.apache.commons.lang3.RandomStringUtils;

public class CreateProfileActivity extends AppCompatActivity implements CreateProfileViewModel.CreateProfileInterface {

    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor preferencesEditor;
    public final String PREFERENCES_FILE = "information";
    private SharedPref sharedPref;
    private String phoneNumber, firebaseUid, countryCode;

    ActivityCreateProfileBinding binding;
    CreateProfileViewModel viewModel;

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

        sharedPreferences = getApplication().getSharedPreferences("information", Context.MODE_PRIVATE);
        preferencesEditor = sharedPreferences.edit();
        sharedPref = SharedPref.getInstance(this);

//        Intent intent = getIntent();
//        phoneNumber = intent.getStringExtra("phoneNumber");

        phoneNumber = sharedPreferences.getString("phone_number", null);
        countryCode = sharedPreferences.getString("country_code", null);
        // TODO: do something special if phoneNumber or country code is null.

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
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Would a 404 land here? - No since 404 is still a value returned.
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUid = currentUser.getUid();
    }

    private void createProfile(String username, String phoneNumber, String uid){
        JsonObject body = new JsonObject();
        body.addProperty("mobile", phoneNumber);
        body.addProperty("uid", uid);
        body.addProperty("country_code", countryCode);
        body.addProperty("name", username);

        apiService.doMobileSignup(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Toast.makeText(CreateProfileActivity.this, "Successful profile creation", Toast.LENGTH_SHORT).show();
                onCreateProfileSuccess();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(CreateProfileActivity.this, "Failure in profile creation", Toast.LENGTH_SHORT).show();
                onCreateProfileFailed();
            }
        });
    }

    @Override
    public void onCreateProfileSuccess() {
         startActivity(new Intent(CreateProfileActivity.this,Welcome.class));
    }

    @Override
    public void onCreateProfileFailed() {

    }

    @Override
    public void onContinueClicked() {
        preferencesEditor.putString("email", viewModel.email);
        preferencesEditor.apply();

        // create profile for server.
        createProfile(viewModel.username, phoneNumber, firebaseUid);
        startActivity(new Intent(CreateProfileActivity.this,Welcome.class));
    }

    @Override
    public void onMaleClicked() {
        preferencesEditor.putString("gender", "M");
        preferencesEditor.apply();
    }

    @Override
    public void onFemaleClicked() {
        preferencesEditor.putString("gender", "F");
        preferencesEditor.apply();
    }

    @Override
    public void onSkip() {
        String username = RandomStringUtils.randomAlphanumeric(20).toUpperCase();
        createProfile(username, phoneNumber, firebaseUid);
        startActivity(new Intent(CreateProfileActivity.this,Welcome.class));
    }
}
