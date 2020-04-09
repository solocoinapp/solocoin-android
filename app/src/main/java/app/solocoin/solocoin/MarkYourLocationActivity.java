package app.solocoin.solocoin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
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
import app.solocoin.solocoin.util.AppPermissionChecker;
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
        if (!AppPermissionChecker.isLocationPermissionGranted(this)){
            requestPermission();
            return;
        }
        LocationRequest request = new LocationRequest()
                .setInterval(80000L)
                .setFastestInterval(50000L)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setUpMap(){
        if (!AppPermissionChecker.isLocationPermissionGranted(this)){
            requestPermission();
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onClick(View view) {
        if (!AppPermissionChecker.isLocationPermissionGranted(this)){
            Toast.makeText(this,"We can't help you in social distancing without your location. Don't worry, it's safe with us!",Toast.LENGTH_SHORT).show();
            requestPermission();
            //return;
            startActivity(new Intent(this,MarkYourLocationActivity.class));
        }
        Toast.makeText(this, "Location added.!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, PermissionsActivity.class);
        intent.putExtra("LOC_ADDED", true);
        SharedPref.getInstance(MarkYourLocationActivity.this).setIsHomeLocationSet(true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AppPermissionChecker.isLocationPermissionGranted(this)){
            Toast.makeText(this,"Thanks for allowing permission, you can continue...",Toast.LENGTH_SHORT).show();
            //changeLocationBtnColor();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {
                        Toast.makeText(this, "Please allow us to access location, for working efficienlty", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this,"Thanks for allowing permission, you can continue...",Toast.LENGTH_SHORT).show();
                    //changeLocationBtnColor();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
