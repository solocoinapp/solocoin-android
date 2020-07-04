package app.solocoin.solocoin.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import app.solocoin.solocoin.model.LeaderBoard
import app.solocoin.solocoin.model.Milestones
import app.solocoin.solocoin.repo.SolocoinRepository
import app.solocoin.solocoin.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * Created by Saurav Gupta on 22/05/2020
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class MilestonesFragmentViewModel(private val repository: SolocoinRepository) : ViewModel() {
    fun getBadgesLevels(): LiveData<Resource<Milestones?>> = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            repository.getBadgesLevels().apply {
                emit(Resource.success(data = body(), code = code()))
            }
        } catch (exception: Exception) {
            emit(Resource.error(data = null, exception = exception))
        }
    }
    fun getleaderboard(): LiveData<Resource<LeaderBoard?>> = liveData(Dispatchers.IO){
        emit(Resource.loading(data = null))
        try{
            repository.getleaderboard().apply {
                emit(Resource.success(data = body(), code = code()))
            }
        }catch (exception: Exception) {
            emit(Resource.error(data = null, exception = exception))
        }
    }
}