package app.solocoin.solocoin.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.solocoin.solocoin.R
import app.solocoin.solocoin.model.RedeemedRewards
import app.solocoin.solocoin.model.Reward
import app.solocoin.solocoin.ui.home.RewardRedeemActivity
import app.solocoin.solocoin.util.GlobalUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*

/**
 * Created by Karandeep Singh on 15/07/2020
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class AllRewardsAdapter(
        private val context: Activity,
        val rewardsArrayList: ArrayList<RedeemedRewards>
) :
        RecyclerView.Adapter<AllRewardsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_allrewards, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindRewards(context, rewardsArrayList[position])
    }

    override fun getItemCount() = rewardsArrayList.size

    class ViewHolder internal constructor(itemView: View) :
            RecyclerView.ViewHolder(itemView),
            View.OnClickListener {


        private var offername: TextView
        private var promocode: TextView


        init {
            offername = itemView.findViewById(R.id.offername)
            promocode = itemView.findViewById(R.id.promocode)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
//            mListener?.onClick(view, adapterPosition)
        }

        @SuppressLint("DefaultLocale")
        fun bindRewards(context: Activity, reward: RedeemedRewards) {
            reward.let {
//                updateImage(it)
                offername.text = it.offer_name
                promocode.text = "Promocode: "+it.coupon_code

            }
        }

//        private fun updateImage(reward: Reward) {
//            GlobalUtils.loadImageNetworkCacheVisibility(reward.companyLogoUrl, companyLogo)
//        }
    }

}