package app.solocoin.solocoin.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.model.SessionPingRequest
import app.solocoin.solocoin.repo.SolocoinRepository
import app.solocoin.solocoin.ui.home.HomeActivity
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

/**
 * Created by Saurav Gupta on 07/05/20
 */

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class SessionPingWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams), KoinComponent {

    private val repository: SolocoinRepository by inject()

    /*
     * Updates the 'status' and 'rewards' of the user through calls to api and shared prefs
     * in the SolocoinRespository class.
     */
    override fun doWork(): Result {
        Log.d(TAG, "Initiating the work")

        /*
         * Pinging the session type to backend
         */
        val sessionType: String? = GlobalUtils.getSessionType(applicationContext)
        sessionType?.let {
            val body: JsonObject =
                JsonParser().parse(SessionPingRequest(sessionType).toString()).asJsonObject
            Log.wtf(TAG, body.toString())
            return doApiCall(body)
        }
        return Result.retry()
    }

    private fun doApiCall(body: JsonObject): Result {
        Log.d(API_CALL, "Calling api")

        var result: Result? = null
        val call: Call<JsonObject> = repository.pingSession(body)
        call.enqueue(object : Callback<JsonObject?> {

            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                val resp = response.body()
                resp?.let {
                    sharedPrefs?.status = resp["status"].asString
                    sharedPrefs?.rewards = resp["rewards"].asString
                }
                result = Result.success()
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                result = Result.failure()
                GlobalUtils.notifyUser(
                    applicationContext,
                    HomeActivity::class.java,
                    "Important Update",
                    "Your network request was unable to be processed. Please check Internet settings."
                )
            }
        })
        return result!!
    }

    override fun onStopped() {
        super.onStopped()
        Log.wtf(TAG, "Stopping worker.")
    }

    companion object {
        private val TAG: String? = SessionPingWorker::class.java.simpleName
        private val API_CALL: String = SessionPingWorker::class.java.simpleName + " API_CALL"
    }
}