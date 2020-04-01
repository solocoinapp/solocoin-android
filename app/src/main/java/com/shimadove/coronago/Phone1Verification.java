package com.shimadove.coronago;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.shimadove.coronago.databinding.ActivityPhone1VerificationBinding;

public class Phone1Verification extends AppCompatActivity {
    ActivityPhone1VerificationBinding binding;
    public static final String PHONE_NO = "com.shimadove.coronago.PHONE_NO";
    CountryCodePicker ccp;
    private static final String TAG = "PhoneAuthActivity";
    private String countrycode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_phone1_verification);
        //nextClick();
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        Button nextbutton=binding.nextButton;
        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phNum = ccp.getSelectedCountryCodeWithPlus()+binding.phno.getText( ).toString( );

                SharedPreferences preferences = getApplication().getSharedPreferences("information", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                countrycode=ccp.getSelectedCountryCodeWithPlus();

                editor.putString("country_code", ccp.getSelectedCountryCode());
                editor.putString("phone_number", binding.phno.getText().toString());
                Log.d(TAG,"phone number is: " + phNum);
                if(!phNum.isEmpty() && phNum.length()==13){
                    openActivity2(phNum);
                    //startActivity(new Intent(Phone1Verification.this, Phone2Verification.class));
                }
                else{
                    binding.phno.setError("Phone number is not valid.");
                }
            }
        });
        //ccp.registerPhoneNumberTextView(binding.phno);
    }
    public void openActivity2(String phNum){
        Intent intent = new Intent(this,Phone2Verification.class);
        intent.putExtra(PHONE_NO,phNum);
        startActivity(intent);
    }
}
//    private void nextClick(){
//        binding.nextButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//
//            public void openActivity2(){
//                Intent intent=new Intent(this,Phone2Verification.class);
//            }
//        });
//    }