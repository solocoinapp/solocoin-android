package app.solocoin.solocoin.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import app.solocoin.solocoin.api.APIClient;
import app.solocoin.solocoin.api.APIService;
import app.solocoin.solocoin.app.SharedPref;
import app.solocoin.solocoin.model.Reward;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Saurav Gupta on 14/5/2020
 */
public class RewardsViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<Reward>> rewardsLiveData;
    private MutableLiveData<String> coinsInWallet;

    public RewardsViewModel(Application application) {
        super(application);
        rewardsLiveData = new MutableLiveData<>();
        coinsInWallet = new MutableLiveData<>();
    }

    private void fetchWalletData() {

        SharedPref sharedPref = SharedPref.getInstance(getApplication());
        APIService apiService = APIClient.getRetrofitInstance(getApplication()).create(APIService.class);
        apiService.showUserData(sharedPref.getAuthToken()).enqueue(new Callback<JsonObject>() {
            @SuppressLint({"SetTextI18n", "LogNotTimber"})
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                JsonObject resp = response.body();
                if (resp != null) {
                    String balance = resp.get("wallet_balance").getAsString();
                    coinsInWallet.setValue(balance);
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                coinsInWallet.setValue("0.0");
            }
        });
    }

    private void fetchRewardsData() {

        Reward dummy = new Reward();
        dummy.setCostCoins("200 Coins");
        dummy.setCostRupees("1000");
        dummy.setCompanyName("SoloCoin");
        dummy.setOfferName("YouTube Premium Subscription for 6 Months");
        dummy.setOfferExtraDetails("** Reward is valid for only Premium User");
        ArrayList<String> offerDetails = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            offerDetails.add("\u2022 Reward Detail " + (i + 1) + ".");
        }
        dummy.setOfferDetails(offerDetails);

        ArrayList<Reward> rewardsArrayList = new ArrayList<>();
        rewardsArrayList.add(dummy);
        rewardsArrayList.add(dummy);
        rewardsArrayList.add(dummy);
        rewardsArrayList.add(dummy);
        rewardsArrayList.add(dummy);
        rewardsArrayList.add(dummy);
        rewardsArrayList.add(dummy);
        rewardsArrayList.add(dummy);
        rewardsLiveData.setValue(rewardsArrayList);
    }

    public MutableLiveData<ArrayList<Reward>> getRewards() {
        fetchRewardsData();
        return rewardsLiveData;
    }

    public MutableLiveData<String> getWalletAmount() {
        fetchWalletData();
        return coinsInWallet;
    }

}
