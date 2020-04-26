package app.solocoin.solocoin.app

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

/**
 * Created by Aditya Sonel on 22/04/20.
 */

class SharedPrefs(context: Context) {
    private val instance: SharedPreferences = context.getSharedPreferences("${context.packageName}_preferences", MODE_PRIVATE)

    private val auth_token = "auth_token"
    var authToken: String?
        get() = instance.getString(auth_token, null)
        set(value) = instance.edit().putString(auth_token, value).apply()

    private val country_code = "country_code"
    var countryCode: String?
        get() = instance.getString(country_code, null)
        set(value) = instance.edit().putString(country_code, value).apply()
}