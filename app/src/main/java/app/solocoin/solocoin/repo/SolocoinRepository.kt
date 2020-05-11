package app.solocoin.solocoin.repo

import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import com.google.gson.JsonObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * Created by Aditya Sonel on 22/04/20.
 */

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class SolocoinRepository(private val apiService: ApiService) {
    suspend fun mobileLogin(body: JsonObject) = apiService.mobileLogin(body)
    suspend fun mobileSignUp(body: JsonObject) = apiService.mobileSignUp(body)
    suspend fun userData() = apiService.userData(sharedPrefs?.authToken!!)
    fun pingSession(body: JsonObject) = apiService.pingSession(sharedPrefs?.authToken!!, body)
    fun rewards(reward: String) {
        sharedPrefs?.rewards = reward
    }

    fun status(status: String) {
        sharedPrefs?.status = status
    }
}