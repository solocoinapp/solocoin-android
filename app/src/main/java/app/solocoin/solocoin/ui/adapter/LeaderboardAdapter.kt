package app.solocoin.solocoin.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.solocoin.solocoin.R
import app.solocoin.solocoin.model.Leaderboard
import app.solocoin.solocoin.ui.home.ShareBadgeActivity
import com.squareup.picasso.Picasso

/**
 * Created by Saurav Gupta on 22/05/2020
 */
class LeaderboardAdapter(
    private val context: Context,
    private val leaderboardArrayList: ArrayList<Leaderboard>
) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_leaderboard, parent, false))

    override fun getItemCount() = leaderboardArrayList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindLevel(leaderboardArrayList[position])
        holder.bindBadges(context, leaderboardArrayList[position])
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        var levelTv1: TextView? = null
        var levelTv2: TextView? = null
        var levelInfoTv1: TextView? = null
        var levelInfoTv2: TextView? = null
        var progressBar: ProgressBar? = null
        var badgesGridL: GridLayout? = null

        init {
            with(itemView) {
                levelTv1 = findViewById(R.id.level_tv_1)
                levelTv2 = findViewById(R.id.level_tv_2)
                levelInfoTv1 = findViewById(R.id.level_info_tv_1)
                levelInfoTv2 = findViewById(R.id.level_info_tv_2)
                progressBar = findViewById(R.id.level_pb)
                badgesGridL = findViewById(R.id.badges_gl)
            }
        }

        fun bindBadges(context: Context, leaderboard: Leaderboard) {
            val total = leaderboard.badges!!.size
            badgesGridL!!.columnCount = 2
            badgesGridL!!.rowCount = total / 2 + 1
            var col = 0
            var row = 0
            leaderboard.badges!!.forEach {
                it!!
                if (col == 2) {
                    col = 0
                    row++
                }
                val param = GridLayout.LayoutParams().apply {
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    width = GridLayout.LayoutParams.WRAP_CONTENT
                    setGravity(Gravity.CENTER)
                    columnSpec = GridLayout.spec(col)
                    rowSpec = GridLayout.spec(row)
                }
                val badgeCv = LayoutInflater.from(badgesGridL!!.context)
                    .inflate(R.layout.item_badge_card, badgesGridL!!, false).apply {

                        findViewById<TextView>(R.id.badge_name).apply {
                            text = it.name!!
                        }
                        findViewById<TextView>(R.id.badge_level).apply {
                            text = it.level!!
                        }
                        findViewById<ImageView>(R.id.badge_iv).apply {
                            try {
                                Picasso.get().load(it.imageUrl!!).into(this)
                            } catch (e: Exception) {
                                // TODO: In case unable to fetch image using internet
                            }
                        }
                        setOnClickListener { _ ->
                            val intent = Intent(
                                context,
                                ShareBadgeActivity::class.java
                            )
                            intent.putExtra("EXTRA_INFO", it)
                            context.startActivity(intent)
                        }
                        layoutParams = param
                    }
                badgesGridL!!.addView(badgeCv)
                col++
            }
        }

        fun bindLevel(leaderboard: Leaderboard) {
            // TODO: Add logic to update the level achieved by user till now and update UI
        }

    }
}
