package app.solocoin.solocoin;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import app.solocoin.solocoin.app.SharedPref;
import timber.log.Timber;

public class MarkYourLocationActivity extends FragmentActivity implements OnMapReadyCallback, OnSuccessListener<Location>, View.OnClickListener {

    @Override
    public void onSuccess(Location location) {
        if (location == null) {
            Toast.makeText(this, "Unable to access location, please try again!", Toast.LENGTH_SHORT).show();
            return;
        }

        LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            etLocation.setText(getString(R.string.current_address, city, state, country));
        } catch (IOException e) {
            Timber.d("%s %s", TAG, e.getMessage());
            etLocation.setText(getString(R.string.error_occurred));
        }

//        mMap.addMarker(new MarkerOptions().position(currentLoc));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 12));
    }

    private static String TAG = MarkYourLocationActivity.class.getSimpleName();
    private GoogleMap mMap;
    private TextInputEditText etLocation;
    private FusedLocationProviderClient fusedLocationClient;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
//                mMap.clear();
                LatLng updatedLoc = new LatLng(location.getLatitude(), location.getLongitude());
//                mMap.addMarker(new MarkerOptions().position(updatedLoc));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(updatedLoc));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(updatedLoc, 12));
            }
        }
    };;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_your_location);
        etLocation = findViewById(R.id.et_location);
        findViewById(R.id.btn_confirm).setOnClickListener(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest request = new LocationRequest()
                .setInterval(80000L)
                .setFastestInterval(50000L)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
    
    public static final String LOC_ADDED = "app.solocoin.solocoin.MarkYourLocationActivity.LOC_ADDED";
    @Override
    public void onClick(View view) {
        Toast.makeText(this, "Location added.!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,PermissionsActivity.class);
        intent.putExtra("LOC_ADDED", true);

        SharedPref.getInstance(MarkYourLocationActivity.this).setIsHomeLocationSet(true);

        startActivity(intent);
    }
}
