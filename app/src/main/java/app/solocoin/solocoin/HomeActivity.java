package app.solocoin.solocoin;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import app.solocoin.solocoin.app.SharedPref;
import app.solocoin.solocoin.receiver.GeofenceRegistrationService;
import app.solocoin.solocoin.receiver.SessionPingManager;
import app.solocoin.solocoin.util.AppPermissionChecker;
import app.solocoin.solocoin.util.GlobalFunc;

@SuppressLint("LogNotTimber")
public class HomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private GoogleApiClient googleApiClient;
    private PendingIntent pendingIntent;
    private static final int GEOFENCE_RADIUS = 50;
    private boolean isGpsDialogShown = false;

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

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, HomeFragment.newInstance()).commit();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = HomeFragment.newInstance();
            switch (item.getItemId()) {
                case R.id.home:
                    selectedFragment = HomeFragment.newInstance();
                    break;
                case R.id.wallet:
                    selectedFragment = WalletFragment.newInstance();
                    break;
                case R.id.leader_board:
                    selectedFragment = LeaderboardFragment.newInstance();
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
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(SessionPingManager.TAG, ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
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
            Log.wtf(TAG, ": server started");
        } else {
            Log.wtf(TAG, ": server already working");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();
        if (AppPermissionChecker.isLocationPermissionGranted(this)) {
            displayLocationSettingsRequest();
        } else {
            Toast.makeText(this, "Please allow Location permission in Settings", Toast.LENGTH_LONG).show();
            startActivity(new Intent(HomeActivity.this, PermissionsActivity.class));
        }
    }

    private void displayLocationSettingsRequest() {
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
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    isGpsDialogShown = true;
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(HomeActivity.this, 101);
                    } catch (IntentSender.SendIntentException e) {
                        Log.d(TAG, "PendingIntent unable to execute request.");
                    }
                    break;
            }
        });
    }

    @NonNull
    private Geofence getGeofence() {
        return new Geofence.Builder()
                .setRequestId(getString(R.string.GEOFENCE_ID))
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setCircularRegion(sharedPref.getLatitude(), sharedPref.getLongitude(), GEOFENCE_RADIUS)
                .setNotificationResponsiveness(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    private void startGeofencing() {
        Log.d(TAG, "Start geofencing monitoring call");
        pendingIntent = getGeofencePendingIntent();
        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .addGeofence(getGeofence())
                .build();

        if (!googleApiClient.isConnected()) {
            Log.d(TAG, "Google API client not connected");
        } else {
            try {
                LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent).setResultCallback(status -> {
                    if (status.isSuccess()) {
                        Log.d(TAG, "Successfully Geofencing Connected");
                    } else {
                        Log.d(TAG, "Failed to add Geofencing " + status.getStatus());
                    }
                });
            } catch (SecurityException e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    public void stopGeoFencing() {
        pendingIntent = getGeofencePendingIntent();
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, pendingIntent)
                .setResultCallback(status -> {
                    if (status.isSuccess())
                        Log.d(TAG, "Stop geofencing");
                    else
                        Log.d(TAG, "Not stop geofencing");
                });
    }

    private PendingIntent getGeofencePendingIntent() {
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceRegistrationService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 101 && resultCode != RESULT_OK || !GlobalFunc.isGpsEnabled(this)) {
            Toast.makeText(this, "We need GPS access to work, please allow!", Toast.LENGTH_LONG).show();
            displayLocationSettingsRequest();
        } else if (!isGpsDialogShown && !GlobalFunc.isGpsEnabled(this)) {
            displayLocationSettingsRequest();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startGeofencing();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.wtf(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.wtf(TAG, "connectionResult"+connectionResult.getErrorCode());
    }
}