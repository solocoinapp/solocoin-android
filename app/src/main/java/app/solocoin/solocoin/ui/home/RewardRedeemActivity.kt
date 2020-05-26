package app.solocoin.solocoin.ui.home

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.solocoin.solocoin.R
import app.solocoin.solocoin.model.Reward
import app.solocoin.solocoin.ui.adapter.RewardRedeemAdapter
import kotlinx.android.synthetic.main.item_reward_redeem.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*

/**
 * Created by Saurav Gupta on 14/5/2020
 */
class RewardRedeemActivity : AppCompatActivity() {

    private lateinit var context: RewardRedeemActivity
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: RewardRedeemAdapter
    private lateinit var rewardArrayList: ArrayList<Reward?>

    @InternalCoroutinesApi
    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward_redeem)
        context = this

        redeem_code_container.visibility = View.GONE

        rewardArrayList = ArrayList()
        rewardArrayList.add(
            intent.extras?.getParcelable("EXTRA_INFO")
        )

        recyclerView = findViewById(R.id.offer_recycler_view)
        mAdapter = RewardRedeemAdapter(context, rewardArrayList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = mAdapter

        //TODO set the redeem coupon code to redeem_code textview in item_reward_redeem layout

        val claimOffer = findViewById<Button>(R.id.claim_offer)
        claimOffer.setOnClickListener {
            redeem_code_container.visibility = View.VISIBLE
            Toast.makeText(
                context,
                "You have claimed the offer.",
                Toast.LENGTH_LONG
            ).show()
            // TODO: replace extra activity creation with revealing coupon in same activity
            val intent = Intent(this, RedeemCodeActivity::class.java)
            intent.putExtra("code", rewardArrayList[0]?.couponCode)
            startActivity(intent)
        }

        val copyButton = findViewById<Button>(R.id.copy_button)
        copyButton.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip: ClipData = ClipData.newPlainText("Code", redeem_code.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(
                context,
                "Coupon code copied sucessfully!",
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