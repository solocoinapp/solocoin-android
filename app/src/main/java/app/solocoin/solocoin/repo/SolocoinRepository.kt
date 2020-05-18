package app.solocoin.solocoin.repo

import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

/**
 * Created by Aditya Sonel on 22/04/20.
 */

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class SolocoinRepository(private val apiService: ApiService) {
    suspend fun mobileLogin(body: JsonObject) = apiService.mobileLogin(body)
    suspend fun mobileSignUp(body: JsonObject) = apiService.mobileSignUp(body)

    suspend fun userData() = apiService.userData(sharedPrefs?.authToken!!)
    suspend fun userUpdate(body: JsonObject) = apiService.userUpdate(sharedPrefs?.authToken!!, body)
}