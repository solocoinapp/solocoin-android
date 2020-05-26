package app.solocoin.solocoin.ui.home

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
 * Created by Saurav Gupta on 22/05/2020
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class MilestonesFragmentViewModel(private val repository: SolocoinRepository) : ViewModel() {

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