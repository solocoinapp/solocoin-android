package app.solocoin.solocoin.util

import app.solocoin.solocoin.util.enums.Status

/**
 * Created by Aditya Sonel on 26/04/20.
 */

data class Resource<out T>(val status: Status, val data: T, val code: Int?, val message: String?) {
    companion object {
        fun <T> success(data: T, code: Int): Resource<T> = Resource(status = Status.SUCCESS, code = code, data = data, message = null)
        fun <T> error(data: T, message: String): Resource<T> = Resource(status = Status.ERROR, code = null, data = data, message = message)
        fun <T> loading(data: T): Resource<T> = Resource(status = Status.LOADING, code = null, data = data, message = null)
    }
}