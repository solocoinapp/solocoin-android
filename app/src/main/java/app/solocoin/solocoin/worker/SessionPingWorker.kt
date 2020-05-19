package app.solocoin.solocoin.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.solocoin.solocoin.model.SessionPingRequest
import app.solocoin.solocoin.repo.SolocoinRepository
import app.solocoin.solocoin.util.GlobalUtils
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class SessionPingWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams), KoinComponent {

    /*
     * Updates the 'status' and 'rewards' of the user through calls to api and shared prefs
     * in the SolocoinRespository class.
     */

    private val repository: SolocoinRepository by inject()

    override suspend fun doWork(): Result {

        Log.wtf(SESSION_PING_WORKER, "Initiating the work")

        val sessionType: String? = GlobalUtils.getSessionType()

        if (sessionType != null) {
            val body: JsonObject =
                JsonParser().parse(SessionPingRequest(sessionType).toString()).asJsonObject
            return doApiCall(body)
        }
        return Result.retry()
    }

    private fun doApiCall(body: JsonObject): Result {

        Log.wtf(SESSION_PING_API_CALL, "Calling api")

        var result: Result? = null
        var rewards: String? = null
        var status: String? = null

        val call: Call<JsonObject> = repository.pingSession(body)
        call.enqueue(object : Callback<JsonObject?> {

            override fun onResponse(
                call: Call<JsonObject?>,
                response: Response<JsonObject?>
            ) {
                val resp = response.body()
                if (resp != null) {
                    status = resp["status"].asString
                    rewards = resp["rewards"].asString
                }
                result = Result.success()
            }

            override fun onFailure(
                call: Call<JsonObject?>,
                t: Throwable
            ) {
                result = Result.failure()
            }
        })

        repository.rewards(rewards!!)
        repository.status(status!!)
        return result!!
    }

    companion object {
        private const val SESSION_PING_WORKER: String = "SESSION_PING_WORKER"
        private const val SESSION_PING_API_CALL: String = "SESSION_PING_API_CALL"
    }
}