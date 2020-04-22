package app.solocoin.solocoin.app

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

/**
 * Created by Aditya Sonel on 22/04/20.
 */

class SharedPrefs(context: Context) {
    private val instance: SharedPreferences = context.getSharedPreferences("${context.packageName}_preferences", MODE_PRIVATE)

    private val AUTH_TOKEN = "AUTH_TOKEN"
    var authToken: String?
        get() = instance.getString(AUTH_TOKEN, null)
        set(value) = instance.edit().putString(AUTH_TOKEN, value).apply()

    private val COUNTRY_CODE = "COUNTRY_CODE"
    var countryCode: String?
        get() = instance.getString(COUNTRY_CODE, null)
        set(value) = instance.edit().putString(COUNTRY_CODE, value).apply()
}