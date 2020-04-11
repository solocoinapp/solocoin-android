package app.solocoin.solocoin;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import app.solocoin.solocoin.app.SharedPref;
import developers.mobile.abt.FirebaseAbt;
import timber.log.Timber;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setPositiveButton("Logout", (dialogInterface, i) -> {
            SharedPref.getInstance(getActivity()).clearSession();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), OnboardingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            SharedPref.getInstance(getActivity()).setReceiverOn(false);
            SharedPref.getInstance(getActivity()).setSessionType("away");
            Objects.requireNonNull(getActivity()).finish();

        }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).setTitle("Please confirm!").setMessage("Do you really want to logout!!!").show();
    }
}
