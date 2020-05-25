package app.solocoin.solocoin.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.model.SessionPingRequest
import app.solocoin.solocoin.repo.SolocoinRepository
import app.solocoin.solocoin.services.FusedLocationService
import app.solocoin.solocoin.util.GlobalUtils
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.*
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

        Log.wtf(TAG, "Initiating the work")

        /*
         * Checking if fused location service is running.
         * If running then ok else restart service.
         */
        statusFusedLocationService()

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

        Log.wtf(API_CALL, "Calling api")

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
                // Create an explicit intent for an Activity in your app
                val intent = Intent(applicationContext, HomeActivity::class.java)
                val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

                val builder = NotificationCompat.Builder(applicationContext, "1")
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle("Important Update")
                    .setContentText("Your network request was unable to be processed. Please check Internet settings.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

                with(NotificationManagerCompat.from(applicationContext)) {
                    // notificationId is a unique int for each notification that you must define
                    notify(1, builder.build())
                }

            }
        })
        return result!!
    }

    private fun statusFusedLocationService() {

        Log.wtf(TAG, "Checking fused location service is running or not.")

        if (!FusedLocationService.isRunning) {
            Log.wtf(TAG, "Starting the fused location service.")
            val intent = Intent().apply {
                setClass(applicationContext, FusedLocationService::class.java)
                action = FusedLocationService::class.simpleName
                flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            }
            applicationScope.launch {
                applicationContext.startService(intent)
            }
        } else {
            Log.wtf(TAG, "Fused location service already running")
        }
    }

    companion object {
        private val TAG: String? = SessionPingWorker::class.simpleName
        private val API_CALL: String = SessionPingWorker::class.simpleName + " API_CALL"
        private val applicationScope = CoroutineScope(Dispatchers.Default)
    }
}