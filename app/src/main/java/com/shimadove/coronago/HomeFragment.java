package com.shimadove.coronago;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;
import com.shimadove.coronago.api.APIClient;
import com.shimadove.coronago.api.APIService;
import com.shimadove.coronago.app.SharedPref;

import org.w3c.dom.Text;

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
    TextView time;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
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
        time = getView().findViewById(R.id.time);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
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
                }
                long uptime = userdata.get("home_duration_in_seconds").getAsLong();
                //long uptime = System.currentTimeMillis();
                long days = TimeUnit.MILLISECONDS
                        .toDays(uptime);
                uptime -= TimeUnit.DAYS.toMillis(days);

                long hours = TimeUnit.MILLISECONDS
                        .toHours(uptime);
                uptime -= TimeUnit.HOURS.toMillis(hours);

                long minutes = TimeUnit.MILLISECONDS
                        .toMinutes(uptime);
                uptime -= TimeUnit.MINUTES.toMillis(minutes);


                time.setText(days + "d " + hours + "h " + minutes + "m");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}
