package app.solocoin.solocoin.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.solocoin.solocoin.R
import app.solocoin.solocoin.model.Reward
import com.squareup.picasso.Picasso
import java.util.*

/**
 * Created by Saurav Gupta on 14/5/2020
 */
class RewardsAdapter(
    private val context: Activity,
    private val rewardsArrayList: ArrayList<Reward>
) :
    RecyclerView.Adapter<RewardsAdapter.ViewHolder>() {

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

        var companyLogo: ImageView? = null
        var costRupees: TextView? = null
        var companyName: TextView? = null
        var costCoins: TextView? = null
        var mListener: RecyclerViewClickListener? = null

        init {
            companyLogo = itemView.findViewById(R.id.company_logo)
            costRupees = itemView.findViewById(R.id.cost_rupees)
            companyName = itemView.findViewById(R.id.company_name)
            costCoins = itemView.findViewById(R.id.cost_coins)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            mListener?.onClick(view, adapterPosition)
        }

        fun bindRewards(context: Activity, reward: Reward) {
            reward.let {
                updateImage(it)
                companyName?.text = it.companyName
                costCoins?.text = it.costCoins
                costRupees?.text = it.costRupees
                mListener = object : RecyclerViewClickListener {
                    override fun onClick(view: View?, position: Int) {
                        val intent = android.content.Intent(
                            context,
                            app.solocoin.solocoin.ui.home.RewardDetailsActivity::class.java
                        )
                        intent.putExtra("EXTRA_INFO", it)
                        context.startActivity(intent)
                    }
                }
            }
        }

        private fun updateImage(reward: Reward) {
            try {
                Picasso.get().load(reward.companyLogoUrl!!).into(companyLogo)
            } catch (e: Exception){

            }
        }
    }

}