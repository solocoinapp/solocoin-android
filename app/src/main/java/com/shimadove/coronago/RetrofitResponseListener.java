package com.shimadove.coronago;

public interface RetrofitResponseListener {
    void onSuccess(int code);
    void onFailure(int code);
}
