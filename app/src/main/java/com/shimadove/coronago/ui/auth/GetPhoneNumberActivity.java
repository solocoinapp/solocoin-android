package com.shimadove.coronago.ui.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.shimadove.coronago.R;
import com.shimadove.coronago.app.SharedPref;
import com.shimadove.coronago.databinding.ActivityGetPhoneNumberBinding;

public class GetPhoneNumberActivity extends AppCompatActivity {

    private static final String TAG = "PhoneAuthActivity";
    ActivityGetPhoneNumberBinding binding;

    public static final String PHONE_NO = "PHONE_NO";
    private CountryCodePicker ccp;

    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_get_phone_number);

        sharedPref = SharedPref.getInstance(this);

        //nextClick();
        ccp = findViewById(R.id.ccp);
        Button nextbutton = binding.nextButton;
        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phNum = ccp.getSelectedCountryCodeWithPlus() + binding.phno.getText().toString();
                //sharedPref.setCountryCode(ccp.getSelectedCountryCode());
                sharedPref.setCountryCode(ccp.getSelectedCountryCodeWithPlus());
                sharedPref.setPhoneNumber(binding.phno.getText().toString());

                ccp.registerPhoneNumberTextView(binding.phno);
                if(ccp.isValid()){
                    openActivity2(phNum);
                } else {
                    binding.phno.setError("Phone number is not valid.");
                }
            }
        });
    }

    public void openActivity2(String phNum){
        Intent intent = new Intent(this, VerifyOtpActivity.class);
        intent.putExtra(PHONE_NO,phNum);
        startActivity(intent);
    }
}