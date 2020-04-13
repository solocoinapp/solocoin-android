package app.solocoin.solocoin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import app.solocoin.solocoin.api.APIClient;
import app.solocoin.solocoin.api.APIService;
import app.solocoin.solocoin.app.SharedPref;
import app.solocoin.solocoin.util.AppPermissionChecker;
import app.solocoin.solocoin.util.GlobalFunc;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

@SuppressLint("LogNotTimber")
public class MarkYourLocationActivity extends FragmentActivity implements OnSuccessListener<Location>, View.OnClickListener {

    @Override
    public void onSuccess(Location location) {
        if (location == null) {
            Toast.makeText(this, "Unable to access location, please try again!", Toast.LENGTH_SHORT).show();
            return;
        }

        LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
        sharedPref.setLatitude((float) location.getLatitude());
        sharedPref.setLongitude((float) location.getLongitude());

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
            etLocation.setText(getString(R.string.error_occurred));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 12));
    }

    private static String TAG = MarkYourLocationActivity.class.getSimpleName();
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private TextInputEditText etLocation;

    private FusedLocationProviderClient fusedLocationClient;
    private SharedPref sharedPref;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                LatLng updatedLoc = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(updatedLoc));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(updatedLoc, 12));
            }
        }
    };

    private OnMapReadyCallback mOnMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (!AppPermissionChecker.isLocationPermissionGranted(MarkYourLocationActivity.this)){
                requestPermission();
            } else {
                mMap = googleMap;
                mMap.setMyLocationEnabled(true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_your_location);
        sharedPref = SharedPref.getInstance(this);
        etLocation = findViewById(R.id.et_location);
        findViewById(R.id.btn_confirm).setOnClickListener(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(mOnMapReadyCallback);
        }
    }

    @Override
    public void onClick(View view) {
        if (!AppPermissionChecker.isLocationPermissionGranted(this)){
            requestPermission();
            return;
        }
        Toast.makeText(this, "Location added, please wait updating...", Toast.LENGTH_SHORT).show();

        APIService service = APIClient.getRetrofitInstance(this).create(APIService.class);
        JsonObject user = new JsonObject();
        user.addProperty("mobile", sharedPref.getPhoneNumber());
        user.addProperty("lat", sharedPref.getLatitude());
        user.addProperty("lng", sharedPref.getLongitude());

        Call<JsonObject> call = service.doUserUpdate(sharedPref.getAuthToken(), user);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NotNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                Intent intent = new Intent(MarkYourLocationActivity.this, PermissionsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call,@NonNull Throwable t) {
                Toast.makeText(MarkYourLocationActivity.this, "Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppPermissionChecker.isLocationPermissionGranted(this)) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            LocationRequest request = new LocationRequest()
                    .setInterval(600000)
                    .setFastestInterval(30000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, this);

            mapFragment.getMapAsync(mOnMapReadyCallback);
        } else {
            Toast.makeText(this, "Please allow Location permission in Settings", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, PermissionsActivity.class));
        }
    }

    private void requestPermission() {
        Toast.makeText(this,"We can't help you in social distancing without your location. Don't worry, it's safe with us!",Toast.LENGTH_SHORT).show();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            String permission = permissions[0];
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                boolean showRationale = shouldShowRequestPermissionRationale(permission);
                if (!showRationale) {
                    Toast.makeText(this, "Please allow us to access location, for working effiecienlty", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this,"Thanks for allowing permission, please wait getting location...",Toast.LENGTH_SHORT).show();
                mapFragment.getMapAsync(mOnMapReadyCallback);
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, this);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
