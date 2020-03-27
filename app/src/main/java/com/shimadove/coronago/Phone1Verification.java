package com.shimadove.coronago;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.shimadove.coronago.databinding.ActivityPhone1VerificationBinding;

public class Phone1Verification extends AppCompatActivity {
    ActivityPhone1VerificationBinding binding;
    public static final String PHONE_NO = "com.shimadove.coronago.PHONE_NO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_phone1_verification);
        //nextClick();
        Button nextbutton=binding.nextButton;
        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phNum = "+91" + binding.phno.getText( ).toString( );
                if(!phNum.isEmpty() && phNum.length()==13){
                    openActivity2(phNum);
                    //startActivity(new Intent(Phone1Verification.this, Phone2Verification.class));
                }
                else{
                    binding.phno.setError("Phone number is not valid.");
                }
            }
        });
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