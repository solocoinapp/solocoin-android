package app.solocoin.solocoin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.solocoin.solocoin.app.SharedPref;

import java.util.Objects;

public class WalletFragment extends Fragment{

    public WalletFragment() {}

    private static String ARG_PARAM1 = WalletFragment.class.getSimpleName();
    private String wallet_balance;
    private SharedPref sharedPref;
    public static WalletFragment newInstance(String param1, String param2) {
        WalletFragment fragment = new WalletFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPref = SharedPref.getInstance(getContext());
        TextView balance;
        balance= Objects.requireNonNull(getView()).findViewById(R.id.tv_coins_count);
        float val = sharedPref.getWallet_balance();
        String bal=Float.toString(val);
        balance.setText(bal);
    }
}
