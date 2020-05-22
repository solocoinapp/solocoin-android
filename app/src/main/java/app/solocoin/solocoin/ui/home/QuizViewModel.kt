package app.solocoin.solocoin.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import app.solocoin.solocoin.repo.SolocoinRepository
import app.solocoin.solocoin.util.Resource
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.lang.Exception

/**
 * Created by Ankur Kumar on 16/05/20
 */

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class QuizViewModel(private val repository: SolocoinRepository): ViewModel() {

    fun getDailyQuiz(): LiveData<Resource<JsonObject?>> = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            repository.getDailyQuiz().apply {
                emit(Resource.success(data = body(), code = code()))
            }
        } catch (exception: Exception){
            emit(Resource.error(data = null, exception = exception))
        }
    }

    fun getWeeklyQuiz(): LiveData<Resource<JsonObject?>> = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            repository.getWeeklyQuiz().apply {
                emit(Resource.success(data = body(), code = code()))
            }
        } catch (exception: Exception){
            emit(Resource.error(data = null, exception = exception))
        }
    }

    fun submitQuizAnswer(body: JsonObject): LiveData<Resource<JsonObject?>> = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            repository.submitQuizAnswer(body).apply {
                emit(Resource.success(data = body(), code = code()))
            }
        } catch (exception: Exception){
            emit(Resource.error(data = null, exception = exception))
        }
    }
}