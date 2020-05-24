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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp
import app.solocoin.solocoin.model.Reward
import app.solocoin.solocoin.ui.adapter.RewardsAdapter
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

    private lateinit var mAdapter: RewardsAdapter
    private lateinit var recyclerView: RecyclerView
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
        recyclerView = view.findViewById(R.id.rewards_recycler_view)
        swipeRefreshLayout = view.findViewById(R.id.wallet_sl)

        recyclerView.layoutManager = LinearLayoutManager(context)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            updateWallet()
            updateRewards()
            swipeRefreshLayout.isRefreshing = false
        }

        updateWallet()
        updateRewards()
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
        val offerDetails = ArrayList<String?>().apply {
            for (i in 0..14) {
                add("\u2022 Reward Detail " + (i + 1) + ".")
            }
        }
        val dummy = Reward(
            "YouTube Premium Subscription for 6 Months",
            "1000",
            "200 Coins",
            offerDetails,
            "** Reward is valid for only Premium User",
            "Solocoin",
            "hdsah29374",
            ""
        )
        ArrayList<Reward>().let {
            it.add(dummy)
            it.add(dummy)
            it.add(dummy)
            it.add(dummy)
            it.add(dummy)
            it.add(dummy)
            mAdapter = RewardsAdapter(context, it)
        }
        recyclerView.adapter = mAdapter
    }

    companion object {
        fun instance() = WalletFragment().apply {}
        private val TAG = WalletFragment::class.simpleName
    }
}
