package app.solocoin.solocoin.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import app.solocoin.solocoin.logging.AppExceptionHandler
import app.solocoin.solocoin.logging.FileLoggingTree
import timber.log.Timber

class MyApplication : Application(),Application.ActivityLifecycleCallbacks {

    private var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        Timber.plant(Timber.DebugTree())
        Timber.plant(FileLoggingTree())
        setUpCrashHandler()
    }

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
        currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity?) {
        currentActivity = activity
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        currentActivity = activity
    }

    private fun setUpCrashHandler() {
        val handler = Thread.UncaughtExceptionHandler { thread, ex -> Timber.e(ex) }
        val fabricExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(AppExceptionHandler(handler, fabricExceptionHandler, this))
    }

}