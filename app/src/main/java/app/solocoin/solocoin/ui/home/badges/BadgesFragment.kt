package app.solocoin.solocoin.ui.home.badges

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import app.solocoin.solocoin.R

/**
 * Created by: Vijay Daita
 * Fragment class that contains a RecyclerView displaying badges, which are passed in as a parameter
 */
class BadgesFragment (val badges: ArrayList<Badge>) : Fragment() {

    private var mView: View? = null
    private var mRecyclerView: RecyclerView? = null
    private lateinit var mAdapter: BadgesAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_badges, container, false)
        mRecyclerView = mView!!.findViewById(R.id.badge_rv)
        mAdapter = BadgesAdapter(badges, context!!)
        mRecyclerView!!.layoutManager = GridLayoutManager(context!!, 2)
        mRecyclerView!!.adapter = mAdapter
        return mView
    }

    companion object {
        fun instance(badges: ArrayList<Badge>) = BadgesFragment(badges).apply {}
    }

}
