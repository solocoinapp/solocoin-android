package app.solocoin.solocoin.app

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import app.solocoin.solocoin.model.Milestones
import app.solocoin.solocoin.model.Reward
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by Aditya Sonel on 22/04/20.
 */

class SharedPrefs(context: Context) {
    private val instance: SharedPreferences = context.getSharedPreferences("${context.packageName}_preferences", MODE_PRIVATE)

    private val auth_token = "auth_token"
    var authToken: String?
        get() = instance.getString(auth_token, null)
        set(value) = instance.edit().putString(auth_token, "Bearer $value").apply()

    private val _id = "id"
    var id: String?
        get() = instance.getString(_id, null)
        set(value) = instance.edit().putString(_id, value).apply()

    private val country_code = "country_code"
    var countryCode: String?
        get() = instance.getString(country_code, null)
        set(value) = instance.edit().putString(country_code, value).apply()

    private val mobile_number = "mobile_number"
    var mobileNumber: String?
        get() = instance.getString(mobile_number, null)
        set(value) = instance.edit().putString(mobile_number, value).apply()

    private val _name = "name"
    var name: String?
        get() = instance.getString(_name, null)
        set(value) = instance.edit().putString(_name, value).apply()

    private val id_token = "id_token"
    var idToken: String?
        get() = instance.getString(id_token, null)
        set(value) = instance.edit().putString(id_token, value).apply()

    private val user_lat = "user_lat"
    var userLat: String?
        get() = instance.getString(user_lat, null)
        set(value) = instance.edit().putString(user_lat, value).apply()

    private val user_long = "user_long"
    var userLong: String?
        get() = instance.getString(user_long, null)
        set(value) = instance.edit().putString(user_long, value).apply()

    private val current_lat = "current_lat"
    var currentLat: String?
        get() = instance.getString(current_lat, null)
        set(value) = instance.edit().putString(current_lat, value).apply()

    private val current_long = "current_long"
    var currentLong: String?
        get() = instance.getString(current_long, null)
        set(value) = instance.edit().putString(current_long, value).apply()

    private val home_duration = "home_duration"
    var homeDuration: Long
        get() = instance.getLong(home_duration, 0)
        set(value) = instance.edit().putLong(home_duration, value).apply()

    private val wallet_balance = "wallet_balance"
    var walletBalance: String?
        get() = instance.getString(wallet_balance, "0.0")
        set(value) = instance.edit().putString(wallet_balance, value).apply()

    private val _rewards = "rewards"
    var rewards: String?
        get() = instance.getString(_rewards, null)
        set(value) = instance.edit().putString(_rewards, value).apply()

    private val _offers = "offers"
    var offers: ArrayList<Reward>?
        get() = Gson().fromJson<ArrayList<Reward>?>(
            instance.getString(_offers, null),
            object : TypeToken<ArrayList<Reward>>() {}.type
        )
        set(value) = instance.edit().putString(_offers, Gson().toJson(value)).apply()

    private val _milestones = "milestones"
    var milestones: Milestones?
        get() = Gson().fromJson(instance.getString(_milestones, null), Milestones::class.java)
        set(value) = instance.edit().putString(_milestones, Gson().toJson(value)).apply()

    private val _status = "status"
    var status: String?
        get() = instance.getString(_status, null)
        set(value) = instance.edit().putString(_status, value).apply()

    private val daily_quiz_time = "daily_quiz_time"
    var dailyQuizTime: Long
        get() = instance.getLong(daily_quiz_time, 0)
        set(value) = instance.edit().putLong(daily_quiz_time, value).apply()

    private val count_answered_weekly_quiz = "count_answered_weekly_quiz"
    var countAnsweredWeeklyQuiz: Int
        get() = instance.getInt(count_answered_weekly_quiz, 0)
        set(value) = instance.edit().putInt(count_answered_weekly_quiz, value).apply()

    private val _mock = "mock"
    var mock: Boolean
        get() = instance.getBoolean(_mock, false)
        set(value) = instance.edit().putBoolean(_mock, value).apply()

    fun clearSession() {
        instance.edit()
            .clear()
            .apply()
    }
}