package com.shimadove.coronago.api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers("Content-Type: application/json")
    @POST("/mobile_login")
    Call<JsonObject> doMobileLogin(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("/mobile_sign_up")
    Call<JsonObject> doMobileSignup(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("/users/1")
    Call<JsonObject> doUserUpdate(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("/users/register_notification_token")
    Call<JsonObject> doRegisterNotificationToken(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("/sessions/start")
    Call<JsonObject> startSession(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("/sessions/end")
    Call<JsonObject> endSession(@Body JsonObject body);
}
