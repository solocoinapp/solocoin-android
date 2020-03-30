package com.shimadove.coronago;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;

public class PermissionsActivity extends AppCompatActivity {

    Button locationButton;
    Button notificationsButton;
    Button continueButton;
    ImageView profilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        locationButton = findViewById(R.id.location_permission_button);
        notificationsButton = findViewById(R.id.notification_permission_button);
        continueButton = findViewById(R.id.continue_button);
        profilePhoto = findViewById(R.id.profile_image);

        // Get profile photo from internet.

        checkAndRequestPermissions(true);
        checkAndRequestPermissions(false);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermissions(true);
                checkAndRequestPermissions(false);
            }
        });
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

    private void checkAndRequestPermissions(boolean request){
        boolean locationPermissionCoarse = ContextCompat.checkSelfPermission(PermissionsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean locationPermissionFine = ContextCompat.checkSelfPermission(PermissionsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean changeButtonStyle = locationPermissionCoarse && locationPermissionFine;

        if(changeButtonStyle){
            locationButton.setBackgroundResource(R.drawable.grey_button_border_radius);
            locationButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_white, 0);
            locationButton.setTextColor(getResources().getColor(R.color.white));
        }

        if(request){
            requestPermissions(locationPermissionCoarse, locationPermissionFine);
        }
    }

    private void requestPermissions(boolean coarse, boolean fine){
        if(coarse){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                new AlertDialog.Builder(PermissionsActivity.this)
                        .setTitle("Location Permission Needed")
                        .setMessage("We need these permissions so that we can make sure you are staying in your house.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Whatever...
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        0);
            }
        }
        if(fine){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(PermissionsActivity.this)
                        .setTitle("Location Permission Needed")
                        .setMessage("We need these permissions so that we can make sure you are staying in your house.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Whatever...
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }
    }
}
