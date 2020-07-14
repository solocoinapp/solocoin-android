package app.solocoin.solocoin.ui.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import app.solocoin.solocoin.R
import app.solocoin.solocoin.model.Reward
import app.solocoin.solocoin.model.ScratchTicket
import app.solocoin.solocoin.ui.home.AppGuideActivity
import app.solocoin.solocoin.ui.home.RewardRedeemActivity
import com.anupkumarpanwar.scratchview.ScratchView
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*

/**
 * Created by Karandeep Singh on 14/07/2020
 */
class ScratchCardAdapter(
    private val context: Activity,
    private val scratchArrayList: ArrayList<Reward>
) :
    RecyclerView.Adapter<ScratchCardAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder =
        ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_scratch_card, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setUpView(scratchArrayList[position])
    }

    override fun getItemCount() = scratchArrayList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var offernametxt: String? = null
        private var scratchitem: Reward?=null
//        private var scratchReward: TextView? = null
//        var scratchCardLayout: ConstraintLayout? = null

        init {
            with(itemView) {
                itemView.setOnClickListener {
                    showDialog(context)
                }
//                offername = findViewById(R.id.offername)
//                scratchReward = findViewById(R.id.scratch_reward)
//                scratchCardLayout = findViewById(R.id.scratch_constraint_layout)
            }
        }


        @OptIn(InternalCoroutinesApi::class)
        private fun showDialog(context:Context) {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.scratch_card)
            val scratchView: ScratchView = dialog.findViewById(R.id.scratch_view)
            var offername:TextView = dialog.findViewById(R.id.offername)
            var moredetails:TextView=dialog.findViewById(R.id.moredetails)
            moredetails.setOnClickListener {
              val intent =Intent(context,RewardRedeemActivity::class.java)
                    intent.putExtra("EXTRA_INFO", scratchitem)
                    intent.putExtra("scratchcard",true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    context.startActivity(intent)
            }
            offername.setText(offernametxt)
            scratchView.setRevealListener(object : ScratchView.IRevealListener {
                override fun onRevealed(scratchView: ScratchView?) {
                    Toast.makeText(context,"Congratulations!!",Toast.LENGTH_LONG).show()
                    scratchView?.visibility=View.GONE
                    scratchitem
//                    scratch_card_image.visibility=View.GONE
                }

                override fun onRevealPercentChangedListener(scratchView: ScratchView?, percent: Float) {
                    if(percent>0.5){
//                    Toast.makeText(context, "Revealed!$percent",Toast.LENGTH_LONG).show()
                    }
                }
            })
            dialog.show()
        }

        fun setUpView(scratchTicket: Reward?) {
            scratchitem=scratchTicket
            scratchTicket?.let {
                offernametxt="You Won "+it.rewardName+"\nworth ₹ "+it.costRupees
//                offername?.text = "You won "+it.rewardName!!
//                scratchReward?.text = it.rewardRupees!!
            }
        }

    }

}