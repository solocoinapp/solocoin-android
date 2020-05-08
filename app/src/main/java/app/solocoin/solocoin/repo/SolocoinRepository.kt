package app.solocoin.solocoin.repo

import android.util.Log
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

    /*
     * Used by SessionPingWorker class to make api call for user
     * recent 'rewards' and 'status'.
     */
    fun doApiCall() {
        Log.wtf(SESSION_PING_API_CALL, "Calling api")
        TODO("Add call to solocoin api")
    }

    companion object {
        private const val SESSION_PING_API_CALL: String = "SESSION_PING_API_CALL"
    }
}