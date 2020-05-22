package app.solocoin.solocoin.ui.home

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.solocoin.solocoin.R

class LeaderboardFragment : Fragment() {
    //
//    private lateinit var mAdapter: RewardsAdapter
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
//    private lateinit var balanceTextView: TextView
    private lateinit var context: Activity

    //
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        context = activity!!
        return inflater.inflate(R.layout.fragment_leaderboard, container, false)
    }

    //
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        recyclerView = view.findViewById(R.id.rewards_recycler_view)
//        swipeRefreshLayout = view.findViewById(R.id.swipe_layout)
//
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        recyclerView.itemAnimator = DefaultItemAnimator()
//        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
//        swipeRefreshLayout.setOnRefreshListener {
//            updateWallet()
//            updateRewards()
//            swipeRefreshLayout.isRefreshing = false
//        }
//    }
//
    companion object {
        fun instance() = LeaderboardFragment().apply {}
    }
}
