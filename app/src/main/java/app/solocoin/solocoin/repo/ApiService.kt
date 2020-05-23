package app.solocoin.solocoin.repo

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Aditya Sonel on 22/04/20.
 */

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("callbacks/mobile_login")
    suspend fun mobileLogin(@Body body: JsonObject): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("callbacks/mobile_sign_up")
    suspend fun mobileSignUp(@Body body: JsonObject): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("user/profile")
    suspend fun userData(@Header("Authorization") authToken: String): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @PATCH("user")
    suspend fun userUpdate(@Header("Authorization") authToken: String, @Body body: JsonObject): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("notification_tokens")
    fun registerNotificationToken(@Body body: JsonObject): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("sessions/ping")
    fun pingSession(
        @Header("Authorization") authToken: String,
        @Body body: JsonObject
    ): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("user/profile")
    fun getProfile(@Header("Authorization") authToken: String): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("rewards")
    fun rewards(@Header("Authorization") authToken: String): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("redeem_rewards")
    fun redeemRewards(@Header("Authorization") authToken: String, @Body body: JsonObject): Call<JsonObject>
}