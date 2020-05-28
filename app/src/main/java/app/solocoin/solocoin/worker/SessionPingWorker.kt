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

//        /*
//         * Checking if fused location service is running.
//         * If running then ok else restart service.
//         */
//        statusFusedLocationService()
//        firstTime = false
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

//    private fun statusFusedLocationService() {
//        Log.d(TAG, "Checking fused location service is running or not.")
//        if(firstTime){
//            Log.d(TAG,"First time work manager started so skipping")
//            return
//        }
//        if (GlobalUtils.isServiceRunning(applicationContext,FusedLocationService::class.java)) {
//            Log.wtf(TAG, "Creating request to start fused location service")
//            GlobalUtils.notifyUser(
//                applicationContext,
//                HomeActivity::class.java,
//                "Important Update",
//                "Please allow location updates to the application by starting the App and continue receiving rewards."
//            )
//        } else {
//            Log.d(TAG, "Fused location service already running")
//        }
//    }

    companion object {
        private val TAG: String? = SessionPingWorker::class.java.simpleName
        private val API_CALL: String = SessionPingWorker::class.java.simpleName + " API_CALL"
//        /*
//         * Avoid notification for fused location service start on first time user open home activity
//         */
//        @JvmStatic private var firstTime: Boolean = true
    }
}