package app.solocoin.solocoin.ui.home

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import app.solocoin.solocoin.util.enums.Status
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
        rewardsRecyclerView = view.findViewById(R.id.rewards_recycler_view)
        scratchRecyclerView = view.findViewById(R.id.scratch_ticket_recycler_view)

        swipeRefreshLayout = view.findViewById(R.id.wallet_sl)

        rewardsRecyclerView.layoutManager = LinearLayoutManager(context)
        scratchRecyclerView.layoutManager = GridLayoutManager(context, 2)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            updateWallet()
            updateRewards()
//            updateScratch()
            swipeRefreshLayout.isRefreshing = false
        }

        updateWallet()
        updateRewards()
//        updateScratch()
    }

    private fun updateWallet() {
        viewModel.userData().observe(viewLifecycleOwner, Observer { response ->
            Log.d(TAG, "$response")
            when (response.status) {
                Status.SUCCESS -> {
                    val balance = response.data?.get("wallet_balance")?.asString
                    balanceTextView.text = balance
                    SolocoinApp.sharedPrefs?.walletBalance = balance
                }
                Status.ERROR -> {
                    if (SolocoinApp.sharedPrefs?.walletBalance != "0") {
                        balanceTextView.text = SolocoinApp.sharedPrefs?.walletBalance
                    }
                }
                Status.LOADING -> {
                }
            }
        })
    }

    private fun updateRewards() {

        val dummy = Reward(
            "YouTube Premium Subscription for 6 Months",
            "Youtube",
            "Reward is valid for only Premium User",
            "200 Coins",
            "1000",
            "xyz1234789sdf",
            "This label is useless remove it.",
            null,
            null
        )
        ArrayList<Reward?>().let {
            it.add(dummy)
            it.add(dummy)
            it.add(dummy)
            it.add(dummy)
            it.add(dummy)
            it.add(dummy)
            mListAdapter = RewardsListAdapter(context, it)
        }
        rewardsRecyclerView.adapter = mListAdapter
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
