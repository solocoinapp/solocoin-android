package app.solocoin.solocoin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import app.solocoin.solocoin.api.APIClient;
import app.solocoin.solocoin.api.APIService;
import app.solocoin.solocoin.app.SharedPref;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private SharedPref sharedPref;
    private TextView time;
    private String lat,lng;
    public HomeFragment() {
        // Required empty public constructor
    }

    static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPref = SharedPref.getInstance(getContext());
        time = view.findViewById(R.id.time);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        Log.d("xoxo, homeauthtoken", "the auth_token is: " + sharedPref.getAuthtoken());
        //JsonObject body = new JsonObject();
        //body.addProperty("auth_token", uid);
        APIService apiService = APIClient.getRetrofitInstance(getContext()).create(APIService.class);
        apiService.showUserData(sharedPref.getAuthtoken()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                //long uptime = System.currentTimeMillis();
                JsonObject userdata = response.body();
                if(userdata==null){
                    Timber.d("Response body is null, error code is: " + response.code());
                }
                else{
                    Timber.d("Response body is not null: " + response.body().toString());
                    Timber.d("the coordinates are, lat-long: " +sharedPref.getLatitude() + " - " + sharedPref.getLongitude());
                }
                long uptime = 0;
                float wallet_balance;
                lat="";
                lng="";
                if (userdata != null) {
                    uptime = userdata.get("home_duration_in_seconds").getAsLong();
                    wallet_balance = userdata.get("wallet_balance").getAsFloat();
                    Log.d("yoyo, wallet", "onResponse: wallet_balance is " + wallet_balance);
                    sharedPref.setWallet_balance(wallet_balance);
                }
                //long uptime = System.currentTimeMillis()
                Log.d("xoyo, time","The time in seconds is: " + Float.toString(uptime));
                if (!userdata.has("lat") && !userdata.has("lng")){
                    sharedPref.setIsHomeLocationSet(false);
                }
                else{
                    sharedPref.setIsHomeLocationSet(true);
                }
                long days = TimeUnit.SECONDS
                        .toDays(uptime);
                uptime -= TimeUnit.DAYS.toSeconds(days);

                long hours = TimeUnit.SECONDS
                        .toHours(uptime);
                uptime -= TimeUnit.HOURS.toSeconds(hours);

                long minutes = TimeUnit.SECONDS
                        .toMinutes(uptime);
                uptime -= TimeUnit.MINUTES.toSeconds(minutes);

                String displayTime= Long.toString(days) + "d " + Long.toString(hours) + "h " + Long.toString(minutes) + "m ";
                time.setText(displayTime);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}
