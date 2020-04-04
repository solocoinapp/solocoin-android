package com.shimadove.coronago;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shimadove.coronago.api.APIClient;
import com.shimadove.coronago.api.APIService;
import com.shimadove.coronago.app.SharedPref;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SessionWorker extends Worker {
    public SessionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        sendToServer(getApplicationContext());
        return Result.success();
    }

    private SharedPref sharedPref;

    private void sendToServer(Context context){
        APIService service = APIClient.getRetrofitInstance(context).create(APIService.class);
        sharedPref=SharedPref.getInstance(context);
        JsonObject body = new JsonObject();
        SessionBody type = new SessionBody(sharedPref.getSessiontype());
        Session session= new Session(type);
        Gson gson = new Gson();
        JsonElement jsonElement= gson.toJsonTree(session);
        body=jsonElement.getAsJsonObject();
        service.startSession(sharedPref.getAuthtoken(),body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code()==201)
                    Timber.d("Session type is sent successfully with code: " + response.code());
                else
                    Timber.d("Error code: "+ response.errorBody().toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Timber.d(t,"Unable to determine session type at backend.");
            }
        });{

        };
    }
}
