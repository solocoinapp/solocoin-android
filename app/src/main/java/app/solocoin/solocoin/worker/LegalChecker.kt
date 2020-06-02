package app.solocoin.solocoin.worker;


import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import com.scottyab.rootbeer.RootBeer
import kotlinx.coroutines.InternalCoroutinesApi


/*
 * Created by Vijay Daita on 5/20
 */

public class LegalChecker(var context: Context){
    @InternalCoroutinesApi
    public fun isCheating() : Boolean{
        var rootBeer = RootBeer(context)
        var mock = false;
        sharedPrefs?.let {
            mock = it.mock
        }
        return  rootBeer.isRooted() && mock
    }

    fun isMockSettingsON(context: Context): Boolean {
        // returns true if mock location enabled, false if not enabled.
        return if (Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ALLOW_MOCK_LOCATION
            ) == "0"
        ) false else true
    }

    fun areThereMockPermissionApps(context: Context): Boolean {
        var count = 0
        val pm = context.packageManager
        val packages =
            pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (applicationInfo in packages) {
            try {
                val packageInfo = pm.getPackageInfo(
                    applicationInfo.packageName,
                    PackageManager.GET_PERMISSIONS
                )

                // Get Permissions
                val requestedPermissions =
                    packageInfo.requestedPermissions
                if (requestedPermissions != null) {
                    for (i in requestedPermissions.indices) {
                        if ((requestedPermissions[i]
                                    == "android.permission.ACCESS_MOCK_LOCATION") && applicationInfo.packageName != context.packageName
                        ) {
                            count++
                        }
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e("Got exception ", e.message)
            }
        }
        return if (count > 0) true else false
    }
}
