package app.solocoin.solocoin.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import app.solocoin.solocoin.R

class WalletFragment : Fragment() {

    private var mView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView =  inflater.inflate(R.layout.fragment_wallet, container, false)
        return mView
    }

    companion object {
        fun instance() = WalletFragment().apply {}
    }
}
