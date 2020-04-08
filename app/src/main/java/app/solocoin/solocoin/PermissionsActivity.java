package app.solocoin.solocoin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.logging.Logger;

import app.solocoin.solocoin.app.SharedPref;
import app.solocoin.solocoin.util.AppPermissionChecker;

public class PermissionsActivity extends AppCompatActivity {

    private Button locationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        locationButton = findViewById(R.id.location_permission_button);
        Button continueButton = findViewById(R.id.continue_button);

        if(AppPermissionChecker.isLocationPermissionGranted(this)){
            locationButton.setEnabled(false);
            changeLocationBtnColor();
        }

        locationButton.setOnClickListener(v -> {
            if(AppPermissionChecker.isLocationPermissionGranted(this)){
                Toast.makeText(this,"Thanks for allowing permission, you can continue...",Toast.LENGTH_SHORT).show();
                changeLocationBtnColor();
            } else{
                requestPermission();
            }
        });

        continueButton.setOnClickListener(v -> {
            if (!AppPermissionChecker.isLocationPermissionGranted(this)){
                Toast.makeText(this,"We can't help you in social distancing without your location. Don't worry, it's safe with us!",Toast.LENGTH_SHORT).show();
                requestPermission();
                return;
            }
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AppPermissionChecker.isLocationPermissionGranted(this)){
            Toast.makeText(this,"Thanks for allowing permission, you can continue...",Toast.LENGTH_SHORT).show();
            changeLocationBtnColor();
        }
    }

    private void changeLocationBtnColor() {
        locationButton.setBackgroundResource(R.drawable.dark_blue_border_radius);
        locationButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_white, 0);
        locationButton.setTextColor(ContextCompat.getColor(this, R.color.white));
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
                    changeLocationBtnColor();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //    private void init() {
//        if(sharedPref.getIsHomeLocationSet()){
//            Toast.makeText(PermissionsActivity.this,"Cannot update your location.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        boolean locationPermissionFine = ContextCompat.checkSelfPermission(PermissionsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
//        if (locationPermissionFine) {
//            changeLocationBtnColor();
//            startActivity(new Intent(PermissionsActivity.this,MarkYourLocationActivity.class));
//            return;
//        }
//        else{
//            if (first==1) {
//                ActivityCompat.requestPermissions(PermissionsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION);
//                //Toast.makeText(PermissionsActivity.this, "Permission not given", Toast.LENGTH_SHORT).show();
//            } else {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(PermissionsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                    Toast.makeText(PermissionsActivity.this, "Permission not given", Toast.LENGTH_SHORT).show();
//                    first = 0;
//                }
//
//            }
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        first=0;
//        if (requestCode == ACCESS_FINE_LOCATION){
//            if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
//                this.init();
//            } else {
//                Toast.makeText(this,"Permission DENIED",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    //    public void pickProfileImage(View view){
//        ImagePicker.Companion.with(this)
//                .crop(1f, 1f)	    			//Crop image(Optional), Check Customization for more option
//                .compress(1024)			//Final image size will be less than 1 MB(Optional)
//                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
//                .start();
//
//    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////        if(resultCode == Activity.RESULT_OK){
////            Uri fileUri = data.getData();
////            profilePhoto.setImageURI(fileUri);
////            // Upload image to server
////        } else if (resultCode == ImagePicker.RESULT_ERROR){
////            Toast.makeText(this, "Error occured", Toast.LENGTH_SHORT).show();
////        }
//    }

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
