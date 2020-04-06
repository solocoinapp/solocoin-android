package app.solocoin.solocoin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import app.solocoin.solocoin.app.SharedPref;
import app.solocoin.solocoin.databinding.ActivityPhone1VerificationBinding;

public class Phone1Verification extends AppCompatActivity {
    private static final String TAG = "PhoneAuthActivity";
    ActivityPhone1VerificationBinding binding;
    public static final String PHONE_NO = "PHONE_NO";
    private CountryCodePicker ccp;

    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_phone1_verification);

        sharedPref = SharedPref.getInstance(this);

        //nextClick();
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        Button nextbutton=binding.nextButton;
        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phNum = ccp.getSelectedCountryCodeWithPlus() + binding.phno.getText().toString();
                //sharedPref.setCountryCode(ccp.getSelectedCountryCode());
                sharedPref.setCountryCode(ccp.getSelectedCountryCodeWithPlus());
                sharedPref.setPhoneNumber(binding.phno.getText().toString());


                Log.d(TAG,"phone number is: " + phNum);
                ccp.registerPhoneNumberTextView(binding.phno);
                if(ccp.isValid()){
                    openActivity2(phNum);
                    //startActivity(new Intent(Phone1Verification.this, Phone2Verification.class));
                } else {
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