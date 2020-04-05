package app.solocoin.solocoin.api;

public interface RetrofitResponseListener {
    void onSuccess(int code);
    void onFailure(int code);
}
