package app.solocoin.solocoin.ui.home

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.model.Profile
import app.solocoin.solocoin.model.Reward
import app.solocoin.solocoin.ui.adapter.AllRewardsAdapter
import app.solocoin.solocoin.ui.adapter.ScratchCardAdapter
import app.solocoin.solocoin.util.AppDialog
import app.solocoin.solocoin.util.GlobalUtils
import app.solocoin.solocoin.util.enums.Status
import com.anupkumarpanwar.scratchview.ScratchView
import com.anupkumarpanwar.scratchview.ScratchView.IRevealListener
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_all_scratch_cards.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel


@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@RequiresApi(Build.VERSION_CODES.M)
class HomeFragment : Fragment() {

    private val TAG = HomeFragment::class.simpleName

    private val viewModel: HomeFragmentViewModel by viewModel()
    private lateinit var rewardsRecyclerView: RecyclerView
    private var tvHomeDuration: TextView? = null
    private lateinit var mScratchCardAdapter: ScratchCardAdapter
    private lateinit var context: Activity
    private lateinit var profile : Profile
    private  lateinit var redeemed_offers_id: ArrayList<Int>
    private lateinit var offersfiltered:ArrayList<Reward>
    private lateinit var  offers: ArrayList<Reward>
    private lateinit var noscratchcards:TextView
//    private lateinit var share_earn_cv:MaterialCardView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context = requireActivity()
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvHomeDuration = view.findViewById(R.id.time)
        rewardsRecyclerView = view.findViewById(R.id.scratch_rewards_recycler_view)
        rewardsRecyclerView.layoutManager = GridLayoutManager(context, 2)
        noscratchcards = view.findViewById(R.id.noscratchcardshome)
        redeemed_offers_id= ArrayList()
        redeemed_offers_id.clear()
        offersfiltered= ArrayList()
        offersfiltered.clear()
        sharedPrefs?.visited?.let {
            if (it[0]) {
                sharedPrefs?.visited = arrayListOf(false, it[1], it[2])
                val infoDialog = AppDialog.instance(
                    "",
                    getString(R.string.new_user_intro),
                    object : AppDialog.AppDialogListener {
                        override fun onClickConfirm() {
                            showIntro()
                        }
                        override fun onClickCancel() {
                            showIntro()
                        }
                    })
                infoDialog.show(requireFragmentManager(), infoDialog.tag)
            }
        }
        allscratchcards.setOnClickListener {
        val intent =Intent(context,AllScratchCardsActivity::class.java)
            startActivity(intent)
        }
//        share_earn_cv.setOnClickListener {
//            val shareIntent = Intent(Intent.ACTION_SEND)
//            shareIntent.type = "text/plain"
//            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.invite_subject))
//            shareIntent.putExtra(
//                    Intent.EXTRA_TEXT,
//                    getString(R.string.invite_message) + getString(R.string.app_link)
//            )
//            startActivity(Intent.createChooser(shareIntent, getString(R.string.invite_title)))
//        }
        updateTime()
        fetchredeemrewards()

        quiz_viewpager.adapter = QuizFragmentAdapter(this)

