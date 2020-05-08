package app.solocoin.solocoin.ui.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.work.*
import app.solocoin.solocoin.worker.SessionPingWorker
import kotlinx.coroutines.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class HomeActivityViewModel(application: Application) : AndroidViewModel(application) {

    /*
     * Generates new periodic work request with unique identifier name 'SESSION_PING_MANAGER'
     */
    private fun createWorkRequest() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED).build()
        val periodicWorkRequest =
            PeriodicWorkRequest.Builder(SessionPingWorker::class.java, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(getApplication()).enqueueUniquePeriodicWork(
            SESSION_PING_REQUEST,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }


    private fun getStateOfWork(): WorkInfo.State {
        return try {
            if (WorkManager.getInstance(getApplication())
                    .getWorkInfosForUniqueWork(SESSION_PING_MANAGER)
                    .get().size > 0
            ) {
                WorkManager.getInstance(getApplication())
                    .getWorkInfosForUniqueWork(SESSION_PING_MANAGER).get()[0].state
            } else {
                WorkInfo.State.CANCELLED
            }
        } catch (e: ExecutionException) {
            e.printStackTrace()
            WorkInfo.State.CANCELLED
        } catch (e: InterruptedException) {
            e.printStackTrace()
            WorkInfo.State.CANCELLED
        }
    }

    /*
     * Once user reaches 'HomeActivity' worker manager service is executed through this function
     * in current view model. The state of the work request is checked using its unique identifier
     * 'SESSION_PING_MANAGER'. In case, work request is already enqueued then new work request is
     * not generated else new work request is created.
     */
    fun startSessionPingManager() {
        if (getStateOfWork() != WorkInfo.State.ENQUEUED && getStateOfWork() != WorkInfo.State.RUNNING) {
            applicationScope.launch {
                createWorkRequest()
            }
            Log.wtf(SESSION_PING_MANAGER, ": Server Started !!")
        } else {
            Log.wtf(SESSION_PING_MANAGER, ": Server Already Working !!")
        }
    }

    companion object {
        private const val SESSION_PING_REQUEST = "SOLOCOIN_API_V1"
        private const val SESSION_PING_MANAGER: String = "SESSION_PING_MANAGER"
        private val applicationScope = CoroutineScope(Dispatchers.Default)
    }
}