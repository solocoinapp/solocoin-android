package com.shimadove.coronago;

import android.content.Context;

import timber.log.Timber;

public class RetrofitListener implements RetrofitResponseListener {

    @Override
    public void onSuccess() {
        Timber.d("No issues.");
    }
    @Override
    public void onFailure() {
        Timber.d("Issue at backend");
    }
}
