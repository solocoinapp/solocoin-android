package app.solocoin.solocoin.repo

import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

/**
 * Created by Aditya Sonel on 22/04/20.
 */

@ExperimentalCoroutinesApi
class SolocoinRepository(private val apiService: ApiService) {

    fun mobileLogin(obj: JsonObject): Flow<NetworkCallState<JsonObject>> {
        return object : NetworkBoundRepo<JsonObject>() {
            override suspend fun fetchFromRemote(): Response<JsonObject> = apiService.mobileLogin(obj)
        }.asFlow().flowOn(Dispatchers.IO)
    }

    fun mobileSignUp(obj: JsonObject): Flow<NetworkCallState<JsonObject>> {
        return object : NetworkBoundRepo<JsonObject>() {
            override suspend fun fetchFromRemote(): Response<JsonObject> = apiService.mobileSignUp(obj)
        }.asFlow().flowOn(Dispatchers.IO)
    }
}