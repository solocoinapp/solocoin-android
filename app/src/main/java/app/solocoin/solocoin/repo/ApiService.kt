package app.solocoin.solocoin.repo

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Aditya Sonel on 22/04/20.
 */

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("callbacks/mobile_login")
    fun mobileLogin(@Body body: JsonObject): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("callbacks/mobile_sign_up")
    fun mobileSignUp(@Body body: JsonObject): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("user/profile")
    fun userData(@Header("Authorization") authToken: String): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @PATCH("user")
    fun userUpdate(@Header("Authorization") authToken: String, @Body body: JsonObject): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("notification_tokens")
    fun registerNotificationToken(@Body body: JsonObject): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("sessions/ping")
    fun pingSession(@Header("Authorization") authToken: String, @Body body: JsonObject): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("user/profile")
    fun getProfile(@Header("Authorization") authToken: String): Response<JsonObject>

    companion object {
        const val BASE_URL = ""
    }
}