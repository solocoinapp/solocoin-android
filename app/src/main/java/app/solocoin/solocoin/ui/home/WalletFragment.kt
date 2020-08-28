package app.solocoin.solocoin.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp
import app.solocoin.solocoin.model.Reward
import app.solocoin.solocoin.ui.adapter.RewardsListAdapter
import app.solocoin.solocoin.ui.adapter.ScratchCardAdapter
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
 * Updated by Karandeep Singh on 07/07/2020
 */

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@RequiresApi(Build.VERSION_CODES.N)
class WalletFragment : Fragment() {

    private lateinit var mListAdapter: RewardsListAdapter
    private lateinit var mScratchCardAdapter: ScratchCardAdapter
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
    private lateinit var  offers: ArrayList<Reward>
    private val categorylistarray=arrayOf("Entertainment","Health & Fitness","Gaming","Education",
            "Lifestyle","Shopping","Food","Travel","Grocery","Software","IT Services","Legal & CA","Everything Else")

    private  var categorylist:ArrayList<String> = ArrayList()
    private val viewModel: WalletFragmentViewModel by viewModel()
        private lateinit var menubutton:TextView
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
        menubutton =view.findViewById(R.id.menubutton)
        errorLabel.visibility = View.GONE
        errorTextView.visibility = View.GONE
        refreshTextView.visibility = View.VISIBLE
        rewardsRecyclerView.visibility = View.GONE
        scratchRecyclerView.visibility = View.GONE
        rewardsRecyclerView.layoutManager = GridLayoutManager(context,1)
        scratchRecyclerView.layoutManager = GridLayoutManager(context, 2)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            updateWallet()
//            updateScratch()
            swipeRefreshLayout.isRefreshing = false
        }

        menubutton.setOnClickListener {
            showDialog()
        }
//        spinner.setSelection(0,false)
//        spinner.setOnTouchListener(fun(v: View, event: MotionEvent): Boolean {
////            println("Real touch felt.")
////            touch = true
////            return false
////        })

