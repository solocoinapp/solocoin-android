package app.solocoin.solocoin;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import app.solocoin.solocoin.app.SharedPref;
import developers.mobile.abt.FirebaseAbt;
import timber.log.Timber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    Toolbar toolbar;
    Spinner spinner;
    SharedPreferences preferences;
    SharedPreferences.Editor preferencesEditor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        view.findViewById(R.id.permissionButton).setOnClickListener(this);
        view.findViewById(R.id.privacyPolicy).setOnClickListener(this);
        view.findViewById(R.id.termConditionButton).setOnClickListener(this);
        view.findViewById(R.id.logoutButton).setOnClickListener(this);
        return view;
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }
    
    
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.permissionButton:
                Timber.d("Permission stuff");
                startActivity(new Intent(getActivity(),PermissionsActivity.class));
                break;
            case R.id.privacyPolicy:
                Intent intent = new Intent(getActivity(),Policy.class);
                startActivity(intent);
                break;
            case R.id.termConditionButton:
                Intent intent1 = new Intent(getActivity(),ToS.class);
                startActivity(intent1);
                break;
            case R.id.logoutButton:
                logout();
                break;
        }
    }

    private void logout(){
        SharedPref.getInstance(getActivity()).clearSession();
        FirebaseAuth.getInstance().signOut();
        Intent intent2 = new Intent(getActivity(),OnboardingActivity.class);
        startActivity(intent2);
    }
}
