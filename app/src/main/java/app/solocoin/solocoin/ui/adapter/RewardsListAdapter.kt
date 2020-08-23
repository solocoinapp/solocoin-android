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
import app.solocoin.solocoin.model.Reward
import app.solocoin.solocoin.ui.home.RewardRedeemActivity
import app.solocoin.solocoin.util.GlobalUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*

/**
 * Created by Saurav Gupta on 14/5/2020
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class RewardsListAdapter(
    private val context: Activity,
    val rewardsArrayList: ArrayList<Reward>
) :
    RecyclerView.Adapter<RewardsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_reward_card, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindRewards(context, rewardsArrayList[position])
    }

    override fun getItemCount() = rewardsArrayList.size

    class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private var companyLogo: ImageView
        private var costRupees: TextView
        private var companyName: TextView
        private var costCoins: TextView
        private var currency:TextView
        private var mListener: RecyclerViewClickListener? = null

        init {
            companyLogo = itemView.findViewById(R.id.company_logo)
            costRupees = itemView.findViewById(R.id.cost_rupees)
            companyName = itemView.findViewById(R.id.company_name)
            costCoins = itemView.findViewById(R.id.cost_coins)
            currency = itemView.findViewById(R.id.currency)
            itemView.setOnClickListener(this)

            companyLogo.visibility = View.GONE
        }

        override fun onClick(view: View) {
            mListener?.onClick(view, adapterPosition)
        }

        @SuppressLint("DefaultLocale")
        fun bindRewards(context: Activity, reward: Reward) {
            reward.let {
                updateImage(it)
                companyName.text = it.companyName.capitalize()
                costCoins.text = ("${it.costCoins} coins")
                costRupees.text = it.costRupees
                currency.text=it.currency
                mListener = object : RecyclerViewClickListener {
                    override fun onClick(view: View?, position: Int) {
                        val intent = Intent(
                            context,
                            RewardRedeemActivity::class.java
                        )
                        if(it.category!=null) {
                            intent.putExtra("EXTRA_INFO", it)
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }

        private fun updateImage(reward: Reward) {
            GlobalUtils.loadImageNetworkCacheVisibility(reward.companyLogoUrl, companyLogo)
        }
    }

}