package app.solocoin.solocoin.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer

import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp
import app.solocoin.solocoin.util.GlobalUtils
import app.solocoin.solocoin.util.enums.Status
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel

class WalletFragment : Fragment() {

    private var mView: View? = null
    @InternalCoroutinesApi
    private val viewModel: HomeFragmentViewModel by viewModel()
    private val TAG = "WalletFragment"


    @InternalCoroutinesApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView =  inflater.inflate(R.layout.fragment_wallet, container, false)
        var walletValueTV = mView?.findViewById<TextView>(R.id.walletValue);
        viewModel.userData().observe(viewLifecycleOwner, Observer { response ->
            Log.d(TAG, "$response")
            when(response.status) {
                Status.SUCCESS -> {
                    val walletAmount = response.data?.get("wallet_balance")?.asString
                    if (walletAmount != null) {
                        walletValueTV?.text = walletAmount
                        SolocoinApp.sharedPrefs?.walletBalance = walletAmount
                    }
                }
                Status.ERROR -> {
                    if (SolocoinApp.sharedPrefs?.homeDuration != 0L) {
                        walletValueTV?.text = SolocoinApp.sharedPrefs?.walletBalance
                    }
                }
                Status.LOADING -> {}
            }
        })
        return mView
    }

    companion object {
        fun instance() = WalletFragment().apply {}
    }
}
