package com.shimadove.coronago.viewmodel;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class LoginViewModel extends AndroidViewModel {

    public String email,password;

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public void setLoginInterface(LoginInterface signUpInterface) {
        this.loginInterface = signUpInterface;
    }

    private LoginInterface loginInterface;

    public interface LoginInterface{
        void onLoginSuccess();
        void onLoginFailed();
        void onSignUpClicked();
        void onFacebookClicked();
        void onGoogleClicked();
    }

    public void onLoginButtonClick(View view){
        Timber.d("Login clicked");
    }

    public void onSignUpButtonClick(View view){
        Timber.d("Sign Up clicked");
        loginInterface.onSignUpClicked();
    }

    public void onFacebookClicked(View view){
        Timber.d("Facebook clicked");
        loginInterface.onFacebookClicked();
    }

    public void onGoogleClicked(View view){
        Timber.d("Google clicked");
        loginInterface.onGoogleClicked();
    }
}
