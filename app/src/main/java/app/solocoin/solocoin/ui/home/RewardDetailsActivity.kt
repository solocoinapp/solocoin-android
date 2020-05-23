package app.solocoin.solocoin.ui.home

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.solocoin.solocoin.R
import app.solocoin.solocoin.model.Reward
import app.solocoin.solocoin.ui.adapter.RewardDetailsAdapter
import java.util.*

/**
 * Created by Saurav Gupta on 14/5/2020
 */
class RewardDetailsActivity : AppCompatActivity() {

    private lateinit var context: RewardDetailsActivity
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: RewardDetailsAdapter
    private lateinit var rewardArrayList: ArrayList<Reward?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward_details)
        context = this

        rewardArrayList = ArrayList()
        rewardArrayList.add(
            intent.extras?.getParcelable("EXTRA_INFO")
        )

        recyclerView = findViewById(R.id.offer_recycler_view)
        mAdapter = RewardDetailsAdapter(context, rewardArrayList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = mAdapter

        val claimOffer = findViewById<Button>(R.id.claim_offer)
        claimOffer.setOnClickListener {
            Toast.makeText(
                context,
                "You have claimed the offer.",
                Toast.LENGTH_LONG
            ).show()
        }

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.profile_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Reward Details"

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}