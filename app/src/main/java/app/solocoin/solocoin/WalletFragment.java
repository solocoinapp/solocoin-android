package app.solocoin.solocoin;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import app.solocoin.solocoin.adapter.RewardsAdapter;
import app.solocoin.solocoin.model.Reward;
import app.solocoin.solocoin.viewmodel.RewardsViewModel;

public class WalletFragment extends Fragment {

    private RewardsViewModel viewModel;
    private Activity context;
    private RecyclerView recyclerView;
    private TextView balanceTextView;
    private RewardsAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    static WalletFragment newInstance() {
        return new WalletFragment();
    }
    private Observer<ArrayList<Reward>> rewardsListUpdateObserver = new Observer<ArrayList<Reward>>() {
        @Override
        public void onChanged(ArrayList<Reward> rewardsArrayList) {
            mAdapter = new RewardsAdapter(context, rewardsArrayList);
            recyclerView.setAdapter(mAdapter);
        }
    };
    private Observer<String> walletUpdateObserver = new Observer<String>() {
        @Override
        public void onChanged(String s) {
            balanceTextView.setText(s);
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateWallet();
        ;
        updateRewards();
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            updateWallet();
            updateRewards();
            swipeRefreshLayout.setRefreshing(false);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        context = getActivity();

        balanceTextView = view.findViewById(R.id.tv_coins_count);
        recyclerView = view.findViewById(R.id.rewards_recycler_view);
        viewModel = new ViewModelProvider(this).get(RewardsViewModel.class);
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);

        return view;
    }

    private void updateWallet() {
        viewModel.getWalletAmount().observe(getViewLifecycleOwner(), walletUpdateObserver);
    }

    private void updateRewards() {
        viewModel.getRewards().observe(getViewLifecycleOwner(), rewardsListUpdateObserver);
    }
}
