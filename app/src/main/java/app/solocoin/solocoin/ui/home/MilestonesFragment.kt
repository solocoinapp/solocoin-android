package app.solocoin.solocoin.ui.home

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp
import app.solocoin.solocoin.model.Badge
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        initializeMilestones()

        SolocoinApp.sharedPrefs?.visited?.let {
            if (it[2]) {
                SolocoinApp.sharedPrefs?.visited = arrayListOf(it[0], it[1], false)
                showIntro()
            }
        }
    }

    // initialize basic milestone section to avoid waiting for response
    private fun initializeMilestones() {
        val milestones = Milestones("0", ArrayList<Badge>().apply {
            add(
                Badge(
                    "error",
                    "Infant",
                    "1",
                    "One who stays home is not common",
                    "0"
                )
            )
            add(
                Badge(
                    "error",
                    "Alpha Warrior",
                    "2",
                    "Every Alpha Warrior was a trainee once",
                    "1000"
                )
            )
            add(
                Badge(
                    "error",
                    "Beta Warrior",
                    "3",
                    "Soldier! Lead the Way!",
                    "2500"
                )
            )
        })
        mAdapter = MilestonesAdapter(context, ArrayList<Milestones>().apply { add(milestones) })
        recyclerView.adapter = mAdapter

        updateMilestones()

        if (SolocoinApp.sharedPrefs?.visited?.get(2) != true) {
            // updating milestone through api or shared prefs
        }
    }

    private fun fetchMilestonesSharedPrefs() {
            SolocoinApp.sharedPrefs?.milestones?.let {
                if (it.badgeLevel.size > 3 && it.earnedPoints.toDouble() >= 0.0) {
                    mAdapter = MilestonesAdapter(context, ArrayList<Milestones>().apply { add(it) })
                    recyclerView.adapter = mAdapter
                }
            }
    }

    private fun updateMilestones() {
        viewModel.getBadgesLevels().observe(viewLifecycleOwner, Observer { response ->
            //Log.d(TAG, "$response")
            when (response.status) {
                Status.SUCCESS -> {
                    val milestones = response.data
                    if ((milestones?.badgeLevel != null) && (milestones.badgeLevel.size > 3 && milestones.earnedPoints.toDouble() >= 0.0)) {
                        mAdapter = MilestonesAdapter(context, ArrayList<Milestones>().apply {
                            milestones.badgeLevel.sortBy { x -> x.level.toInt() }
                            add(milestones)
                        })
                        recyclerView.adapter = mAdapter
                        SolocoinApp.sharedPrefs?.milestones = milestones
                    } else {
                        fetchMilestonesSharedPrefs()
                    }
                }
                Status.ERROR -> {
                    fetchMilestonesSharedPrefs()
                }
                Status.LOADING -> {
                }
            }
        })
    }

    private fun showIntro() {
        with(requireActivity()) {
            val intro = findViewById<ImageView>(R.id.intro).apply {
                setImageResource(R.drawable.intro_milestone)
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
        fun instance() = MilestonesFragment().apply {}
        private val TAG = MilestonesFragment::class.java.simpleName
    }
}
