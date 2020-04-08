package app.solocoin.solocoin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import app.solocoin.solocoin.app.SharedPref;
import app.solocoin.solocoin.receiver.GeofenceBroadcastReceiver;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

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

        if (sharedPref.getAuthtoken() == null) {
            Timber.d("The user has an issue.");
            Intent intent = new Intent(HomeActivity.this, OnboardingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        if (!(sharedPref.getIsHomeLocationSet())) {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (lm != null) {
                try {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
                } catch (SecurityException ex) {
                    Toast.makeText(this, "Error - please allow Location permission in Settings", Toast.LENGTH_LONG).show();
                    //finish();
                    startActivity(new Intent(HomeActivity.this, PermissionsActivity.class));
                }
            }
        } else {
            reinstateGeofence(sharedPref.getLatitude(), sharedPref.getLongitude());
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
                    //todo:: inflate wallet fragment
                    selectedFragment = WalletFragment.newInstance("", "");
                    break;
                case R.id.leader_board:
                    //todo :: inflate leaderboard fragment
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

    private void reinstateGeofence(float latitude, float longitude) {
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
                        latitude, longitude, 100
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