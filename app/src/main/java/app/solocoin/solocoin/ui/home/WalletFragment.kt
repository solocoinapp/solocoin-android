package app.solocoin.solocoin.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp
import app.solocoin.solocoin.model.Reward
import app.solocoin.solocoin.ui.adapter.RewardsListAdapter
import app.solocoin.solocoin.ui.adapter.ScratchDetailsAdapter
import app.solocoin.solocoin.util.EventBus
import app.solocoin.solocoin.util.GlobalUtils
import app.solocoin.solocoin.util.enums.Status
import com.google.gson.JsonObject
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by Saurav Gupta on 14/5/2020
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@RequiresApi(Build.VERSION_CODES.N)
class WalletFragment : Fragment() {

    private lateinit var mListAdapter: RewardsListAdapter
    private lateinit var mScratchTicketsAdapter: ScratchDetailsAdapter
    private lateinit var rewardsRecyclerView: RecyclerView
    private lateinit var scratchRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var balanceTextView: TextView
    private lateinit var errorLabel: ImageView
    private lateinit var errorTextView: TextView
    private lateinit var refreshTextView: TextView
    private lateinit var walletUpdateInfoTv: TextView
    private lateinit var context: Activity
    private var eventBusReward: Disposable? = null
    private var eventBusString: Disposable? = null
    private var show: Boolean = true

    private val viewModel: WalletFragmentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context = requireActivity()
        return inflater.inflate(R.layout.fragment_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        show = true
        balanceTextView = view.findViewById(R.id.tv_coins_count)
        errorLabel = view.findViewById(R.id.error_label)
        errorTextView = view.findViewById(R.id.load_issue)
        refreshTextView = view.findViewById(R.id.refresh)
        rewardsRecyclerView = view.findViewById(R.id.rewards_recycler_view)
        scratchRecyclerView = view.findViewById(R.id.scratch_ticket_recycler_view)
        swipeRefreshLayout = view.findViewById(R.id.wallet_sl)
        walletUpdateInfoTv = view.findViewById(R.id.wallet_update_info)

        errorLabel.visibility = View.GONE
        errorTextView.visibility = View.GONE
        refreshTextView.visibility = View.VISIBLE
        rewardsRecyclerView.visibility = View.GONE
        scratchRecyclerView.visibility = View.GONE
        walletUpdateInfoTv.visibility = View.INVISIBLE
        rewardsRecyclerView.layoutManager = LinearLayoutManager(context)
        scratchRecyclerView.layoutManager = GridLayoutManager(context, 2)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            updateWallet()
//            updateScratch()
            swipeRefreshLayout.isRefreshing = false
        }

        updateWallet()
//        updateScratch()

