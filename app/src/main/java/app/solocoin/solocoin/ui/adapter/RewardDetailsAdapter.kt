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
import java.util.*

/**
 * Created by Saurav Gupta on 14/5/2020
 */
class RewardDetailsAdapter(
    private val context: Activity,
    private val rewardArrayList: ArrayList<Reward?>
) :
    RecyclerView.Adapter<RewardDetailsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder =
        ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_reward_details, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setUpView(rewardArrayList[position])
    }

    override fun getItemCount() = rewardArrayList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var offerImage1: ImageView? = null
        var offerImage2: ImageView? = null
        var extraTnc: TextView? = null
        var offerName: TextView? = null
        var coinsAmt: TextView? = null
        var tnc: LinearLayout? = null

        init {
            with(itemView) {
                offerImage1 = findViewById(R.id.offer_image_1)
                offerImage2 = findViewById(R.id.reward_image_2)
                extraTnc = findViewById(R.id.extra_tnc)
                offerName = findViewById(R.id.offer_name)
                coinsAmt = findViewById(R.id.coins_amt)
                tnc = findViewById(R.id.tnc)
            }
        }

        fun setUpView(reward: Reward?) {
            reward?.let {
                updateImage(it)
                coinsAmt?.text = it.costCoins
                extraTnc?.text = it.offerDetails
                offerName?.text = it.offerName
                updateOfferDetails(it)
            }
        }

        private fun updateImage(reward: Reward) {
            //TODO: add code to update image from api or download the image
            offerImage1?.setImageResource(R.drawable.reward)
            offerImage2?.setImageResource(R.drawable.app_icon)
        }

        private fun updateOfferDetails(reward: Reward) {
            reward.offerDetails.let {
                it?.forEach { x ->
                    val offerDetails = TextView(tnc?.context).apply {
                        text = x.toString()
                        setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                        typeface = ResourcesCompat.getFont(context, R.font.poppins)
                    }
                    tnc?.addView(offerDetails)
                }
            }
        }

    }

}