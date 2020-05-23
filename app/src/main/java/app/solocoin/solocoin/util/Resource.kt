package app.solocoin.solocoin.util

import app.solocoin.solocoin.util.enums.Status
import java.lang.Exception

/**
 * Created by Aditya Sonel on 26/04/20.
 */

data class Resource<out T>(val status: Status, val data: T, val code: Int?, val exception: Exception?) {
    companion object {
        fun <T> success(data: T, code: Int): Resource<T> = Resource(status = Status.SUCCESS, code = code, data = data, exception = null)
        fun <T> error(data: T, exception: Exception): Resource<T> = Resource(status = Status.ERROR, code = null, data = data, exception = exception)
        fun <T> loading(data: T): Resource<T> = Resource(status = Status.LOADING, code = null, data = data, exception = null)
    }
}