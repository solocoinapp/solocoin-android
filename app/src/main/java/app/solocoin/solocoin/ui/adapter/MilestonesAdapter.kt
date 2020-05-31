package app.solocoin.solocoin.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import app.solocoin.solocoin.R
import app.solocoin.solocoin.model.Milestones
import app.solocoin.solocoin.ui.home.ShareBadgeActivity
import app.solocoin.solocoin.util.GlobalUtils
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.ceil

/**
 * Created by Saurav Gupta on 22/05/2020
 */
class MilestonesAdapter(
    private val context: Context,
    private val milestonesArrayList: ArrayList<Milestones>
) : RecyclerView.Adapter<MilestonesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_milestones, parent, false))

    override fun getItemCount() = milestonesArrayList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindBadges(context, milestonesArrayList[position])
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private var levelFront: TextView
        private var levelMid: TextView
        private var levelRear: TextView
        private var levelTv: TextView
        private var trophyCntTv: TextView
        private var levelInfoTv1: TextView
        private var levelInfoTv2: TextView
        private var progressBar: ProgressBar
        private var badgesGridL: GridLayout

        init {
            with(itemView) {
                levelFront = findViewById(R.id.level_front)
                levelMid = findViewById(R.id.level_mid)
                levelRear = findViewById(R.id.level_rear)
                levelTv = findViewById(R.id.level_tv)
                trophyCntTv = findViewById(R.id.award_cnt)
                levelInfoTv1 = findViewById(R.id.level_info_tv_1)
                levelInfoTv2 = findViewById(R.id.level_info_tv_2)
                progressBar = findViewById(R.id.level_pb)
                badgesGridL = findViewById(R.id.badges_gl)
            }
        }

        @SuppressLint("DefaultLocale")
        fun bindBadges(context: Context, milestones: Milestones) {
            var userBadgesCnt = 0
            var userLevel = 0
            val total = milestones.badgeLevel.size
            badgesGridL.columnCount = 2
            badgesGridL.rowCount = total / 2 + 1
            var col = 0
            var row = 0
            milestones.badgeLevel.forEach {
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
                val badgeCv = LayoutInflater.from(badgesGridL.context)
                    .inflate(R.layout.item_badge_card, badgesGridL, false).let { badgeCv ->
                        badgeCv.findViewById<TextView>(R.id.badge_name).apply {
                            text = it.name.capitalize()
                        }
                        badgeCv.findViewById<TextView>(R.id.badge_level).apply {
                            text = ("Level ${it.level}")
                        }
                        badgeCv.findViewById<ImageView>(R.id.badge_iv).apply {
                            if (it.level == "1") {
                                visibility = View.GONE
                                val hPadding =
                                    (resources.displayMetrics.density * 82 + 0.5f).toInt()
                                val width = (resources.displayMetrics.density * 158 + 0.5f).toInt()
                                badgeCv.findViewById<RelativeLayout>(R.id.badge).apply {
                                    setPadding(0, hPadding, 0, hPadding)
                                    layoutParams.width = width
                                    gravity = Gravity.CENTER
                                }
                            } else {
                                GlobalUtils.loadImageNetworkCachePlaceholder(
                                    context.getString(R.string.image_base_url) + it.imageUrl,
                                    this
                                )
                            }
                        }
                        badgeCv.layoutParams = param
                        badgeCv
                    }
                if (milestones.earnedPoints.toDouble() >= it.minPoints.toDouble()) {
                    it.has = true
                    badgeCv.setOnClickListener { _ ->
                        val intent = Intent(
                            context,
                            ShareBadgeActivity::class.java
                        )
                        intent.putExtra("EXTRA_INFO", it)
                        context.startActivity(intent)
                    }
                    userLevel++
                    userBadgesCnt++
                } else {
                    it.has = false
                    CoroutineScope(Dispatchers.Default).launch {
                        val radius = 4f
                        val blurView = badgeCv.findViewById<BlurView>(R.id.blurView)
                        val decorView = (context as Activity).window.decorView
                        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
                        val windowBackground = decorView.background
                        blurView.setupWith(rootView)
                            .setFrameClearDrawable(windowBackground)
                            .setBlurAlgorithm(RenderScriptBlur(context))
                            .setBlurRadius(radius)
                    }
                }
                badgesGridL.addView(badgeCv)
                col++
            }
            // trophy counts
            trophyCntTv.text = "$userBadgesCnt"

            bindLevel(userLevel, milestones)
        }

        private fun bindLevel(
            userLevel: Int,
            milestones: Milestones
        ) {
            // user current level
            levelTv.text = ("Level $userLevel")

            // handle case when all the levels are exhausted/ acheived by the user
            if (userLevel < milestones.badgeLevel.size) {
                val nextLevelPoints = milestones.badgeLevel[userLevel].minPoints.toInt()
                val currentLevelPoints = milestones.badgeLevel[userLevel - 1].minPoints.toInt()
                val tillNextLevel = nextLevelPoints - milestones.earnedPoints.toInt()
                levelInfoTv1.text = ("$tillNextLevel coins away.")
                levelInfoTv2.text = ("$tillNextLevel coins to move to next level!")

                //progress bar
                levelRear.text = ("Level ${(ceil(userLevel / 3.0) * 3).toInt()}")
                levelMid.text = ("Level ${(ceil(userLevel / 3.0) * 3 - 1).toInt()}")
                levelFront.text = ("Level ${(ceil(userLevel / 3.0) * 3 - 2).toInt()}")
                var extraProgress = 1.0 * (milestones.earnedPoints.toInt() - currentLevelPoints)
                when (userLevel - ceil(userLevel / 3.0).toInt() * 3 + 2) {
                    0 -> {
                        extraProgress *= (27.0 / (nextLevelPoints - currentLevelPoints))
                        extraProgress = ceil(extraProgress)
                        progressBar.progress = 26 + extraProgress.toInt()
                    }
                    1 -> {
                        extraProgress *= (27.0 / (nextLevelPoints - currentLevelPoints))
                        extraProgress = ceil(extraProgress)
                        progressBar.progress = 53 + extraProgress.toInt()
                    }
                    2 -> {
                        extraProgress *= (19.0 / (nextLevelPoints - currentLevelPoints))
                        extraProgress = ceil(extraProgress)
                        progressBar.progress = 80 + extraProgress.toInt()
                    }
                }

            } else {
                levelInfoTv1.text =
                    ("Congratulations! Reached last level.\n New levels coming soon....")
                levelInfoTv2.text = ("0 coins to move to next level!")
                levelFront.text = ("Level ${(userLevel - 3)}")
                levelMid.text = ("Level ${(userLevel - 2)}")
                levelRear.text = ("Level ${(userLevel - 1)}")
                progressBar.progress = 99
            }
        }
    }
}
