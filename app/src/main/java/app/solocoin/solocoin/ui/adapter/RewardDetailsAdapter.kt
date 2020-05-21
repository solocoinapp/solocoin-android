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
    ): ViewHolder {
        val rootView: View =
            LayoutInflater.from(context).inflate(R.layout.reward_details_layout, parent, false)
        return ViewHolder(rootView)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        rewardArrayList[position]?.let {
            holder.let { h ->
                updateImage(h, it)
                h.coinsAmt?.text = it.costCoins
                h.extraTnc?.text = it.offerExtraDetails
                h.offerName?.text = it.offerName
                updateOfferDetails(h, it)
            }
        }
    }

    private fun updateOfferDetails(
        viewHolder: ViewHolder,
        reward: Reward
    ) {
        reward.offerDetails.let {
            it.forEach { x ->
                val offerDetails = TextView(viewHolder.tnc?.context).apply {
                    text = x.toString()
                    setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                    typeface = ResourcesCompat.getFont(context, R.font.poppins)
                }
                viewHolder.tnc?.addView(offerDetails)
            }
        }
    }

    private fun updateImage(
        viewHolder: ViewHolder,
        reward: Reward
    ) {
        //TODO: add code to update image from api or download the image
        viewHolder.offerImage1?.setImageResource(R.drawable.reward)
        viewHolder.offerImage2?.setImageResource(R.drawable.reward)
    }

    override fun getItemCount() = rewardArrayList.size

    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        var offerImage1: ImageView? = null
        var offerImage2: ImageView? = null
        var extraTnc: TextView? = null
        var offerName: TextView? = null
        var coinsAmt: TextView? = null
        var tnc: LinearLayout? = null

        init {
            offerImage1 = itemView.findViewById(R.id.offer_image_1)
            offerImage2 = itemView.findViewById(R.id.reward_image_2)
            extraTnc = itemView.findViewById(R.id.extra_tnc)
            offerName = itemView.findViewById(R.id.offer_name)
            coinsAmt = itemView.findViewById(R.id.coins_amt)
            tnc = itemView.findViewById(R.id.tnc)
        }
    }

}