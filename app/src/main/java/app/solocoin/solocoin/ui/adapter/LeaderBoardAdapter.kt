package app.solocoin.solocoin.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import app.solocoin.solocoin.R
import app.solocoin.solocoin.model.LeaderBoard
import app.solocoin.solocoin.model.Milestones
import app.solocoin.solocoin.model.ScratchTicket
import app.solocoin.solocoin.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
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

        init {
            with(itemView) {
                name = findViewById(R.id.name)
                country = findViewById(R.id.country)
                coincount = findViewById(R.id.coincount)
                rank=findViewById(R.id.rank)
            }
        }
        private lateinit var context: Context
        fun setUpView(user: User?) {
            user?.let {
                name?.text = it.name?.capitalize()!!
                rank?.text = "#"+it.rank!!
                country?.text=it.countryCode?.toUpperCase()
                coincount?.text=it.wallet_balance+" coins"
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setUpView( leaderboardArrayList[position])
    }

}
