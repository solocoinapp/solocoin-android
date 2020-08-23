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
 * Created by Aditya Sonel on 12/05/20.
 */

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class CreateProfileViewModel(private val repository: SolocoinRepository): ViewModel() {
    fun mobileSignup(body: JsonObject): LiveData<Resource<JsonObject?>> = liveData(Dispatchers.IO) {
        if (body.size() != 0) {
            emit(Resource.loading(data = null))
            try {
                repository.mobileSignUp(body).apply {
                    emit(Resource.success(data = body(), code = code()))
                }
            } catch (exception: Exception) {
                emit(Resource.error(data = null, exception = exception))
            }
        }
    }

    fun userUpdate(body: JsonObject): LiveData<Resource<JsonObject?>> = liveData(Dispatchers.IO) {
        if (body.size() != 0) {
            emit(Resource.loading(data = null))
            try {
                repository.userUpdate(body).apply {
                    emit(Resource.success(data = body(), code = code()))
                }
            } catch (exception: Exception) {
                emit(Resource.error(data = null, exception = exception))
            }
        }
    }
    fun refer_and_earn(newusertoken:String,refercode:String): LiveData<Resource<JsonObject?>> = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            repository.refer_and_earn(newusertoken,refercode).apply {
                emit(Resource.success(data = body(), code = code()))
            }
        } catch (exception: Exception) {
            emit(Resource.error(data = null, exception = exception))
        }
    }
}