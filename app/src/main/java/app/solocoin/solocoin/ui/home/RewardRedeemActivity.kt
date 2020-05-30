package app.solocoin.solocoin.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp
import app.solocoin.solocoin.model.Reward
import app.solocoin.solocoin.ui.adapter.RewardRedeemAdapter
import app.solocoin.solocoin.util.AppDialog
import app.solocoin.solocoin.util.enums.Status
import com.google.gson.JsonObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

/**
 * Created by Saurav Gupta on 14/5/2020
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class RewardRedeemActivity : AppCompatActivity() {

    private lateinit var context: RewardRedeemActivity
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: RewardRedeemAdapter
    private lateinit var rewardArrayList: ArrayList<Reward>
    private val loadingDialog = AppDialog.instance()

    private val viewModel: RewardRedeemViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward_redeem)
        context = this

        rewardArrayList = ArrayList()
        rewardArrayList.add(
            intent.extras?.getParcelable("EXTRA_INFO")!!
        )

        recyclerView = findViewById(R.id.reward_recycler_view)
        mAdapter = RewardRedeemAdapter(context, rewardArrayList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = mAdapter

        val claimOffer = findViewById<Button>(R.id.claim_offer)
        claimOffer.setOnClickListener {
            if (rewardArrayList[0].isClaimed) {
                val infoDialog = AppDialog.instance(
                    "",
                    getString(R.string.already_claimed),
                    object : AppDialog.AppDialogListener {
                        override fun onClickConfirm() {

                        }

                        override fun onClickCancel() {}
                    },
                    getString(R.string.okay)
                )
                infoDialog.show(supportFragmentManager, infoDialog.tag)
            } else {
                redeemCoupon()
            }
        }

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.profile_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Reward Details"
    }

    @SuppressLint("DefaultLocale")
    private fun redeemCoupon() {
        loadingDialog.show(supportFragmentManager, loadingDialog.tag)

        Log.wtf(TAG, SolocoinApp.sharedPrefs?.authToken)
        val body = JsonObject().apply {
            addProperty("rewards_sponsor_id", rewardArrayList[0].rewardId.toInt())
        }

        viewModel.redeemReward(body).observe(this, Observer { response ->
            Log.d(TAG, "$response")
            when (response.status) {
                Status.SUCCESS -> {
                    if (response.data != null) {
                        when (response.code) {
                            200 -> {
                                try {
                                    // update this activity adapter to show coupon code
                                    rewardArrayList[0].isClaimed = true
                                    mAdapter.notifyDataSetChanged()
                                    val offers = SolocoinApp.sharedPrefs?.offers
                                    offers?.let { x ->
                                        val index =
                                            x.binarySearchBy(rewardArrayList[0].rewardId) { it.rewardId }
                                        x[index].isClaimed = true
                                    }

                                    // update shared prefs offers list
                                    SolocoinApp.sharedPrefs?.offers = offers

                                    // inform Milestones fragment about changes
//                                    EventBus.publish(rewardArrayList[0])

                                } catch (e: Exception) {
//                                    EventBus.publish("null")
                                    Log.d(TAG, e.toString())
                                }
                                loadingDialog.dismiss()
                                val infoDialog = AppDialog.instance(
                                    "Offer Claimed",
                                    getString(R.string.claim_success),
                                    object : AppDialog.AppDialogListener {
                                        override fun onClickConfirm() {
                                            onClickCancel()
                                        }

                                        override fun onClickCancel() {}
                                    },
                                    getString(R.string.okay)
                                )
                                infoDialog.show(supportFragmentManager, infoDialog.tag)
                            }

                            422 -> {
                                loadingDialog.dismiss()
                                val infoDialog = AppDialog.instance(
                                    "Error",
                                    response.data.get("error").asString.capitalize(),
                                    object : AppDialog.AppDialogListener {
                                        override fun onClickConfirm() {
                                            onClickCancel()
                                        }

                                        override fun onClickCancel() {}
                                    }
                                )
                                infoDialog.show(supportFragmentManager, infoDialog.tag)
                            }
                        }
                    } else {
                        loadingDialog.dismiss()
                        val infoDialog = AppDialog.instance(
                            "Error",
                            getString(R.string.claim_error),
                            object : AppDialog.AppDialogListener {
                                override fun onClickConfirm() {
                                    onClickCancel()
                                }

                                override fun onClickCancel() {}
                            },
                            getString(R.string.okay)
                        )
                        infoDialog.show(supportFragmentManager, infoDialog.tag)
                    }
                }
                Status.ERROR -> {
                    loadingDialog.dismiss()
                    val infoDialog = AppDialog.instance(
                        "Error",
                        getString(R.string.claim_error),
                        object : AppDialog.AppDialogListener {
                            override fun onClickConfirm() {
                                onClickCancel()
                            }

                            override fun onClickCancel() {}
                        },
                        getString(R.string.okay)
                    )
                    infoDialog.show(supportFragmentManager, infoDialog.tag)
                }
                Status.LOADING -> {
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        private val TAG = RewardRedeemActivity::class.java.simpleName
    }
}