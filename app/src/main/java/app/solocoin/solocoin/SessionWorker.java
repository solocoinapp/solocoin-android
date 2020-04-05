package app.solocoin.solocoin;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import app.solocoin.solocoin.api.APIClient;
import app.solocoin.solocoin.api.APIService;
import app.solocoin.solocoin.app.SharedPref;

import java.util.List;

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
        boolean cheating = areThereMockPermissionApps(getApplicationContext()) || isMockSettingsON(getApplicationContext());
        if(!cheating) {
            service.startSession(sharedPref.getAuthtoken(), body).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.code() == 201)
                        Timber.d("Session type is sent successfully with code: " + response.code());
                    else
                        Timber.d("Error code: " + response.errorBody().toString());
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Timber.d(t, "Unable to determine session type at backend.");
                }
            });
        } else {
            // Location spoofing
        }
    }

    // https://stackoverflow.com/questions/6880232/disable-check-for-mock-location-prevent-gps-spoofing/34726023#34726023

      public static boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
    }

    public static boolean areThereMockPermissionApps(Context context) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i]
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception " , e.getMessage());
            }
        }

        if (count > 0)
            return true;
        return false;
    }
}
