package app.solocoin.solocoin.repo

import java.io.IOException

/**
 * Created by Aditya Sonel on 05/05/20.
 */
class NoConnectivityException : IOException() {
    override val message: String
        get() = "No network available, please check your WiFi or Data connection"
}