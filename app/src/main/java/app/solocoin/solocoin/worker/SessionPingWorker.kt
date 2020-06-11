package app.solocoin.solocoin.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
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
import java.util.*

/**
 * Created by Saurav Gupta on 07/05/20
 */

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class SessionPingWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams), KoinComponent {

    private val repository: SolocoinRepository by inject()

    /*
     * Updates the 'status' and 'rewards' of the user through calls to api and shared prefs
     * in the SolocoinRespository class.
     */
    override suspend fun doWork(): Result {
        Log.wtf(TAG, "Initiating the work")

        // Checking if the period is valid
        sharedPrefs?.let {
            if ((it.recentNotifTime + 30 * 60 * 1000 <= Calendar.getInstance().get(
                    Calendar.MILLISECOND
                ).toLong()) && it.recentCheckTime < it.recentNotifTime
            ) {
                it.periodValid = false
            }
        }

//        /*
//         * Checking if fused location service is running.
//         * If running then ok else restart service.
//         */
//        statusFusedLocationService()
//        firstTime = false

        // Check user is within 20 meters radius or not
        var sessionType: String? = GlobalUtils.getSessionType(applicationContext)

        // Check to detect user is cheating or not
        val legalChecker = LegalChecker(applicationContext)
        if (legalChecker.isCheating()) {
            sessionType = "away"
        }

        // Check to detect user presence
        sharedPrefs?.let{
            if(!(it.periodValid)){
                sessionType = "away"
            }
        }

        Log.wtf(TAG, "Your session type is : $sessionType")

        sessionType?.let {
            val body: JsonObject =
                JsonParser().parse(SessionPingRequest(sessionType!!).toString()).asJsonObject
            return doApiCall(body)
        }
        return Result.retry()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private suspend fun doApiCall(body: JsonObject): Result {
        Log.wtf(API_CALL, "Calling api")

        try {
            val response = repository.pingSession(body)
            Log.wtf(TAG, "sending request")
            if (response.isSuccessful) {
                response.body()?.let {
                    sharedPrefs?.status = it["status"]?.asString
                    sharedPrefs?.rewards = it["rewards"]?.asString
                }
                return Result.success()
            } else {
                val pendingIntent = PendingIntent.getActivity(
                    applicationContext,
                    0,
                    Intent(applicationContext, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    },
                    0
                )
                GlobalUtils.notifyUser(
                    notificationId,
                    channelId,
                    applicationContext,
                    pendingIntent,
                    NotificationManager.IMPORTANCE_HIGH,
                    "Internet Connectivity Issue",
                    "Your network request was unable to be processed. Please check Internet settings."
                )
                return Result.retry()
            }

        } catch (e: Exception) {
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                Intent(applicationContext, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                },
                0
            )
            GlobalUtils.notifyUser(
                notificationId,
                channelId,
                applicationContext,
                pendingIntent,
                NotificationManager.IMPORTANCE_HIGH,
                "Internet Connectivity Issue",
                "Your network request was unable to be processed. Please check Internet settings."
            )
            return Result.retry()
        }
    }

//    private fun statusFusedLocationService() {
//        //Log.d(TAG, "Checking fused location service is running or not.")
//        if(firstTime){
//            //Log.d(TAG,"First time work manager started so skipping")
//            return
//        }
//        if (GlobalUtils.isServiceRunning(applicationContext,FusedLocationService::class.java)) {
//            //Log.wtf(TAG, "Creating request to start fused location service")
//            GlobalUtils.notifyUser(
//                applicationContext,
//                HomeActivity::class.java,
//                "Important Update",
//                "Please allow location updates to the application by starting the App and continue receiving rewards."
//            )
//        } else {
//            //Log.d(TAG, "Fused location service already running")
//        }
//    }

    companion object {
        private val TAG: String? = SessionPingWorker::class.java.simpleName
        private val API_CALL: String = SessionPingWorker::class.java.simpleName + " API_CALL"
        private const val notificationId = 323
        private const val channelId = "1"
//        /*
//         * Avoid notification for fused location service start on first time user open home activity
//         */
//        @JvmStatic private var firstTime: Boolean = true
    }
}