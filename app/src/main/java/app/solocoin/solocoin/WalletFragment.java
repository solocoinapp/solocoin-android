package app.solocoin.solocoin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.solocoin.solocoin.adapter.RewardsAdapter;
import app.solocoin.solocoin.api.APIClient;
import app.solocoin.solocoin.api.APIService;
import app.solocoin.solocoin.app.SharedPref;
import app.solocoin.solocoin.model.Reward;
import app.solocoin.solocoin.viewmodel.RewardsViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletFragment extends Fragment {

    private RewardsViewModel viewModel;
    private Activity context;
    private RecyclerView recyclerView;
    private RewardsAdapter mAdapter;

    static WalletFragment newInstance() {
        return new WalletFragment();
    }

    private Observer<ArrayList<Reward>> rewardsListUpdateObserver = new Observer<ArrayList<Reward>>() {
        @Override
        public void onChanged(ArrayList<Reward> rewardsArrayList) {
            mAdapter = new RewardsAdapter(context, rewardsArrayList);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPref sharedPref = SharedPref.getInstance(getContext());
        TextView balanceTextView = view.findViewById(R.id.tv_coins_count);

        APIService apiService = APIClient.getRetrofitInstance(getContext()).create(APIService.class);
        apiService.showUserData(sharedPref.getAuthToken()).enqueue(new Callback<JsonObject>() {
            @SuppressLint({"SetTextI18n", "LogNotTimber"})
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                JsonObject resp = response.body();
                if (resp != null) {
                    String balance = resp.get("wallet_balance").getAsString();
                    balanceTextView.setText(balance);
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call,@NonNull Throwable t) {
                balanceTextView.setText("0.0");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        context = getActivity();

        recyclerView = view.findViewById(R.id.rewards_recycler_view);
        viewModel = new ViewModelProvider(this).get(RewardsViewModel.class);
        viewModel.getOfferMutableLiveData().observe(getViewLifecycleOwner(), rewardsListUpdateObserver);

        return view;
    }
}
