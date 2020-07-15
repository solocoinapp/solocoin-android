package app.solocoin.solocoin.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import app.solocoin.solocoin.model.Profile
import app.solocoin.solocoin.model.Reward
import app.solocoin.solocoin.repo.SolocoinRepository
import app.solocoin.solocoin.util.Resource
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class AllScratchCardsViewModel(val repository: SolocoinRepository) : ViewModel() {
    fun getProfile(): LiveData<Resource<Profile?>> = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            repository.getprofile().apply {
                emit(Resource.success(data = body(), code = code()))
            }
        } catch (exception: Exception) {
            emit(Resource.error(data = null, exception = exception))
        }
    }
    fun getScratchCardOffers(): LiveData<Resource<ArrayList<Reward>?>> = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            repository.getScratchCardOffers().apply {
                emit(Resource.success(data = body(), code = code()))
            }
        } catch (exception: Exception) {
            emit(Resource.error(data = null, exception = exception))
        }
    }
}