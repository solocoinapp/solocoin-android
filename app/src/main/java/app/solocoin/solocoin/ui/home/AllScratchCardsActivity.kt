package app.solocoin.solocoin.ui.home

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp
import app.solocoin.solocoin.model.Profile
import app.solocoin.solocoin.model.RedeemedRewards
import app.solocoin.solocoin.model.Reward
import app.solocoin.solocoin.ui.adapter.AllRewardsAdapter
import app.solocoin.solocoin.ui.adapter.RewardRedeemAdapter
import app.solocoin.solocoin.ui.adapter.RewardsListAdapter
import app.solocoin.solocoin.ui.adapter.ScratchCardAdapter
import app.solocoin.solocoin.util.GlobalUtils
import app.solocoin.solocoin.util.enums.Status
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_all_scratch_cards.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Karandeep Singh on 15/07/2020
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class AllScratchCardsActivity : AppCompatActivity() {
    private lateinit var context: AllScratchCardsActivity
    private lateinit var recyclerView: RecyclerView
    private lateinit var unscratched_recyclerView: RecyclerView
    private lateinit var mAdapter: AllRewardsAdapter
    private lateinit var mScratchCardAdapter: ScratchCardAdapter
    private lateinit var swipe_to_refresh: SwipeRefreshLayout
    private var TAG:String ="AllScratchCardsActivity/"
    private lateinit var profile :Profile
    private lateinit var offers: ArrayList<Reward>
    private lateinit var offersfiltered:ArrayList<Reward>
    private var redeemedRewards:ArrayList<RedeemedRewards> = ArrayList()
    private  lateinit var redeemed_offers_id: ArrayList<Int>
    private val viewModel: AllScratchCardsViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_scratch_cards)
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        context = this
        recyclerView = findViewById(R.id.allrewards_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        unscratched_recyclerView = findViewById(R.id.unsratched_recycler_view)
        unscratched_recyclerView.layoutManager = GridLayoutManager(context, 2)
        swipe_to_refresh=findViewById(R.id.swipe_to_refresh)
        swipe_to_refresh.setColorSchemeResources(R.color.colorAccent)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.allrewards_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Redeemed Rewards"
//        fetchScratchcardOffers()
        redeemed_offers_id= ArrayList()
        redeemed_offers_id.clear()
        offersfiltered= ArrayList()
        offersfiltered.clear()
        fetchredeemrewards()
        swipe_to_refresh.setOnRefreshListener {
//            fetchScratchcardOffers()
            redeemed_offers_id.clear()
            offersfiltered.clear()
            fetchredeemrewards()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun fetchredeemrewards() {
        viewModel.getProfile().observe(this, Observer { response ->

            when (response.status) {
                Status.SUCCESS -> {
                    profile=response.data!!
                   Log.d(TAG,"receivedresponsenow"+response.data)
                    if(profile.redeemed_rewards.isEmpty()){
                        norewards.visibility= View.VISIBLE
                        swipe_to_refresh.isRefreshing=false
                        fetchScratchcardOffers()
                    }
                    else {
                        norewards.visibility= View.GONE
                        var i=profile.redeemed_rewards.size-1
                        while(i >=0){
                            redeemed_offers_id.add(profile.redeemed_rewards[i].rewards_sponsor_id)
                            i--
                        }
                        redeemedRewards=profile.redeemed_rewards
                        redeemedRewards.reverse()
                        fetchScratchcardOffers()
                        mAdapter = AllRewardsAdapter(context, redeemedRewards)
                        recyclerView.adapter = mAdapter
                        swipe_to_refresh.isRefreshing=false
                    }
                }
                Status.ERROR -> {

                }
                Status.LOADING -> {}
            }
        })
    }
    private fun fetchScratchcardOffers() {
        Log.i(TAG,"outsidescratchcardviewmodel")
        viewModel.getScratchCardOffers().observe(this, Observer { response ->
            //Log.d(TAG, "$response")
            Log.i(TAG,"insidescratchcardviewmodel")
            when (response.status) {
                Status.SUCCESS -> {
                    if (response.data != null) {
                        offers=response.data
                        if (offers.size == 0) {
                            Log.i(TAG,"offerssizeiszero")
                        }
                        else {
                            var j = offers.size - 1
//                            var found: Boolean
                            while (j >= 0) {
//                                found = false
//                                var size = redeemed_offers_id.size - 1
//                                while (size >= 0) {
//                                    Log.i(TAG, "redeemed_offers_idinsidewhile" + redeemed_offers_id[size])
//                                    Log.i(TAG, "offersjrewardIdinsidewhile" + offers[j].rewardId)
//                                    if (redeemed_offers_id[size] == offers[j].rewardId) {
//                                        found = true
//                                        Log.i(TAG, "bothareequalandfound=" + found)
//                                        break
//                                    }
//                                    size--
//                                }
//                                if (!found) offersfiltered.add(offers[j])
//                            if(!redeemed_offers_id.contains(offers[j].rewardId)){
//                                offersfiltered.add(offers[j])
//                                Log.i(TAG,"offersfiltered:"+j)
//                            }
                        if(offers[j].rewardId !in redeemed_offers_id){

                            offersfiltered.add(offers[j])
                            Log.i(TAG,"offersfiltered:"+j)
                        }
                                j--
                            }
                            if (offersfiltered.size > 0) {
                                Log.i(TAG, "offersfiltered:" + offersfiltered)
                                mScratchCardAdapter = ScratchCardAdapter(context, offersfiltered)
                                unscratched_recyclerView.adapter = mScratchCardAdapter

                                // Update shared prefs
                                SolocoinApp.sharedPrefs?.offers = offers
                            }
                            else noscratchcards.visibility=View.VISIBLE
                        }
                    }
//                    else {
////                        fetchOffersSharedPrefs()
//                    }
                }
                Status.ERROR -> {}
                Status.LOADING -> {
                }
            }
        })

    }
}