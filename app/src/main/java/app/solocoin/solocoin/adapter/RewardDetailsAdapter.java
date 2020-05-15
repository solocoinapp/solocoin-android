package app.solocoin.solocoin.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import app.solocoin.solocoin.R;
import app.solocoin.solocoin.model.Reward;

public class RewardDetailsAdapter extends RecyclerView.Adapter<RewardDetailsAdapter.ViewHolder> {

    private Activity context;
    private ArrayList<Reward> rewardArrayList;

    public RewardDetailsAdapter(Activity context, ArrayList<Reward> rewardArrayList) {
        this.context = context;
        this.rewardArrayList = rewardArrayList;
    }

    @NonNull
    @Override
    public RewardDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.reward_details_layout, parent, false);
        return new ViewHolder(rootView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RewardDetailsAdapter.ViewHolder holder, int position) {
        Reward reward = rewardArrayList.get(position);

        updateImage(holder, reward);
        holder.coinsAmt.setText(reward.getCostCoins());
        holder.extraTnc.setText(reward.getOfferExtraDetails());
        holder.offerName.setText(reward.getOfferName());
        updateOfferDetails(holder, reward);
    }

    private void updateOfferDetails(ViewHolder viewHolder, Reward reward) {
        for (String x : reward.getOfferDetails()) {
            TextView offerDetails = new TextView(viewHolder.tnc.getContext());
            offerDetails.setText(x);
            offerDetails.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            offerDetails.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            offerDetails.setTypeface(ResourcesCompat.getFont(context, R.font.poppins));
            viewHolder.tnc.addView(offerDetails);
        }
    }

    private void updateImage(ViewHolder viewHolder, Reward reward) {
        //TODO: add code to update image from api or download the image
        viewHolder.offerImage_1.setImageResource(R.drawable.reward);
        viewHolder.offerImage_2.setImageResource(R.drawable.reward);
    }

    @Override
    public int getItemCount() {
        return rewardArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView offerImage_1;
        ImageView offerImage_2;
        TextView extraTnc;
        TextView offerName;
        TextView coinsAmt;
        LinearLayout tnc;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            offerImage_1 = itemView.findViewById(R.id.offer_image_1);
            offerImage_2 = itemView.findViewById(R.id.reward_image_2);
            extraTnc = itemView.findViewById(R.id.extra_tnc);
            offerName = itemView.findViewById(R.id.offer_name);
            coinsAmt = itemView.findViewById(R.id.coins_amt);
            tnc = itemView.findViewById(R.id.tnc);
        }
    }
}
