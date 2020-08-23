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
 *  Created by Karandeep Singh on 23/08/2020
 */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class ProfileViewModel( val repository: SolocoinRepository) : ViewModel() {
    fun redeemcoupon(promocode:String): LiveData<Resource<JsonObject?>> = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            repository.redeemcoupon(promocode).apply {
                emit(Resource.success(data = body(), code = code()))
            }
        } catch (exception: Exception) {
            emit(Resource.error(data = null, exception = exception))
        }
    }

}