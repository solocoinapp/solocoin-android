package app.solocoin.solocoin.viewmodel;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import timber.log.Timber;

public class CreateProfileViewModel extends AndroidViewModel {

    public String email,username,dob;

    public CreateProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public void setCreateProfileInterface(CreateProfileInterface signUpInterface) {
        this.createProfileInterface = signUpInterface;
    }

    private CreateProfileInterface createProfileInterface;

    public interface CreateProfileInterface{
        void onCreateProfileSuccess();
        void onCreateProfileFailed();
        void onContinueClicked();
    //    void onMaleClicked();
    //    void onFemaleClicked();
    //   void onSkip();
    }

//    public void onMaleClicked(View view){
//        createProfileInterface.onMaleClicked();
//    }
//
//    public void onFemaleClicked(View view){
//        createProfileInterface.onFemaleClicked();
//    }
//
//    public void onLoginButtonClick(View view){
//        Timber.d("Login clicked");
//    }

    public void onContinueButtonClick(View view){
        Timber.d("Sign Up clicked");
        createProfileInterface.onContinueClicked();
    }


//    public void onSkip(View view){
//        createProfileInterface.onSkip();
//    }
}
