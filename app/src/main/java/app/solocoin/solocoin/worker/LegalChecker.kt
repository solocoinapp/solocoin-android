package app.solocoin.solocoin.worker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs


import com.scottyab.rootbeer.RootBeer;
import kotlinx.coroutines.InternalCoroutinesApi

import java.util.List;
import java.util.Objects;

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
}