        TabLayoutMediator(quiz_tablayout, quiz_viewpager) { tab, position ->
            tab.text = tabHeading[position]
        }.attach()
    }

    fun showDialog(){
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.scratch_card)
        val scratchView: ScratchView = dialog.findViewById(R.id.scratch_view)

        scratchView.setRevealListener(object :IRevealListener{
            override fun onRevealed(scratchView: ScratchView?) {
                Toast.makeText(context,"Congratulations!!",Toast.LENGTH_LONG).show()
                scratchView?.visibility=View.GONE
//                scratch_card_image.visibility=View.GONE
            }

            override fun onRevealPercentChangedListener(scratchView: ScratchView?, percent: Float) {
                if(percent>0.5){
//                    Toast.makeText(context, "Revealed!$percent",Toast.LENGTH_LONG).show()
                }
            }
        })
        dialog.show()

    }
    private fun fetchredeemrewards() {
        viewModel.getProfile().observe(viewLifecycleOwner, Observer { response ->

            when (response.status) {
                Status.SUCCESS -> {
                    profile=response.data!!
                    Log.d(TAG,"receivedresponsenow"+response.data)
                    if(profile.redeemed_rewards.isEmpty()){
                        fetchScratchcardOffers()
                    }
                    else {
                        var i=profile.redeemed_rewards.size-1
                        while(i >=0){
                            redeemed_offers_id.add(profile.redeemed_rewards[i].rewards_sponsor_id)
                            i--
                        }
                        fetchScratchcardOffers()
                    }
                }
                Status.ERROR -> {

                }
                Status.LOADING -> {}
            }
        })
    }
    private fun updateTime() {
        viewModel.userData().observe(viewLifecycleOwner, Observer { response ->
//            Log.d(TAG + "After Login/SignUp", "$response")
            when (response.status) {
                Status.SUCCESS -> {
                    val duration =
                        GlobalUtils.parseJsonNullFieldValue(response.data?.get("home_duration_in_seconds"))?.asLong
                    if (duration != 0L && duration != null) {
                        tvHomeDuration?.text = GlobalUtils.formattedHomeDuration(duration)
                        sharedPrefs?.homeDuration = duration
                    }
//                    fetchScratchcardOffers()
                }
                Status.ERROR -> {
                    if (sharedPrefs?.homeDuration != 0L) {
                        tvHomeDuration?.text = GlobalUtils.formattedHomeDuration(sharedPrefs?.homeDuration)
                    }
                }
                Status.LOADING -> {}
            }
        })
    }

    private fun fetchScratchcardOffers() {
        viewModel.getScratchCardOffers().observe(viewLifecycleOwner, Observer { response ->
            //Log.d(TAG, "$response")
            when (response.status) {
                Status.SUCCESS -> {
                    if (response.data != null) {
//                            val offers: ArrayList<Reward> = response.data
                        offers=response.data
                        Log.d(TAG,"receivedscratchcardoffers: "+offers)
                        if (offers.size == 0) {
                            noscratchcards.visibility=View.VISIBLE
//                            updateNFetchOffersSharedPrefs()
                            Log.d(TAG,"inifffff")
                        } else {
                            Log.d(TAG,"inelssssseee")
                            // Check which offers are claimed already n create adapter
//                            offers.sortBy { it.rewardId.toInt() }
//                            userProfile?.getAsJsonArray("redeemed_rewards")?.forEach { itr ->
//                                val index =
//                                        offers.binarySearchBy(itr.asJsonObject.get("rewards_sponsor_id").asInt) { it.rewardId.toInt() }
//                                offers[index].isClaimed = true
//                            }
                            var j = offers.size - 1
                            while (j >= 0) {
                                if(offers[j].rewardId !in redeemed_offers_id){

                                    offersfiltered.add(offers[j])
                                    if(offersfiltered.size==4) break
                                    Log.i(TAG,"offersfiltered:"+j)
                                }
                                j--
                            }
                            Log.d(TAG,"calling setadapter")
                            if(offersfiltered.size>0)
                            setOffersAdapter(offersfiltered)
                            else noscratchcards.visibility=View.VISIBLE

                            // Update shared prefs
                            SolocoinApp.sharedPrefs?.offers = offers
                        }
                    }
//                    else {
//                        fetchOffersSharedPrefs()
//                    }
                }
                Status.ERROR -> {}
                Status.LOADING -> {
                }
            }
        })

    }

    private fun setOffersAdapter(offers: ArrayList<Reward>) {
        Log.d(TAG,"inside setadapter"+offers)
                mScratchCardAdapter = ScratchCardAdapter(context, offers)
                rewardsRecyclerView.adapter = mScratchCardAdapter
        }

    private fun showIntro() {
        with(requireActivity()) {
            val intro = findViewById<ImageView>(R.id.intro).apply {
                setImageResource(R.drawable.intro_home)
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
        fun instance() = HomeFragment().apply {}
        private const val TAB_COUNT = 2
        private val tabHeading = arrayOf("DAILY", "WEEKLY")
    }

    private class QuizFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int {
            return TAB_COUNT
        }

        override fun createFragment(position: Int): Fragment {
            return QuizFragment(position)
        }
    }
}