//        spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                println("error")
//            }
//
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                if (touch) {
//                    val item: String = parent?.getItemAtPosition(position).toString()
//
////                if(item!="Select Category") {
//                    setOffersAdapter(offers, item)
//                    Toast.makeText(parent?.context, item + " selected!!", Toast.LENGTH_LONG).show()
////                }
//                }
//                touch=false
//            }
//        }
        updateWallet()
//        updateScratch()

        SolocoinApp.sharedPrefs?.visited?.let {
            if (it[1]) {
                SolocoinApp.sharedPrefs?.visited = arrayListOf(it[0], false, it[2])
                showIntro()
            }
        }

    }
    fun showDialog(){
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_menu)

        val all: TextView = dialog.findViewById(R.id.all)
        val entertainment:TextView = dialog.findViewById(R.id.entertainment)
        val health:TextView = dialog.findViewById(R.id.health)
        val gaming: TextView = dialog.findViewById(R.id.gaming)
        val education: TextView = dialog.findViewById(R.id.education)
        val shopping:TextView = dialog.findViewById(R.id.shopping)
        val lifestyle:TextView = dialog.findViewById(R.id.lifestyle)
        val food: TextView = dialog.findViewById(R.id.food)
        val travel:TextView = dialog.findViewById(R.id.travel)
        val grocery:TextView = dialog.findViewById(R.id.grocery)
        val software:TextView = dialog.findViewById(R.id.software)
        val it_services:TextView = dialog.findViewById(R.id.it_services)
        val legal_and_CA:TextView = dialog.findViewById(R.id.legal)
        val everythingelse:TextView =dialog.findViewById(R.id.everythingelse)
        var i=0
        while(i<offers.size){
            //condition to prevent null values
            if(offers.get(i).category!=null ) {
                //condition to display only those categories which are present in backend api
                when (offers.get(i).category.name) {
                    categorylistarray[0] -> entertainment.visibility=View.VISIBLE
                    categorylistarray[1] -> health.visibility=View.VISIBLE
                    categorylistarray[2] -> gaming.visibility=View.VISIBLE
                    categorylistarray[3] -> education.visibility=View.VISIBLE
                    categorylistarray[4] -> lifestyle.visibility=View.VISIBLE
                    categorylistarray[5] -> shopping.visibility=View.VISIBLE
                    categorylistarray[6] -> food.visibility=View.VISIBLE
                    categorylistarray[7] -> travel.visibility=View.VISIBLE
                    categorylistarray[8] -> grocery.visibility=View.VISIBLE
                    categorylistarray[9] -> software.visibility=View.VISIBLE
                    categorylistarray[10] -> it_services.visibility=View.VISIBLE
                    categorylistarray[11] -> legal_and_CA.visibility=View.VISIBLE
                    categorylistarray[12] -> everythingelse.visibility=View.VISIBLE
                }
            }
            i++
        }
        entertainment.setOnClickListener {
            setOffersAdapter(offers,"Entertainment")
            menubutton.text="Entertainment"
            dialog.dismiss()
        }
        health.setOnClickListener {
            setOffersAdapter(offers,"Health")
            menubutton.text="Health"
            dialog.dismiss()
        }
        gaming.setOnClickListener {
            setOffersAdapter(offers,"Gaming")
            menubutton.text="Gaming"
            dialog.dismiss()
        }
        education.setOnClickListener {
            setOffersAdapter(offers,"Education")
            menubutton.text="Education"
            dialog.dismiss()
        }
        lifestyle.setOnClickListener {
            setOffersAdapter(offers,"Lifestyle")
            menubutton.text="Lifestyle"
            dialog.dismiss()
        }
        shopping.setOnClickListener {
            setOffersAdapter(offers,"Shopping")
            menubutton.text="Shopping"
            dialog.dismiss()
        }
        food.setOnClickListener {
            setOffersAdapter(offers,"Food")
            menubutton.text="Food"
            dialog.dismiss()
        }
        travel.setOnClickListener {
            setOffersAdapter(offers,"Travel")
            menubutton.text="Travel"
            dialog.dismiss()
        }
        grocery.setOnClickListener {
            setOffersAdapter(offers,"Grocery")
            menubutton.text="Grocery"
            dialog.dismiss()
        }
        software.setOnClickListener {
            setOffersAdapter(offers,"Software")
            menubutton.text="Software"
            dialog.dismiss()
        }
        it_services.setOnClickListener {
            setOffersAdapter(offers,"IT Services")
            menubutton.text="IT Services"
            dialog.dismiss()
        }
        legal_and_CA.setOnClickListener {
            setOffersAdapter(offers,"Legal & CA")
            menubutton.text="Legal & CA"
            dialog.dismiss()
        }
        everythingelse.setOnClickListener {
            setOffersAdapter(offers,"Everything Else")
            menubutton.text="Everything Else"
            dialog.dismiss()
        }
        all.setOnClickListener {
            setOffersAdapter(offers,"All")
            menubutton.text="All"
            dialog.dismiss()
        }

        dialog.show()
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
//        walletUpdateInfoTv.visibility = View.GONE
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
                        walletUpdateInfoTv.visibility = View.GONE
                    } else {
                        SolocoinApp.sharedPrefs?.walletBalance?.let {
                            balanceTextView.text = it
                            walletUpdateInfoTv.visibility = View.GONE
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

    private fun setOffersAdapter(offers: ArrayList<Reward>,category: String) {
        // Remove event bus if already present on this fragment
        removeEventBus()
        rewardsRecyclerView.visibility = View.VISIBLE
        errorLabel.visibility = View.GONE
        errorTextView.visibility = View.GONE
        refreshTextView.visibility = View.GONE
//        var i=0
//        while(i<offers.size){
//            //condition to prevent null values
//            if(offers.get(i).category!=null ) {
//                //condition to prevent duplicate values
//                if(!categorylist.contains(offers.get(i).category.name))
//                categorylist.add(offers.get(i).category.name)
//            }
//            i++
//        }
        //fetching offers lying under the particular category

        var specificOffers:ArrayList<Reward> = ArrayList()
        var j=0
        while(j<offers.size){
            if(offers.get(j).category!=null && offers.get(j).category.name == category)
                specificOffers.add(offers.get(j))
            j++
        }

        if(specificOffers.size==0) {
            mListAdapter = RewardsListAdapter(context, offers)
            rewardsRecyclerView.adapter = mListAdapter
        }

        else{
            mListAdapter = RewardsListAdapter(context, specificOffers)
            rewardsRecyclerView.adapter = mListAdapter
        }
//         val categoryadapter: ArrayAdapter<String> = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, categorylist)
//        spinner.adapter=categoryadapter
        // Add event bus to listen to changes in RewardRedeemActivity for isClaimed variable
        addEventBus()
    }

    private fun fetchOffersSharedPrefs() {
        val offers = SolocoinApp.sharedPrefs?.offers
        if (offers != null) {
            setOffersAdapter(offers,getString(R.string.General))
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
            setOffersAdapter(offers,getString(R.string.General))
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
//                            val offers: ArrayList<Reward> = response.data
                            offers=response.data
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
                                setOffersAdapter(offers,getString(R.string.General))

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
