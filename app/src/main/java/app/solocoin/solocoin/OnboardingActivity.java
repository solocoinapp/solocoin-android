package app.solocoin.solocoin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnSuccessListener;

import app.solocoin.solocoin.app.SharedPref;
import app.solocoin.solocoin.databinding.ActivityMainBinding;

import java.util.ArrayList;

import static android.text.format.DateUtils.HOUR_IN_MILLIS;
import static android.text.format.DateUtils.MINUTE_IN_MILLIS;

public class OnboardingActivity extends AppCompatActivity {


    ActivityMainBinding binding;
    public static final int MULTIPLE_PERMISSION_REQUEST = 102;

    //Onboarding
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private ViewPager onboard_pager;
    private OnBoard_Adapter mAdapter;
    ArrayList<OnBoardItem> onBoardItems=new ArrayList<>();
    // distance from last known location in meters
    private static final float DISPLACEMENT = 1000;
    private static final int REQUEST_CHECK_SETTINGS = 3455;
    private final long FASTEST_INTERVAL = MINUTE_IN_MILLIS * 2;
    private final long INTERVAL = MINUTE_IN_MILLIS * 30;
    private FusedLocationProviderClient mFusedLocationClient;
    private final long FORTY_EIGHT_HOURS = HOUR_IN_MILLIS * 48;
    private static final int PERMISSION_REQUEST_CODE = 277;
    private static final String TAG = OnboardingActivity.class.getSimpleName();

    private SharedPref sharedPref;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_on_boarding);
        //check if the user is logging for the first time.
        /*initView();*/
        sharedPref = SharedPref.getInstance(this);

        ask_permissions();

        //Buttons
        final Button abutton = findViewById(R.id.button0);
        abutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(OnboardingActivity.this, Phone1Verification.class));
            }
        });

        /*final Button  bbutton= findViewById(R.id.skipbuttonO);
        bbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Phone1Verification.class));
            }
        });*/

        //Onboarding
        onboard_pager = (ViewPager) findViewById(R.id.pager_introduction);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);

        loadData();

        mAdapter = new OnBoard_Adapter(this,onBoardItems);
        onboard_pager.setAdapter(mAdapter);
        onboard_pager.setCurrentItem(0);
        onboard_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                // Change the current position intimation

                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(OnboardingActivity.this, R.drawable.non_selected_item_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(OnboardingActivity.this, R.drawable.selected_item_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setUiPageViewController();

    }

// Load data into the viewpager

    public void loadData()
    {

        int[] header = {R.string.ob_header1, R.string.ob_header2, R.string.ob_header3};
        int[] imageId = {R.mipmap.intro1, R.mipmap.intro2, R.mipmap.intro3};

        for(int i=0;i<imageId.length;i++)
        {
            OnBoardItem item=new OnBoardItem();
            item.setImageID(imageId[i]);
            item.setTitle(getResources().getString(header[i]));

            onBoardItems.add(item);
        }
    }



    // setup the
    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(OnboardingActivity.this, R.drawable.non_selected_item_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(6, 0, 6, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(OnboardingActivity.this, R.drawable.selected_item_dot));
    }


    /*private void initView() {


        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Phone1Verification.class));
            }
        });
        /*binding.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
            }
        });
        binding.button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Main3Activity.class));
            }
        });
        binding.skipbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Phone1Verification.class));
            }
        });
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,
                        "GPS(Location) permissions accepted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                      "Please accept and enable GPS(Location) permissions ", Toast.LENGTH_LONG).show();
            }
        }
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == MULTIPLE_PERMISSION_REQUEST) {
//            if (grantResults.length > 0) {
//               //todo if no location permission is given
//            }
//        }
//    }

    private void ask_permissions() {
        if (

                ActivityCompat.checkSelfPermission(OnboardingActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(OnboardingActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(OnboardingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(OnboardingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED

        ) {

            ActivityCompat.requestPermissions(OnboardingActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS
                            , Manifest.permission.READ_PHONE_STATE
                            , Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.ACCESS_FINE_LOCATION
                    }, 1);


        }
        intilializeLocationPermission();

    }

    private void intilializeLocationPermission() {
        if (!isLocationPermissionGranted())
            requestLocationPermission();
        isGPSEnabled();
    }

    private void isGPSEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            displayLocationSettingsRequest(this);
        } else {
            initializeCaptureUserLocation();
        }
    }
    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(OnboardingActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        buildAlertMessageNoGps();
                        break;
                }
            }
        });
    }
    /**
     * Incase the phone does not permit activating GPS from the application.
     * The User will be redirected to the her phone Settings application
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please enable your GPS to use the Locate Merchant functionality")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }
    /**
     * This captures the Merchant location in case a period of 48hrs elapses
     * without her using the application
     * Apart from the onLocationChangeListener in the WebSocketService,
     * this runs on the first time a merchant opens the application or returns after 48hrs
     */
    private void initializeCaptureUserLocation() {
        long lastSync = sharedPref.getLastSync();
        if (lastSync == 0 || System.currentTimeMillis() - lastSync >= FORTY_EIGHT_HOURS) {
            sharedPref.saveLastSync(System.currentTimeMillis());
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            Log.d(TAG, "onCreate: BUILD VERSION" + Build.VERSION.SDK_INT);

            try {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (isLocationPermissionGranted()) {
                        getUserLocation();
                        Log.d("permission", "Permission already granted");
                    } else {
                        requestLocationPermission();
                        Log.d("permission", "Request permission");
                    }
                } else {
                    getUserLocation();
                }
            } catch (SecurityException e) {
                Log.e(TAG, "onCreate: ", e);
            }
        }
    }
    private void getUserLocation() throws SecurityException {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.d(TAG, "onSuccess: Got location");
                            Log.d(TAG, "LAT:" + location.getLatitude() + "\nLNG: " + location.getLongitude());
                            sendLocationToServer(location.getLongitude(), location.getLatitude());
                        }
                    }
                });
    }

    private void sendLocationToServer(double longitude, double latitude) {
        //TODO SEND THE LOCATION TO THE SERVER LOGIC
        Log.e(TAG, "PendingIntent unable to execute request." + longitude + " AND " + latitude);

    }
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    private boolean isLocationPermissionGranted() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

}
