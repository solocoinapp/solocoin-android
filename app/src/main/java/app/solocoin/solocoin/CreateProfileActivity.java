package app.solocoin.solocoin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;

import app.solocoin.solocoin.api.APIClient;
import app.solocoin.solocoin.api.APIService;
import app.solocoin.solocoin.app.SharedPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("LogNotTimber")
public class CreateProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPref sharedPref;
    private APIService apiService;

    private TextInputEditText etUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        sharedPref = SharedPref.getInstance(this);
        apiService = APIClient.getRetrofitInstance(this).create(APIService.class);

        etUsername = findViewById(R.id.et_username);
        findViewById(R.id.btn_continue).setOnClickListener(this);
    }

    private void createProfile(String username) {
        JsonObject body = new JsonObject();
        JsonObject user = new JsonObject();
        user.addProperty("name", username);
        user.addProperty("country_code", sharedPref.getCountryCode());
        user.addProperty("mobile", sharedPref.getPhoneNumber());
        user.addProperty("uid", FirebaseAuth.getInstance().getUid());
        user.addProperty("id_token", sharedPref.getIdToken());
        body.add("user", user);

        apiService.doMobileSignup(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                Log.d("xoxo", response.code()+"/"+response.body());
                if (response.code() == 200){
                    JsonObject body = response.body();
                    String authToken = body.get("auth_token").getAsString();
                    authToken = "Bearer " + authToken;
                    sharedPref.setAuthToken(authToken);

                    onCreateProfileSuccess();
                } else {
                    Toast.makeText(CreateProfileActivity.this, "Please try again!!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call,@NonNull Throwable t) {
                Toast.makeText(CreateProfileActivity.this, "Please try again!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onCreateProfileSuccess() {
        Toast.makeText(CreateProfileActivity.this, "Profile creation, successful!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MarkYourLocationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        try {
            String username = etUsername.getText().toString();
            if (username.length() != 0) {
                createProfile(username);
            } else {
                Toast.makeText(CreateProfileActivity.this, "Username can't be empty!!!", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException ex) {
            Toast.makeText(CreateProfileActivity.this, "Please try again!!!", Toast.LENGTH_SHORT).show();
        }
    }
}
