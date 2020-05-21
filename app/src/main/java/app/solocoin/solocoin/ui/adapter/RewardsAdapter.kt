package app.solocoin.solocoin.ui.adapter

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
import app.solocoin.solocoin.ui.home.RewardDetailsActivity
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
    ): ViewHolder {
        val rootView: View =
            LayoutInflater.from(context).inflate(R.layout.reward_card_layout, parent, false)
        return ViewHolder(rootView)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        rewardsArrayList[position].let {
            updateImage(holder, it)
            with(holder) {
                companyName?.text = it.companyName
                costCoins?.text = it.costCoins
                costRupees?.text = it.costRupees
                mListener = object : RecyclerViewClickListener {
                    override fun onClick(view: View?, position: Int) {
                        val intent = Intent(context, RewardDetailsActivity::class.java)
                        intent.putExtra("EXTRA_INFO", rewardsArrayList[position])
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    private fun updateImage(
        viewHolder: ViewHolder,
        reward: Reward
    ) {
        //TODO: add code to update image from api or download the image
        viewHolder.companyLogo?.setImageResource(R.drawable.app_icon)
    }

    override fun getItemCount() = rewardsArrayList.size

    inner class ViewHolder internal constructor(itemView: View) :
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
    }

}