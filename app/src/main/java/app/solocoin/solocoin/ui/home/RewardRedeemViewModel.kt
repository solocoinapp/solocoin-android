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

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class RewardRedeemViewModel(private val repository: SolocoinRepository) : ViewModel() {
    fun redeemReward(body: JsonObject): LiveData<Resource<JsonObject?>> = liveData(Dispatchers.IO) {
        if (body.size() != 0) {
            emit(Resource.loading(data = null))
            try {
                repository.redeemRewards(body).apply {
                    emit(Resource.success(data = body(), code = code()))
                }
            } catch (exception: Exception) {
                emit(Resource.error(data = null, exception = exception))
            }
        }
    }
}