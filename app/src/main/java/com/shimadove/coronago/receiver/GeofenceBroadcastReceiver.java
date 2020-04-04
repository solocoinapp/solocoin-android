package com.shimadove.coronago.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.gson.JsonObject;
import com.shimadove.coronago.app.Wallet;
import com.shimadove.coronago.api.APIClient;
import com.shimadove.coronago.api.APIService;
import com.shimadove.coronago.app.SharedPref;

import org.jetbrains.annotations.NotNull;

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
    private Wallet wallet;
    private void reportSession(String type, Context context){
        APIService service = APIClient.getRetrofitInstance(context).create(APIService.class);
        JsonObject object = new JsonObject();
        wallet = new Wallet();
        wallet.Updatebalance(context);
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