        SolocoinApp.sharedPrefs?.visited?.let {
            if (it[1]) {
                SolocoinApp.sharedPrefs?.visited = arrayListOf(it[0], false, it[2])
                showIntro()
            }
        }
    }

    override fun onDestroyView() {
        removeEventBus()
        super.onDestroyView()
    }

    @SuppressLint("CheckResult")
    private fun addEventBus() {
        eventBusReward = EventBus.listen(Reward::class.java).subscribe { event ->
            event?.let { x ->
                val index =
                    mListAdapter.rewardsArrayList.binarySearchBy(x.rewardId.toInt()) { it.rewardId.toInt() }
                mListAdapter.rewardsArrayList[index].isClaimed = true
                mListAdapter.notifyDataSetChanged()
                walletUpdateInfoTv.visibility = View.VISIBLE
            }
        }

        eventBusString = EventBus.listen(String::class.java).subscribe {
            if (it == "null") {
                walletUpdateInfoTv.visibility = View.VISIBLE
                updateWallet()
            }
        }
    }

    private fun removeEventBus() {
        eventBusReward?.dispose()
        eventBusString?.dispose()
    }

    private fun updateWallet() {
        walletUpdateInfoTv.visibility = View.INVISIBLE
        // Fetch wallet amount and offers already redeemed from user
        viewModel.userData().observe(viewLifecycleOwner, Observer { response ->
            //Log.d(TAG, "$response")
            when (response.status) {
                Status.SUCCESS -> {
                    val balance =
                        GlobalUtils.parseJsonNullFieldValue(response.data?.get("wallet_balance"))?.asString
                    if (balance != null) {
                        balanceTextView.text = balance
                        SolocoinApp.sharedPrefs?.walletBalance = balance
                    } else {
                        SolocoinApp.sharedPrefs?.walletBalance?.let {
                            balanceTextView.text = it
                        }
                    }
                    fetchOffers(response.data)
                }
                Status.ERROR -> {
                    balanceTextView.text = SolocoinApp.sharedPrefs?.walletBalance
                    fetchOffers(null)
                }
                Status.LOADING -> {
                }
            }
        })
    }

    private fun setOffersAdapter(offers: ArrayList<Reward>) {
        // Remove event bus if already present on this fragment
        removeEventBus()
        rewardsRecyclerView.visibility = View.VISIBLE
        errorLabel.visibility = View.GONE
        errorTextView.visibility = View.GONE
        refreshTextView.visibility = View.GONE
        mListAdapter = RewardsListAdapter(context, offers)
        rewardsRecyclerView.adapter = mListAdapter

        // Add event bus to listen to changes in RewardRedeemActivity for isClaimed variable
        addEventBus()
    }

    private fun fetchOffersSharedPrefs() {
        val offers = SolocoinApp.sharedPrefs?.offers
        if (offers != null) {
            setOffersAdapter(offers)
        } else {
            fetchIssue(1)
        }
    }

    // remove the offers not claimed yet since no offers are available
    private fun updateNFetchOffersSharedPrefs() {
        val offers = SolocoinApp.sharedPrefs?.offers?.apply {
            removeIf { !it.isClaimed }
        }
        if (offers != null) {
            setOffersAdapter(offers)
            // Update shared prefs
            SolocoinApp.sharedPrefs?.offers = offers
        } else {
            fetchIssue(2)
        }
    }

    private fun fetchOffers(userProfile: JsonObject?) {
        if (show) {
            refreshTextView.visibility = View.VISIBLE
            show = false
        }
        if (GlobalUtils.parseJsonNullFieldValue(userProfile?.get("redeemed_rewards")) == null) {
            fetchOffersSharedPrefs()
        } else {
            viewModel.getOffers().observe(viewLifecycleOwner, Observer { response ->
                //Log.d(TAG, "$response")
                when (response.status) {
                    Status.SUCCESS -> {
                        if (response.data != null) {
                            val offers: ArrayList<Reward> = response.data
                            if (offers.size == 0) {
                                updateNFetchOffersSharedPrefs()
                            } else {
                                // Check which offers are claimed already n create adapter
                                offers.sortBy { it.rewardId.toInt() }
                                userProfile?.getAsJsonArray("redeemed_rewards")?.forEach { itr ->
                                    val index =
                                        offers.binarySearchBy(itr.asJsonObject.get("rewards_sponsor_id").asInt) { it.rewardId.toInt() }
                                    offers[index].isClaimed = true
                                }
                                setOffersAdapter(offers)

                                // Update shared prefs
                                SolocoinApp.sharedPrefs?.offers = offers
                            }
                        } else {
                            fetchOffersSharedPrefs()
                        }
                    }
                    Status.ERROR -> fetchOffersSharedPrefs()
                    Status.LOADING -> {
                    }
                }
            })
        }
    }

    // Display error messages in place of offers list
    private fun fetchIssue(option: Int) {
        when (option) {
            1 -> {
                if (!GlobalUtils.isNetworkAvailable(context)) {
                    errorTextView.text = getString(R.string.internet_issue)
                } else {
                    errorTextView.text = getString(R.string.load_issue)
                }
            }
            2 -> errorTextView.text = getString(R.string.zero_rewards)
        }
        rewardsRecyclerView.visibility = View.GONE
        scratchRecyclerView.visibility = View.GONE
        refreshTextView.visibility = View.GONE
        errorLabel.visibility = View.VISIBLE
        errorTextView.visibility = View.VISIBLE
    }

//    private fun updateScratch() {
//        val dummy = ScratchTicket(
//            "50 rupees",
//            "100 rupees"
//        )
//        ArrayList<ScratchTicket?>().let {
//            it.add(dummy)
//            it.add(dummy)
//            it.add(dummy)
//            it.add(dummy)
//            it.add(dummy)
//            it.add(dummy)
//            mScratchTicketsAdapter = ScratchDetailsAdapter(context, it)
//        }
//        scratchRecyclerView.adapter = mScratchTicketsAdapter
//    }

    private fun showIntro() {
        with(requireActivity()) {
            val intro = findViewById<ImageView>(R.id.intro).apply {
                setImageResource(R.drawable.intro_wallet)
                visibility = View.VISIBLE
            }
            findViewById<ImageButton>(R.id.close_bt).apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    intro.visibility = View.GONE
                    it.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        fun instance() = WalletFragment().apply {}
        private val TAG = WalletFragment::class.simpleName
    }
}
