package app.solocoin.solocoin.app;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import app.solocoin.solocoin.api.APIClient;
import app.solocoin.solocoin.api.APIService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Wallet {
    public Wallet() { }

    private float wallet_balance;
    private SharedPref sharedPref;

    public void Updatebalance(Context context) {
        APIService service = APIClient.getRetrofitInstance(context).create(APIService.class);
        //FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //String firebaseUid = currentUser.getUid();
        sharedPref = SharedPref.getInstance(context);
        //JsonObject userbody = new JsonObject();
        //userbody.addProperty("auth_token", firebaseUid);
        service.showUserData(sharedPref.getAuthtoken()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject userdata = response.body();
                if (userdata==null){
                    Toast.makeText(context,"Some error occured. Unable to get balance.",Toast.LENGTH_SHORT).show();
                }
                else {
                    wallet_balance = userdata.get("wallet_balance").getAsFloat();
                    Log.d("yoyo, wallet", "onResponse: wallet_balance is " + wallet_balance);
                    sharedPref.setWallet_balance(wallet_balance);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context,"Some error occured. Unable to get balance.",Toast.LENGTH_SHORT).show();
            }
        });

    }
}