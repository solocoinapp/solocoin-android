package app.solocoin.solocoin.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import app.solocoin.solocoin.R;
import app.solocoin.solocoin.RewardDetailsActivity;
import app.solocoin.solocoin.model.Reward;

/**
 * Created by Saurav Gupta on 14/5/2020
 */
public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.ViewHolder> {

    private ArrayList<Reward> rewardsArrayList;
    private Activity context;

    public RewardsAdapter(Activity context, ArrayList<Reward> rewardsArrayList) {
        this.context = context;
        this.rewardsArrayList = rewardsArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.reward_card_layout, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reward reward = rewardsArrayList.get(holder.getAdapterPosition());

        updateImage(holder, reward);
        holder.companyName.setText(reward.getCompanyName());
        holder.costCoins.setText(reward.getCostCoins());
        holder.costRupees.setText(reward.getCostRupees());
        holder.setItemClickListener((view, position1) -> {
            Intent intent = new Intent(context, RewardDetailsActivity.class);
            intent.putExtra("EXTRA_INFO", rewardsArrayList.get(position1));
            context.startActivity(intent);
        });
    }

    private void updateImage(ViewHolder viewHolder, Reward reward) {
        //TODO: add code to update image from api or download the image
        viewHolder.companyLogo.setImageResource(R.drawable.logo);
    }

    @Override
    public int getItemCount() {
        return rewardsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView companyLogo;
        TextView costRupees;
        TextView companyName;
        TextView costCoins;
        private RecyclerViewClickListener mListener;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            companyLogo = itemView.findViewById(R.id.company_logo);
            companyName = itemView.findViewById(R.id.company_name);
            costCoins = itemView.findViewById(R.id.cost_coins);
            costRupees = itemView.findViewById(R.id.cost_rupees);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }

        void setItemClickListener(RecyclerViewClickListener mListener) {
            this.mListener = mListener;
        }
    }
}
