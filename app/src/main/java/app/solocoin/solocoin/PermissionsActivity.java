package app.solocoin.solocoin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import app.solocoin.solocoin.app.SharedPref;

public class PermissionsActivity extends AppCompatActivity {

    Button locationButton;
    Button notificationsButton;
    Button continueButton;
    ImageView profilePhoto;
    private int ACCESS_FINE_LOCATION=1;
    private int first=1;
    private SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        locationButton = findViewById(R.id.location_permission_button);
        notificationsButton = findViewById(R.id.notification_permission_button);
        continueButton = findViewById(R.id.continue_button);
        profilePhoto = findViewById(R.id.profile_image);

        // Get profile photo from internet.
        sharedPref = SharedPref.getInstance(this);
        //checkAndRequestPermissions(true);
        //checkAndRequestPermissions(false);

        Intent intent = getIntent();
        boolean LOC_ADDED = intent.getBooleanExtra(MarkYourLocationActivity.LOC_ADDED,false);

        boolean isHomeLocationSet = sharedPref.getIsHomeLocationSet();

        if(isHomeLocationSet){
            locationButton.setEnabled(false);
            changeColor();
        }

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isHomeLocationSet){
                    Toast.makeText(PermissionsActivity.this,"We got your location before,you can just continue",Toast.LENGTH_SHORT).show();
                }else{
                    init();
                }
                //checkAndRequestPermissions(true);
                //checkAndRequestPermissions(false);
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPref.getHttpResponse()==404 && !LOC_ADDED){
                    Toast.makeText(PermissionsActivity.this,"We can't help you in social distancing without your location. Don't worry, it's safe with us!",Toast.LENGTH_SHORT).show();
                }
                startActivity(new Intent(PermissionsActivity.this,HomeActivity.class));
            }
        });
    }

    private void init() {
        if(sharedPref.getHttpResponse()==200){
            Toast.makeText(PermissionsActivity.this,"Cannot update your location.", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean locationPermissionFine = ContextCompat.checkSelfPermission(PermissionsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (locationPermissionFine) {
            changeColor();
            startActivity(new Intent(PermissionsActivity.this,MarkYourLocationActivity.class));
            return;
        }
        else{
            if (first==1) {
                ActivityCompat.requestPermissions(PermissionsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION);
                //Toast.makeText(PermissionsActivity.this, "Permission not given", Toast.LENGTH_SHORT).show();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(PermissionsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(PermissionsActivity.this, "Permission not given", Toast.LENGTH_SHORT).show();
                    first = 0;
                }
                ActivityCompat.requestPermissions(PermissionsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION);
            }
        }
    }

    private void changeColor() {
        locationButton.setBackgroundResource(R.drawable.grey_button_border_radius);
        locationButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_white, 0);
        locationButton.setTextColor(getResources().getColor(R.color.white));
    }

    public void pickProfileImage(View view){
        ImagePicker.Companion.with(this)
                .crop(1f, 1f)	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            Uri fileUri = data.getData();
            profilePhoto.setImageURI(fileUri);
            // Upload image to server
        } else if (resultCode == ImagePicker.RESULT_ERROR){
            Toast.makeText(this, "Error occured", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        first=0;
        if (requestCode == ACCESS_FINE_LOCATION){
            if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                this.init();
            } else {
                Toast.makeText(this,"Permission DENIED",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //    private void checkAndRequestPermissions(boolean request){
//        boolean locationPermissionCoarse = ContextCompat.checkSelfPermission(PermissionsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
//        boolean locationPermissionFine = ContextCompat.checkSelfPermission(PermissionsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
//        boolean changeButtonStyle = locationPermissionCoarse && locationPermissionFine;
//
//        if(changeButtonStyle){
//            startActivity(new Intent(PermissionsActivity.this, MarkYourLocationActivity.class));
//            locationButton.setBackgroundResource(R.drawable.grey_button_border_radius);
//            locationButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_white, 0);
//            locationButton.setTextColor(getResources().getColor(R.color.white));
//        }
//
//        if(request){
//            requestPermissions(locationPermissionCoarse, locationPermissionFine);
//        }
//    }
//
//    private void requestPermissions(boolean coarse, boolean fine){
//        if(coarse){
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                new AlertDialog.Builder(PermissionsActivity.this)
//                        .setTitle("Location Permission Needed")
//                        .setMessage("We need these permissions so that we can make sure you are staying in your house.")
//                        .setCancelable(false)
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // Whatever...
//                            }
//                        }).show();
//            } else {
//                ActivityCompat.requestPermissions(
//                        this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                        0);
//            }
//        }
//        if(fine){
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//                new AlertDialog.Builder(PermissionsActivity.this)
//                        .setTitle("Location Permission Needed")
//                        .setMessage("We need these permissions so that we can make sure you are staying in your house.")
//                        .setCancelable(false)
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // Whatever...
//                            }
//                        }).show();
//            } else {
//                ActivityCompat.requestPermissions(
//                        this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        1);
//            }
//        }
//    }
}
