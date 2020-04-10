package app.solocoin.solocoin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import app.solocoin.solocoin.app.SharedPref;
import app.solocoin.solocoin.receiver.SessionPingManager;
import app.solocoin.solocoin.util.AppPermissionChecker;

@SuppressLint("LogNotTimber")
public class HomeActivity extends AppCompatActivity {

    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPref = SharedPref.getInstance(this);

        if (sharedPref.getAuthToken() == null) {
            Intent intent = new Intent(HomeActivity.this, OnboardingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }
        startSessionPingManager();

        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, HomeFragment.newInstance()).commit();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = HomeFragment.newInstance();
            switch (item.getItemId()) {
                case R.id.home:
                    selectedFragment = HomeFragment.newInstance();
                    break;
                case R.id.wallet:
                    selectedFragment = WalletFragment.newInstance("", "");
                    break;
                case R.id.leader_board:
                    selectedFragment = LeaderboardFragment.newInstance("", "");
                    break;
                case R.id.profile:
                    selectedFragment = ProfileFragment.newInstance();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.main_content, selectedFragment).commit();
            return true;
        });
    }

    private void createWorkRequest() {
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder
                (SessionPingManager.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(SessionPingManager.TAG, ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
    }

    private WorkInfo.State getStateOfWork() {
        try {
            if (WorkManager.getInstance(this).getWorkInfosForUniqueWork(SessionPingManager.TAG).get().size() > 0) {
                return WorkManager.getInstance(this).getWorkInfosForUniqueWork(SessionPingManager.TAG).get().get(0).getState();
            } else {
                return WorkInfo.State.CANCELLED;
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return WorkInfo.State.CANCELLED;
        }
    }

    private void startSessionPingManager() {
        if (getStateOfWork() != WorkInfo.State.ENQUEUED && getStateOfWork() != WorkInfo.State.RUNNING) {
            createWorkRequest();
            Log.wtf("xolo", ": server started");
        } else {
            Log.wtf("xolo", ": server already working");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppPermissionChecker.isLocationPermissionGranted(this)) {
            displayLocationSettingsRequest(this);
        } else {
            Toast.makeText(this, "Please allow Location permission in Settings", Toast.LENGTH_LONG).show();
            startActivity(new Intent(HomeActivity.this, PermissionsActivity.class));
        }
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(30000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(HomeActivity.this, 101);
                    } catch (IntentSender.SendIntentException e) {
                        Log.d("xolo", "PendingIntent unable to execute request.");
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    break;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.wtf("xolo", requestCode+"/"+resultCode);
        if (requestCode == 101 && resultCode != RESULT_OK) {
            Toast.makeText(this, "We need GPS access to work, please allow!", Toast.LENGTH_LONG).show();
            displayLocationSettingsRequest(this);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}