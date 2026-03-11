package app.solocoin.solocoin.api;

import timber.log.Timber;

public class RetrofitListener implements RetrofitResponseListener {

    @Override
    public void onSuccess(int code) {
        Timber.d("No issues. The code sent is: " + code);
    }
    @Override
    public void onFailure(int code) {
        Timber.d("Issue at backend. The code sent is: " + code);
    }
}
