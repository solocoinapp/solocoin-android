package app.solocoin.solocoin.receiver;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.ListenableWorker;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import app.solocoin.solocoin.R;
import app.solocoin.solocoin.Session;
import app.solocoin.solocoin.SessionBody;
import app.solocoin.solocoin.app.Wallet;
import app.solocoin.solocoin.api.APIClient;
import app.solocoin.solocoin.api.APIService;
import app.solocoin.solocoin.app.SharedPref;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("LogNotTimber")
public class GeofenceRegistrationService extends IntentService {

    private static final String TAG = "GeoIntentService";
//    private static final String TAG = "xolo";

    private APIService apiService;
    private SharedPref sharedPref;

    public GeofenceRegistrationService(Context context) {
        super(TAG);
        apiService = APIClient.getRetrofitInstance(context).create(APIService.class);
        sharedPref = SharedPref.getInstance(context);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        Log.d(TAG, "geofencing event started!!");
        if (geofencingEvent == null) {
            Log.d(TAG, "geofencing event is null need to check again!!!");
            return;
        }
        if (geofencingEvent.hasError()) {
            String errorString = getErrorString(geofencingEvent.getErrorCode());
            Log.wtf(TAG, "GeofencingEvent error " + errorString);
        } else {
            int transaction = geofencingEvent.getGeofenceTransition();
            List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
            Geofence geofence = geofences.get(0);
            if (transaction == Geofence.GEOFENCE_TRANSITION_ENTER && geofence.getRequestId().equals(getString(R.string.GEOFENCE_ID))) {
                doApiCall("home");
            } else {
                doApiCall("away");
            }
            String geofenceTransitionDetails = getGeofenceTransitionDetails(transaction);
            doApiCall(geofenceTransitionDetails);
        }
    }

    private void doApiCall(String detail) {
        Log.wtf(TAG, detail);
        if (sharedPref.getSessionType() != null) {
            JsonObject body = new JsonObject();
            JsonObject session = new JsonObject();
            body.add("session", session);
            session.addProperty("type", sharedPref.getSessionType());
            Call<JsonObject> call = apiService.pingSession(sharedPref.getAuthToken(), body);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NotNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    JsonObject resp = response.body();
                    if (resp != null) {
                        sharedPref.setSessionStatus(resp.get("status").getAsString());
                        sharedPref.setSessionRewards(resp.get("rewards").getAsString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call,@NonNull Throwable t) {
                }
            });
        }
    }

    private String getGeofenceTransitionDetails(int geoFenceTransition) {
        String status = null;
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER )
            status = "home";
        else if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT )
            status = "away";
        return status;
    }

    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }
}