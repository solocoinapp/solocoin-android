package app.solocoin.solocoin.viewmodel;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MarkYourLocationViewModel extends AndroidViewModel {
    public String lat, lng;
    public String address;

    public MarkYourLocationViewModel(@NonNull Application application) {
        super(application);
    }

    private MarkYourLocationInterface markYourLocationInterface;
    public void setCreateProfileInterface(MarkYourLocationInterface signUpInterface) {
        this.markYourLocationInterface = signUpInterface;
    }

    public interface MarkYourLocationInterface{
        void onFetchedLatLng();
        void onFetchedAddress();
        void onConfirmClicked();
    }

    public void onConfirmClicked(View view) {
        markYourLocationInterface.onConfirmClicked();
    }
}
