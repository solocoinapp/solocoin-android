package app.solocoin.solocoin.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import app.solocoin.solocoin.repo.SolocoinRepository
import app.solocoin.solocoin.util.Resource
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi


/**
 * Created by Aditya Sonel on 27/04/20.
 */

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class LoginSignupViewModel(private val repository: SolocoinRepository): ViewModel() {
    fun mobileLogin(body: JsonObject): LiveData<Resource<JsonObject?>> = liveData(Dispatchers.IO) {
        if (body.size() != 0) {
            emit(Resource.loading(data = null))
            try {
                repository.mobileLogin(body).apply {
                    emit(Resource.success(data = body(), code = code()))
                }
            } catch (exception: Exception) {
                emit(Resource.error(data = null, exception = exception))
            }
        }
    }

    fun userData(): LiveData<Resource<JsonObject?>> = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            repository.userData().apply {
                emit(Resource.success(data = body(), code = code()))
            }
        } catch (exception: Exception) {
            emit(Resource.error(data = null, exception = exception))
        }
    }
}