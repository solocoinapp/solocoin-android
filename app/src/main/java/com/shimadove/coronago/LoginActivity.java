package com.shimadove.coronago;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import timber.log.Timber;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.shimadove.coronago.databinding.ActivityLoginBinding;
import com.shimadove.coronago.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity implements LoginViewModel.LoginInterface {

    ActivityLoginBinding binding;
    LoginViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

         binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
         viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
         binding.getRoot();
         binding.setLoginViewModel(viewModel);

         viewModel.setLoginInterface(this);
    }

    @Override
    public void onLoginSuccess() {

    }

    @Override
    public void onLoginFailed() {

    }

    @Override
    public void onSignUpClicked() {
        Timber.d("Sign up");
    }

    @Override
    public void onFacebookClicked() {
        Timber.d("Facebook");
    }

    @Override
    public void onGoogleClicked() {
        Timber.d("Google");
    }
}
