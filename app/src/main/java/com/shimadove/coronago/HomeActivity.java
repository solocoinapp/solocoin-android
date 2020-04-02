package com.shimadove.coronago;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private GeofencingClient geofencingClient;
    private List<Geofence> geofencesList;
    PendingIntent geofencePendingIntent;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            // TODO: Implement this as I don't really know what to do.
        }

        @Override
        public void onProviderEnabled(String s) {
            // TODO: Implement this as I don't really know what to do.
        }

        @Override
        public void onProviderDisabled(String s) {
            // TODO: Implement this as I don't really know what to do.
        }

        @Override
        public void onLocationChanged(Location location) {
            long timeout = System.currentTimeMillis() + 1000*60*60*24*7;

            geofencingClient = LocationServices.getGeofencingClient(HomeActivity.this);
            geofencesList = new ArrayList<Geofence>();
            geofencesList.add(new Geofence.Builder()
                    .setRequestId("GEOFENCE")
                    .setCircularRegion(
                        location.getLatitude(), location.getLongitude(), 100
                    )
                    .setExpirationDuration(timeout)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());

            try{
                if(sharedPreferences.getInt("timeout", 0) > System.currentTimeMillis() + 1000*60*60*24*2){
                    geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent()); // Add OnSuccessListener
                    editor.putLong("timeout", timeout);
                    editor.apply();
                }
            } catch (SecurityException e){
                Toast.makeText(HomeActivity.this, "Please close and reopen the app while enabling the Location permission", Toast.LENGTH_SHORT);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getApplication().getSharedPreferences("information", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try{
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        } catch (SecurityException e){
            Toast.makeText(this, "Error - please allow Location permission in Settings", Toast.LENGTH_LONG);
            finish();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()){
                    case R.id.home:
                        selectedFragment = HomeFragment.newInstance();
                        break;
                    case R.id.wallet:
                        //todo:: inflate wallet fragment
                        selectedFragment = WalletFragment.newInstance("","");
                        break;
                    case R.id.leader_board:
                        //todo :: inflate leaderboard fragment
                        selectedFragment = LeaderboardFragment.newInstance("","");
                        break;
                    case R.id.profile:
                        selectedFragment = ProfileFragment.newInstance();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, selectedFragment).commit();
                return true;
            }

        });
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
