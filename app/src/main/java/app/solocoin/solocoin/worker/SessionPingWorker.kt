package app.solocoin.solocoin.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.solocoin.solocoin.repo.SolocoinRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.core.KoinComponent
import org.koin.core.inject

import retrofit2.HttpException

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class SessionPingWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams), KoinComponent {

    /*
     * Updates the 'status' and 'rewards' of the user through calls to api and shared prefs
     * in the SolocoinRespository class.
     */
    override suspend fun doWork(): Result {
        val repository: SolocoinRepository by inject()
        try {
            Log.wtf(SESSION_PING_WORKER, "Work request for sync is run")
            repository.doApiCall()
        } catch (e: HttpException) {
            return Result.failure()
        }
        return Result.success()
    }

    companion object {
        private const val SESSION_PING_WORKER: String = "SESSION_PING_WORKER"
    }
}