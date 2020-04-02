package com.shimadove.coronago;

import android.content.Context;

import com.google.gson.JsonObject;
import com.shimadove.coronago.api.APIClient;
import com.shimadove.coronago.api.APIService;
import com.shimadove.coronago.app.SharedPref;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Wallet {
    public Wallet() { }

    private float wallet_balance;
    private SharedPref sharedPref;

    public void Updatebalance(Context context) {
        APIService service = APIClient.getRetrofitInstance(context).create(APIService.class);
        sharedPref = SharedPref.getInstance(context);
        JSONObject userbody = new JSONObject();
        service.showUserData(userbody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject userdata = response.body();
                assert userdata != null;
                wallet_balance = userdata.get("wallet_balance").getAsFloat();
                sharedPref.setWallet_balance(wallet_balance);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //To decide what to do here
            }
        });

    }
}