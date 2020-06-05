package app.solocoin.solocoin.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import app.solocoin.solocoin.R
import app.solocoin.solocoin.model.SessionPingRequest
import app.solocoin.solocoin.repo.SolocoinRepository
import app.solocoin.solocoin.util.AppDialog
import app.solocoin.solocoin.util.GlobalUtils
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.fragment_app_dialog_snf.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.BuildConfig
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class ProfileFragment : Fragment(), KoinComponent {

    private val repository: SolocoinRepository by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //invite-btn
        view.findViewById<TextView>(R.id.tv_invite).setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.invite_subject))
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.invite_message, BuildConfig.APPLICATION_ID))
            startActivity(Intent.createChooser(shareIntent, getString(R.string.invite_title)))
        }
        //invite-btn

        //privacy-policy-btn
        view.findViewById<TextView>(R.id.tv_pp).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_pp))))
        }
        //privacy-policy-btn

        //terms-condition-btn
        view.findViewById<TextView>(R.id.tv_tnc).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_tnc))))
        }
        //terms-condition-btn

        //logout-btn
        view.findViewById<TextView>(R.id.tv_logout).setOnClickListener {
                val logoutDialog = AppDialog.instance(
                    getString(R.string.confirm),
                    getString(R.string.tag_logout),
                    object : AppDialog.AppDialogListener {
                        override fun onClickConfirm() {
                            if (GlobalUtils.isNetworkAvailable(requireActivity())) {
                                // updating backend for user logout
                                val body: JsonObject =
                                    JsonParser().parse(SessionPingRequest("away").toString()).asJsonObject
                                val call: Call<JsonObject> = repository.pingSession(body)
                                call.enqueue(object : Callback<JsonObject?> {
                                    override fun onResponse(
                                        call: Call<JsonObject?>,
                                        response: Response<JsonObject?>
                                    ) {
                                        Log.d("Logout", "Successful")
                                    }

                                    override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                                        Log.d("Logout", "Failure updating backend")
                                    }

                                })
                                GlobalUtils.logout(context!!, activity!!)
//                            SolocoinApp.sharedPrefs?.let{
//                                it.loggedIn = false
//                            }
                                activity?.finish()
                            } else {
                                tv_title.text = "Sorry"
                                tv_subtitle.text = getString(R.string.logout_issue)
                            }
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

    companion object {
        fun instance() = ProfileFragment()
    }
}
