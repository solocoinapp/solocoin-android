package app.solocoin.solocoin.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo


/**
 * Created by Aditya Sonel on 22/04/20.
 */

class GlobalUtils {
    companion object {

        /**
         * Public function for detecting is network available or not
         */
        fun isNetworkAvailable(context: Context): Boolean {
            var isConnected = false
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            if (activeNetwork != null && activeNetwork.isConnected)
                isConnected = true
            return isConnected
        }

        /**
         * For starting desired activity as a new task,
         * intent_flags are added for clear back stack.
         * @param context: context of current activity
         * @param to: activity class need to start
         *
         * Note: call finish() method after this function execution
         */
        fun startActivityAsNewStack(context: Context, to: Class<*>) {
            val intent = Intent(context, to)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}