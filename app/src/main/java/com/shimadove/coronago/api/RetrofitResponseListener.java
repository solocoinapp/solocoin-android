package com.shimadove.coronago.api;

public interface RetrofitResponseListener {
    void onSuccess(int code);
    void onFailure(int code);
}
