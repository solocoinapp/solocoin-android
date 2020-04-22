package app.solocoin.solocoin.repo

import androidx.annotation.MainThread
import kotlinx.coroutines.flow.flow
import retrofit2.Response

/**
 * Created by Aditya Sonel on 22/04/20.
 */

abstract class NetworkBoundRepo<T> {

    fun asFlow() = flow<NetworkCallState<T>> {
        emit(NetworkCallState.loading())
        try {
            val apiResponse = fetchFromRemote()
            val data = apiResponse.body()
            if (apiResponse.isSuccessful && data != null) {
                emit(NetworkCallState.success(data))
            } else {
                emit(NetworkCallState.error(apiResponse.message()))
            }
        } catch (e: Exception) {
            emit(NetworkCallState.error("Network error! can't get latest data."))
        }
    }

    @MainThread
    protected abstract suspend fun fetchFromRemote(): Response<T>
}