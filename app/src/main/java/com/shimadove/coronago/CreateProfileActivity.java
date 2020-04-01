package com.shimadove.coronago;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import timber.log.Timber;

import android.os.Bundle;

import com.shimadove.coronago.databinding.ActivityCreateProfileBinding;
import com.shimadove.coronago.viewmodel.CreateProfileViewModel;

public class CreateProfileActivity extends AppCompatActivity implements CreateProfileViewModel.CreateProfileInterface {

    ActivityCreateProfileBinding binding;
    CreateProfileViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

         binding = DataBindingUtil.setContentView(this,R.layout.activity_create_profile);
         viewModel = new ViewModelProvider(this).get(CreateProfileViewModel.class);
         binding.getRoot();
         binding.setCreateProfileViewModel(viewModel);

         viewModel.setCreateProfileInterface(this);
    }

    @Override
    public void onCreateProfileSuccess() {

    }

    @Override
    public void onCreateProfileFailed() {

    }

    @Override
    public void onContinueClicked() {

    }


    @Override
    public void onMaleClicked() {

    }

    @Override
    public void onFemaleClicked() {

    }

    @Override
    public void onSkip() {

    }
}
