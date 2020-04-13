package app.solocoin.solocoin.util;

import android.content.Context;
import android.location.LocationManager;

public class GlobalFunc {
    public static boolean isGpsEnabled(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
