package app.solocoin.solocoin.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat

import app.solocoin.solocoin.R

/**
 * Created by Aditya Sonel on 26/04/20.
 */

/**
 * Fragment used for showing on-boarding greeting,
 * this fragment is bind to
 * @see OnboardActivity
 */
class OnboardFragment : Fragment() {

    companion object {
        private const val FRAG_INDEX = "FRAG_INDEX"

        fun instance(idx: Int) = OnboardFragment().apply {
            val bundle = Bundle()
            bundle.putInt(FRAG_INDEX, idx)
            arguments = bundle
        }
    }

    private var idx = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mView = inflater.inflate(R.layout.fragment_onboard, container, false)

        idx = arguments!!.getInt(FRAG_INDEX)

        val ivIntro = mView.findViewById<ImageView>(R.id.iv_intro)
        val tvIntro = mView.findViewById<TextView>(R.id.tv_intro)

        when (idx) {
            0 -> {
                ivIntro.setImageResource(R.mipmap.intro_one)
                tvIntro.text = getString(R.string.intro_one)
            }
            1 -> {
                ivIntro.setImageResource(R.mipmap.intro_two)
                tvIntro.text = getString(R.string.intro_two)
            }
            2 -> {
                ivIntro.setImageResource(R.mipmap.intro_three)
                tvIntro.text = getString(R.string.intro_three)
            }
        }

        return mView
    }
}
