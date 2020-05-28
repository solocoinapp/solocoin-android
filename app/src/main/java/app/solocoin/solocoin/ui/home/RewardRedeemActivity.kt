package app.solocoin.solocoin.ui.home

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.solocoin.solocoin.R
import app.solocoin.solocoin.model.Reward
import app.solocoin.solocoin.repo.SolocoinRepository
import app.solocoin.solocoin.ui.adapter.RewardRedeemAdapter
import app.solocoin.solocoin.util.AppDialog
import com.google.gson.JsonObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

    private val repository: SolocoinRepository by inject()

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
                            onClickCancel()
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

    private fun redeemCoupon() {
        val body = JsonObject()
        body.addProperty("reward_sponsor_id", rewardArrayList[0].rewardId)
        val call: Call<JsonObject> = repository.redeemRewards(body)
        call.enqueue(object : Callback<JsonObject?> {
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                val infoDialog = AppDialog.instance(
                    "",
                    getString(R.string.claim_error),
                    object : AppDialog.AppDialogListener {
                        override fun onClickConfirm() {
                            onClickCancel()
                        }

                        override fun onClickCancel() {
                        }
                    },
                    getString(R.string.okay)
                )
                infoDialog.show(supportFragmentManager, infoDialog.tag)
            }

            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                val resp = response.body()
                resp?.let {
                    val infoDialog = AppDialog.instance(
                        "",
                        getString(R.string.claim_success),
                        object : AppDialog.AppDialogListener {
                            override fun onClickConfirm() {
                                rewardArrayList[0].isClaimed = true
                                mAdapter.notifyDataSetChanged()
                                onClickCancel()
                            }

                            override fun onClickCancel() {
                            }
                        },
                        getString(R.string.okay)
                    )
                    infoDialog.show(supportFragmentManager, infoDialog.tag)
                }
            }
        })
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}