package app.solocoin.solocoin.di

import android.content.Context
import android.util.Log
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by Saurav Gupta on 25/05/2020
 */

/*
 * Network Interceptor to add Cache control header to response received from server (API)
 * Currently no Cache Control is available at server side.
 * Still that case is taken into consideration.
 */
object CachingModule : Interceptor {

    private val TAG = CachingModule::class.simpleName

    override fun intercept(chain: Interceptor.Chain): Response {
        chain.proceed(chain.request()).apply {

            /* Check whether cache control is enable by server or not.
             * If Not then add cache control to network interceptor
             * Else return the response from the server.
             */
            val responseCC = header("Cache-Control")
            if (responseCC == null ||
                responseCC.contains("no-store") ||
                responseCC.contains("no-cache") ||
                responseCC.contains("must-revalidate") ||
                responseCC.contains("max-stale=0")
            ) {
                Log.d(TAG, "Setting up cache for response")
                val cacheControl = CacheControl.Builder()
                    .maxAge(2, TimeUnit.MINUTES)  // Cache Control period is set to 5 minutes
                    .build()
                return newBuilder().header("Cache-Control", cacheControl.toString()).build()
            }
            return this
        }
    }

    private const val DISK_CACHE_SIZE: Long = 15 * 1024 * 1024  // 10 MB cache allotted for app
    val mCache = { context: Context ->
        try {
            Cache(
                File(context.cacheDir, "http-cache"),
                DISK_CACHE_SIZE
            )
        } catch (e: Exception) {
            Log.wtf(TAG, "Unable to create cache!")
            null
        }
    }
}