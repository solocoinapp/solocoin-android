package app.solocoin.solocoin.ui.adapter

import android.app.Activity
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import app.solocoin.solocoin.R
import app.solocoin.solocoin.model.Reward
import com.squareup.picasso.Picasso
import java.util.*

/**
 * Created by Saurav Gupta on 14/5/2020
 */
class RewardRedeemAdapter(
    private val context: Activity,
    private val rewardArrayList: ArrayList<Reward?>
) :
    RecyclerView.Adapter<RewardRedeemAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder =
        ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_reward_redeem, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setUpView(rewardArrayList[position])
    }

    override fun getItemCount() = rewardArrayList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var rewardImage1: ImageView? = null
        var rewardImage2: ImageView? = null
        var extraTnc: TextView? = null
        var rewardName: TextView? = null
        var coinsAmt: TextView? = null
        var tnc: LinearLayout? = null

        init {
            with(itemView) {
                // TODO: change variable name to add company and reward image
                rewardImage1 = findViewById(R.id.reward_image_1)
                rewardImage2 = findViewById(R.id.reward_image_2)
                extraTnc = findViewById(R.id.extra_tnc)
                rewardName = findViewById(R.id.reward_name)
                coinsAmt = findViewById(R.id.coins_amt)
                tnc = findViewById(R.id.tnc)
            }
        }

        fun setUpView(reward: Reward?) {
            reward?.let {
                updateImage(it)
                coinsAmt?.text = it.costCoins
                extraTnc?.text = it.rewardDetails
                rewardName?.text = it.rewardName
                updateOfferDetails(it)
            }
        }

        private fun updateImage(reward: Reward) {
            //TODO: add code to handle case when either image is not available
            Picasso.get().load(reward.rewardImageUrl).into(rewardImage1)
            Picasso.get().load(reward.companyLogoUrl).into(rewardImage2)
        }

        private fun updateOfferDetails(reward: Reward) {
            reward.rewardTermsAndConditions?.let {
                val rewardTncTV = TextView(tnc?.context).apply {
                    text = it
                    setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    typeface = ResourcesCompat.getFont(context, R.font.poppins)
                }
                tnc?.addView(rewardTncTV)
            }
        }
    }


}