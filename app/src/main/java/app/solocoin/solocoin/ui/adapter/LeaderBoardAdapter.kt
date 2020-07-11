package app.solocoin.solocoin.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import app.solocoin.solocoin.R
import app.solocoin.solocoin.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.coroutines.coroutineContext

/**
 *  Created by Karandeep Singh on 04/07/2020
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class LeaderBoardAdapter(
        private val context: Context,
        private val leaderboardArrayList: ArrayList<User>
): RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rank_card, parent, false))

    override fun getItemCount() = leaderboardArrayList.size

    class ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        private var name: TextView? = null
        private var country: TextView? = null
        private var coincount: TextView? = null
        private var rank: TextView? = null
        private var dots:TextView?=null
        private var cardview: CardView?=null
        private var cardviewborder: LinearLayout?=null

        init {
            with(itemView) {
                name = findViewById(R.id.name)
                country = findViewById(R.id.country)
                coincount = findViewById(R.id.coincount)
                rank=findViewById(R.id.rank)
                dots=findViewById(R.id.dots)
                cardview=findViewById(R.id.rank_cv)
                cardviewborder = findViewById(R.id.cardviewborder)

            }
        }
        private lateinit var context: Context;

        fun setUpView(context:Context, user: User?, position: Int) {
            user?.let {
                if(it.wallet_balance!=null){
                    coincount?.text=it.wallet_balance?.substring(0,it.wallet_balance!!.length-2)+" coins"
                }
                else{
                    coincount?.text=it.wallet_balance+" coins"
                }
                name?.text = it.name?.capitalize()!!
                rank?.text = "#"+it.rank!!
                country?.text=it.countryCode?.toUpperCase()
                if(position==2) dots?.visibility=View.VISIBLE
                if(position in 0..2){
//                    cardview?.cardBackgroundColor(getColor(context,R.color.light))
//                   cardview?.setCardBackgroundColor(getColor(context,R.color.grey_light))
//                    rank?.setTextColor(getColor(context,R.color.colorPrimaryDark))
//                    name?.setTextColor(getColor(context,R.color.colorAccent))
//                    coincount?.setTextColor(getColor(context,R.color.colorAccent))
//                    country?.setTextColor(getColor(context,R.color.colorPrimaryDark))
                    cardviewborder?.setBackgroundColor(getColor(context,R.color.white))
                }
            }
        }
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setUpView( context,leaderboardArrayList[position],position)
    }
}