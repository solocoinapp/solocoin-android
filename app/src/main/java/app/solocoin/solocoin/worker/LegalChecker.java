package app.solocoin.solocoin.worker;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;


import com.scottyab.rootbeer.RootBeer;

import java.util.List;
import java.util.Objects;

/*
* Created by Vijay Daita on 5/20
 */

public class LegalChecker {
    private Context context;

    public LegalChecker(Context context){
        this.context = context;
    }
    public boolean isCheating() {
        RootBeer rootBeer = new RootBeer(context);
        return rootBeer.isRootedWithBusyBoxCheck() || isMockSettingsON(context) || areThereMockPermissionApps(context);
    }

    private static boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        return !Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
    }

    private static boolean areThereMockPermissionApps(Context context) {
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
                    for (String requestedPermission : requestedPermissions) {
                        if (requestedPermission
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception ", Objects.requireNonNull(e.getMessage()));
            }
        }

        return count > 0;
    }
}
