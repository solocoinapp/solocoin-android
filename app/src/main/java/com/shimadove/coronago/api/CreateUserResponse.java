package com.shimadove.coronago.api;

import com.google.gson.annotations.SerializedName;

public class CreateUserResponse {
    @SerializedName("auth_token")
    public String auth_token;

    @SerializedName("id")
    public String id;

    public CreateUserResponse(String auth_token, String id) {
        this.auth_token = auth_token;
        this.id = id;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
