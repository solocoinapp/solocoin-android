package app.solocoin.solocoin.ui.home

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.solocoin.solocoin.R
import app.solocoin.solocoin.model.Milestones
import app.solocoin.solocoin.ui.adapter.MilestonesAdapter
import app.solocoin.solocoin.util.enums.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by Saurav Gupta on 22/05/2020
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class MilestonesFragment : Fragment() {

    private lateinit var mAdapter: MilestonesAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var context: Activity

    private val viewModel: MilestonesFragmentViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        context = requireActivity()
        return inflater.inflate(R.layout.fragment_milestones, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.milestones_recycler_view)
        swipeRefreshLayout = view.findViewById(R.id.milestones_sl)

        recyclerView.layoutManager = LinearLayoutManager(context)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            updateMilestones()
            swipeRefreshLayout.isRefreshing = false
        }

        updateMilestones()
    }

    private fun updateMilestones() {
        viewModel.getBadgesLevels().observe(viewLifecycleOwner, Observer { response ->
            Log.d(TAG, "$response")
            when (response.status) {
                Status.SUCCESS -> {
                    response.data?.let {
                        mAdapter = MilestonesAdapter(context, ArrayList<Milestones>().apply {
                            it.badgeLevel.sortBy { x -> x.minPoints }
                            add(it)
                        })
                        recyclerView.adapter = mAdapter
                    }
                }
                Status.ERROR -> {
                }
                Status.LOADING -> {
                }
            }
        })
    }

    companion object {
        fun instance() = MilestonesFragment().apply {}
        private val TAG = MilestonesFragment::class.java.simpleName
    }
}
