package com.shimadove.coronago.api;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface APIService {
    @Headers("Content-Type: application/json")
    @POST("callbacks/mobile_login")
    Call<JsonObject> doMobileLogin(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("callbacks/mobile_sign_up")
    Call<JsonObject> doMobileSignup(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @GET("user/profile")
    Call<JsonObject> showUserData(@Header("Authorization") String authtoken);

    @Headers("Content-Type: application/json")
    @POST("user")
    Call<JsonObject> doUserUpdate(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("notification_tokens")
    Call<JsonObject> doRegisterNotificationToken(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @PUT("sessions/ping")
    Call<JsonObject> startSession(@Header("Authorization") String authtoken,@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("sessions/end")
    Call<JsonObject> endSession(@Body JsonObject body);
}
