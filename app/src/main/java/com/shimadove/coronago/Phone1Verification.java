package com.shimadove.coronago;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shimadove.coronago.databinding.ActivityPhone1VerificationBinding;

public class Phone1Verification extends AppCompatActivity {
    ActivityPhone1VerificationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_phone1_verification);
        nextClick();
    }
    private void nextClick(){
        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Phone1Verification.this, Phone2Verification.class));
            }
        });
    }
}
