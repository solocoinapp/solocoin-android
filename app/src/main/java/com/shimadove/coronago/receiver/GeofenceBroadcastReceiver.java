package com.shimadove.coronago.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonObject;
import com.shimadove.coronago.api.APIClient;
import com.shimadove.coronago.api.APIService;
import com.shimadove.coronago.app.SharedPref;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GeofenceBroadcastReceiver extends BroadcastReceiver{
    private float wallet_balance;

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e("SoloCoin", "error");
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            reportSession("home", context);
        } else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            reportSession("away", context);
        }
    }
    private SharedPref sharedPref;
    private void reportSession(String type, Context context){
        APIService service = APIClient.getRetrofitInstance(context).create(APIService.class);
        JsonObject object = new JsonObject();
        object.addProperty("type", type);
        sharedPref = SharedPref.getInstance(context);
        JSONObject userbody =new JSONObject();
        service.showUserData(userbody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject userdata = response.body();
                assert userdata != null;
                wallet_balance= userdata.get("wallet_balance").getAsFloat();
                sharedPref.setWallet_balance(wallet_balance);
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //To decide what to do here
            }
        });

        Call<JsonObject> call = service.startSession(object);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NotNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                Log.e("SoloCoin", "Start session success");
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call,@NonNull Throwable t) {
                //on-failure-api-call
                Toast.makeText(context, "Error in SoloCoin", Toast.LENGTH_LONG).show();
            }
        });
    }
}
