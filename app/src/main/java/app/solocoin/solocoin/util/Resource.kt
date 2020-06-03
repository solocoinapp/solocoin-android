package app.solocoin.solocoin.util

import app.solocoin.solocoin.util.enums.Status
import com.google.gson.JsonObject
import retrofit2.Response

/**
 * Created by Aditya Sonel on 26/04/20.
 */

data class Resource<out T>(
    val status: Status,
    val data: T,
    val response: Response<JsonObject>?,
    val code: Int?,
    val exception: Exception?
) {
    companion object {
        fun <T> success(data: T, code: Int): Resource<T> = Resource(
            status = Status.SUCCESS,
            data = data,
            code = code,
            response = null,
            exception = null
        )

        fun <T> success(data: T, code: Int, response: Response<JsonObject>): Resource<T> = Resource(
            status = Status.SUCCESS,
            data = data,
            code = code,
            response = response,
            exception = null
        )

        fun <T> error(data: T, exception: Exception): Resource<T> = Resource(
            status = Status.ERROR,
            data = data,
            code = null,
            response = null,
            exception = exception
        )

        fun <T> loading(data: T): Resource<T> = Resource(
            status = Status.LOADING,
            data = data,
            code = null,
            response = null,
            exception = null
        )
    }
}