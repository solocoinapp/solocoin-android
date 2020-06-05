package app.solocoin.solocoin.ui.auth

import androidx.lifecycle.ViewModel
import app.solocoin.solocoin.repo.SolocoinRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * Created by Aditya Sonel on 12/05/20.
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class MarkYourLocationViewModel(private val repository: SolocoinRepository): ViewModel() {
//    fun userUpdate(body: JsonObject): LiveData<Resource<JsonObject?>> = liveData(Dispatchers.IO) {
//        if (body.size() != 0) {
//            emit(Resource.loading(data = null))
//            try {
//                repository.userUpdate(body).apply {
//                    emit(Resource.success(data = body(), code = code()))
//                }
//            } catch (exception: Exception) {
//                emit(Resource.error(data = null, exception = exception))
//            }
//        }
//    }
}