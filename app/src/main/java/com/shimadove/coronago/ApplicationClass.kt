package com.shimadove.coronago

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.shimadove.coronago.logging.AppExceptionHandler
import com.shimadove.coronago.logging.FileLoggingTree
import timber.log.Timber

class ApplicationClass : Application(),Application.ActivityLifecycleCallbacks {

    var currentActivity: Activity? = null

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