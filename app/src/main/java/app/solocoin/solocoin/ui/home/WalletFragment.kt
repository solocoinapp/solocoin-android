package app.solocoin.solocoin.ui.home

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp
import app.solocoin.solocoin.model.Reward
import app.solocoin.solocoin.model.ScratchTicket
import app.solocoin.solocoin.ui.adapter.RewardsListAdapter
import app.solocoin.solocoin.ui.adapter.ScratchDetailsAdapter
import app.solocoin.solocoin.util.GlobalUtils
import app.solocoin.solocoin.util.Resource
import app.solocoin.solocoin.util.enums.Status
import com.google.gson.JsonObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by Saurav Gupta on 14/5/2020
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class WalletFragment : Fragment() {

    private lateinit var mListAdapter: RewardsListAdapter
    private lateinit var mScratchTicketsAdapter: ScratchDetailsAdapter
    private lateinit var rewardsRecyclerView: RecyclerView
    private lateinit var scratchRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var balanceTextView: TextView
    private lateinit var errorLabel: ImageView
    private lateinit var errorTextView: TextView
    private lateinit var context: Activity

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

        balanceTextView = view.findViewById(R.id.tv_coins_count)
        errorLabel = view.findViewById(R.id.error_label)
        errorTextView = view.findViewById(R.id.load_issue)
        rewardsRecyclerView = view.findViewById(R.id.rewards_recycler_view)
        scratchRecyclerView = view.findViewById(R.id.scratch_ticket_recycler_view)
        swipeRefreshLayout = view.findViewById(R.id.wallet_sl)

        errorLabel.visibility = View.GONE
        errorTextView.visibility = View.GONE
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
    }

    private fun updateWallet() {
        viewModel.userData().observe(viewLifecycleOwner, Observer { response ->
            Log.d(TAG, "$response")
            when (response.status) {
                Status.SUCCESS -> {
                    val balance = response.data?.get("wallet_balance")?.asString
                    balanceTextView.text = balance
                    updateRewards(response)
                    SolocoinApp.sharedPrefs?.walletBalance = balance
                }
                Status.ERROR -> {
                    SolocoinApp.sharedPrefs?.walletBalance?.let {
                        balanceTextView.text = SolocoinApp.sharedPrefs?.walletBalance
                    }
                    fetchIssue(1)
                }
                Status.LOADING -> {
                }
            }
        })
    }

    private fun updateRewards(userProfile: Resource<JsonObject?>) {
        if (userProfile.data != null) {
            viewModel.rewards().observe(viewLifecycleOwner, Observer { response ->
                Log.d(TAG, "${response.data?.size}")
                when (response.status) {
                    Status.SUCCESS -> {
                        if (response.data != null) {
                            val rewards: ArrayList<Reward> = response.data
                            if (rewards.size == 0) {
                                fetchIssue(3)
                            } else {
                                rewardsRecyclerView.visibility = View.VISIBLE
                                errorLabel.visibility = View.GONE
                                errorTextView.visibility = View.GONE
                                rewards.sortBy { it.rewardId }
                                userProfile.data.getAsJsonArray("redeemed_rewards").forEach { itr ->
                                    val index =
                                        rewards.binarySearchBy(itr.asJsonObject.get("id").asString) { it.rewardId }
                                    rewards[index].isClaimed = true
                                }
                                mListAdapter = RewardsListAdapter(context, rewards)
                                rewardsRecyclerView.adapter = mListAdapter
                            }
                        } else {
                            fetchIssue(2)
                        }
                    }
                    Status.ERROR -> fetchIssue(1)
                    Status.LOADING -> {
                    }
                }
            })
        } else {
            fetchIssue(2)
        }
    }

    val fetchIssue = { option: Int ->
        when (option) {
            1 -> {
                if (!GlobalUtils.isNetworkAvailable(context)) {
                    errorTextView.text = getString(R.string.internet_issue)
                } else {
                    errorTextView.text = getString(R.string.load_issue)
                }
            }
            2 -> errorTextView.text = getString(R.string.load_issue)
            3 -> errorTextView.text = getString(R.string.zero_rewards)
        }
        rewardsRecyclerView.visibility = View.GONE
        scratchRecyclerView.visibility = View.GONE
        errorLabel.visibility = View.VISIBLE
        errorTextView.visibility = View.VISIBLE
    }

    private fun updateScratch() {
        val dummy = ScratchTicket(
            "50 rupees",
            "100 rupees"
        )
        ArrayList<ScratchTicket?>().let {
            it.add(dummy)
            it.add(dummy)
            it.add(dummy)
            it.add(dummy)
            it.add(dummy)
            it.add(dummy)
            mScratchTicketsAdapter = ScratchDetailsAdapter(context, it)
        }
        scratchRecyclerView.adapter = mScratchTicketsAdapter
    }

    companion object {
        fun instance() = WalletFragment().apply {}
        private val TAG = WalletFragment::class.simpleName
    }
}
