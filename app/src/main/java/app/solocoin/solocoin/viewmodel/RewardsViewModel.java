package app.solocoin.solocoin.viewmodel;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import app.solocoin.solocoin.model.Reward;

public class RewardsViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Reward>> rewardsLiveData;
    private ArrayList<Reward> rewardsArrayList;

    public RewardsViewModel() {
        rewardsLiveData = new MutableLiveData<>();
        loadData();
    }

    private void loadData() {
        fetchApiData();
        rewardsLiveData.setValue(rewardsArrayList);
    }

    private void fetchApiData() {

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

        rewardsArrayList = new ArrayList<>();
        rewardsArrayList.add(dummy);
        rewardsArrayList.add(dummy);
        rewardsArrayList.add(dummy);
        rewardsArrayList.add(dummy);
        rewardsArrayList.add(dummy);
        rewardsArrayList.add(dummy);
        rewardsArrayList.add(dummy);
        rewardsArrayList.add(dummy);

    }

    public MutableLiveData<ArrayList<Reward>> getOfferMutableLiveData() {
        return rewardsLiveData;
    }

}
