package app.solocoin.solocoin.ui.home

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.model.Profile
import app.solocoin.solocoin.repo.SolocoinRepository
import app.solocoin.solocoin.util.AppDialog
import app.solocoin.solocoin.util.GlobalUtils
import app.solocoin.solocoin.util.enums.Status
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent
import org.koin.core.inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class ProfileFragment : Fragment(), KoinComponent {

    private val repository: SolocoinRepository by inject()
    private val viewModel: AllScratchCardsViewModel by viewModel()
    private val TAG="ProfileFragment"
    private lateinit var profile : Profile
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //invite-btn
        view.findViewById<TextView>(R.id.tv_invite).setOnClickListener {
            //the below method will be used for invite & earn functionality
//            createlink()
            var rewardline="fh"
            viewModel.getProfile().observe(viewLifecycleOwner, Observer { response ->
                //Log.d(TAG, "$response")
                Log.d(TAG,"userprofile:"+response.data)
                when (response.status) {
                    Status.SUCCESS -> {

                        profile=response.data!!
                        rewardline="\nEnter Promocode "+profile.referral.refercode+" to earn "+
                                profile.referral.amount +" Solocoins as Referral bonus!!"
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.invite_subject))
                        shareIntent.putExtra(
                                Intent.EXTRA_TEXT,
                                getString(R.string.invite_message) + getString(R.string.app_link)+rewardline
                        )
                        startActivity(Intent.createChooser(shareIntent, getString(R.string.invite_title)))
                    }
                    Status.ERROR -> {
                    }
                    Status.LOADING -> {
                    }
                }
            })

        }
        //invite-btn
        //Redeemed Rewards btn
        view.findViewById<TextView>(R.id.redeemed_rewards).setOnClickListener {
            val intent=Intent(context,AllScratchCardsActivity::class.java)
            intent.putExtra("onlyredeemedrewards",true)
            startActivity(intent)
        }
        //end Redeemed Rewards btn
        // get free coins
        view.findViewById<TextView>(R.id.free_coins).setOnClickListener {
            val intent =Intent(context,GetFreeCoinsActivity::class.java)
            startActivity(intent)
        }


        // end get free coins

        //privacy-policy-btn
        view.findViewById<TextView>(R.id.tv_pp).setOnClickListener {
            val intent = Intent(context, AppGuideActivity::class.java)
            intent.putExtra("link",getString(R.string.url_pp))
            startActivity(intent)
//            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_pp))))
        }
        //privacy-policy-btn

        //terms-condition-btn
        view.findViewById<TextView>(R.id.tv_tnc).setOnClickListener {
            val intent = Intent(context, AppGuideActivity::class.java)
            intent.putExtra("link",getString(R.string.url_tnc))
            startActivity(intent)
//            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_tnc))))
        }

        view.findViewById<TextView>(R.id.tv_guide).setOnClickListener {

            val intent = Intent(context, AppGuideActivity::class.java)
            intent.putExtra("link",getString(R.string.url_guide))
            startActivity(intent)
//            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_guide))))
        }
        //terms-condition-btn

        //logout-btn
        view.findViewById<TextView>(R.id.tv_logout).setOnClickListener {
            val logoutDialog = AppDialog.instance(
                getString(R.string.confirm),
                getString(R.string.tag_logout),
                object : AppDialog.AppDialogListener {
                    override fun onClickConfirm() {
                        GlobalUtils.logout(context!!, activity!!)
                        activity?.finish()
                    }

                    override fun onClickCancel() {}
                },
                getString(R.string.logout),
                getString(R.string.cancel)
            )
            logoutDialog.show(childFragmentManager, logoutDialog.tag)
        }
        //logout-btn
    }

    private fun createlink() {

            val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse(getString(R.string.app_link)+sharedPrefs?.mobileNumber))
                    .setDomainUriPrefix("https://solocoin.page.link") // Open links with this app on Android
                    .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build()) // Open links with com.example.ios on iOS
//                    .setIosParameters(IosParameters.Builder("com.example.ios").build())
                    .buildDynamicLink()
            val dynamicLinkUri = dynamicLink.uri
            Log.i("Tagkarandeep", "link:$dynamicLinkUri")
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT, dynamicLinkUri.toString())
                intent.type = "text/plain"
                startActivity(intent)
            //manual
//            val sharelinktext = "https://karandeep.page.link/?" +
//                    "link=" + "https://www.solocoin.app/karadeepid=27" +
//                    "&apn=" + getPackageName() +
//                    "&st=" + "My refer link" +
//                    "&sd=" + "Reward Coins 20" +
//                    "&si=" + "https://www.solocoin.app/wp-content/uploads/2020/04/horizontal-logo1.png"


//        https://karandeep.page.link?apn=com.karandeep.referandearn&ibi=com.example.ios&link=https%3A%2F%2Fwww.solocoin.app%2F
            //shorten the link

//            val shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink() //                .setLongLink(dynamicLinkUri)
//                    .setLongLink(Uri.parse(sharelinktext)) //manual
//                    .buildShortDynamicLink()
//                    .addOnCompleteListener(this, OnCompleteListener<ShortDynamicLink> { task ->
//                        if (task.isSuccessful) {
//                            // Short link created
//                            val shortLink = task.result!!.shortLink
//                            val flowchartLink = task.result!!.previewLink
//                            Log.i("Tagkarandeep2", "link: $shortLink")
//                            val intent = Intent()
//                            intent.action = Intent.ACTION_SEND
//                            intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
//                            intent.type = "text/plain"
//                            startActivity(intent)
//                        } else {
//                            // Error
//                            // ...
//                            Log.i("error", "error")
//                        }
//                    })
    }

    companion object {
        fun instance() = ProfileFragment()
    }
}
