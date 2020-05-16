package app.solocoin.solocoin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.solocoin.solocoin.adapter.RewardDetailsAdapter;
import app.solocoin.solocoin.model.Reward;

/**
 * Created by Saurav Gupta on 14/5/2020
 */
public class RewardDetailsActivity extends AppCompatActivity {

    private RewardDetailsActivity context;
    private RecyclerView recyclerView;
    private RewardDetailsAdapter mAdapter;
    private ArrayList<Reward> rewardArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_details);
        context = this;

        rewardArrayList = new ArrayList<>();
        rewardArrayList.add(Objects.requireNonNull(getIntent().getExtras()).getParcelable("EXTRA_INFO"));
        recyclerView = findViewById(R.id.offer_recycler_view);
        mAdapter = new RewardDetailsAdapter(context, rewardArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        Button claimOffer = findViewById(R.id.claim_offer);
        claimOffer.setOnClickListener(view -> {
            Toast.makeText(context, "You have claimed the offer !!", Toast.LENGTH_LONG).show();
            // TODO : Implement logic to provide user's requested offer
        });

        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(view -> onSupportNavigateUp());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
