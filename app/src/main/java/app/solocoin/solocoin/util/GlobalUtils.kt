package app.solocoin.solocoin.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo


/**
 * Created by Aditya Sonel on 22/04/20.
 */

fun isNetworkAvailable(context: Context): Boolean {
    var isConnected: Boolean = false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
    if (activeNetwork != null && activeNetwork.isConnected)
        isConnected = true
    return isConnected
}

fun startActivityAsNewStack(context: Context, to: Class<*>) {
    val intent = Intent(context, to)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}