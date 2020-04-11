package app.solocoin.solocoin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import app.solocoin.solocoin.app.SharedPref;
import app.solocoin.solocoin.receiver.GeofenceBroadcastReceiver;
import app.solocoin.solocoin.receiver.SessionPingManager;
import app.solocoin.solocoin.util.AppPermissionChecker;

@SuppressLint("LogNotTimber")
public class HomeActivity extends AppCompatActivity {

    private GeofencingClient geofencingClient;
    private List<Geofence> geofencesList;
    PendingIntent geofencePendingIntent;

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }

        @Override
        public void onLocationChanged(Location location) {
            if (!sharedPref.getIsHomeLocationSet()) {
                sharedPref.setIsHomeLocationSet(true);
                sharedPref.setLatitude((float) location.getLatitude());
                sharedPref.setLongitude((float) location.getLongitude());
                reinstateGeofence((float) location.getLatitude(), (float) location.getLongitude());
            }
        }
    };

    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPref = SharedPref.getInstance(this);

        sharedPref.setReceiverOn(true);

        if (sharedPref.getAuthtoken() == null) {
            Intent intent = new Intent(HomeActivity.this, OnboardingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

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
            if (displayLocationSettingsRequest(this)) {
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (lm != null) {
                    try {
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
                    } catch (SecurityException ex) {
                        Toast.makeText(this, "Please allow Location permission in Settings", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(HomeActivity.this, PermissionsActivity.class));
                    }
                }
            }
            reinstateGeofence(sharedPref.getLatitude(), sharedPref.getLongitude());
        }

        if (sharedPref.getAuthtoken() != null) {
            startSessionPingManager();
        }
    }

    private boolean displayLocationSettingsRequest(Context context) {
        final boolean[] mResult = {false};
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    mResult[0] = true;
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(HomeActivity.this, 101);
                    } catch (IntentSender.SendIntentException e) {
                        Log.d("xoxo", "PendingIntent unable to execute request.");
                    }

                    mResult[0] = false;
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    mResult[0] = false;
                    break;
            }
        });

        return mResult[0];
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 101 && resultCode == 0) {
            Toast.makeText(this, "We need GPS access to work, please allow!", Toast.LENGTH_LONG).show();
            if (displayLocationSettingsRequest(this)) {
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (lm != null) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void reinstateGeofence(float latitude, float longitude) {
        geofencingClient=LocationServices.getGeofencingClient(this);
        geofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setGeofence(latitude, longitude);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setGeofence(latitude, longitude);
                    }
                });
    }

    private void setGeofence(float latitude, float longitude) {
        long timeout = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7;

        geofencingClient = LocationServices.getGeofencingClient(HomeActivity.this);
        geofencesList = new ArrayList<Geofence>();
        geofencesList.add(new Geofence.Builder()
                .setRequestId("GEOFENCE")
                .setCircularRegion(
                        latitude, longitude, 20
                )
                .setExpirationDuration(timeout)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        try {
            geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent());
            sharedPref.setTimeout(timeout);
        } catch (SecurityException e) {
            Toast.makeText(HomeActivity.this, "Please close and reopen the app while enabling the Location permission", Toast.LENGTH_SHORT).show();
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofencesList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }
}