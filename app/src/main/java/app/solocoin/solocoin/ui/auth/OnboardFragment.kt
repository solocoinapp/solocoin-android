package app.solocoin.solocoin.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
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

        idx = requireArguments().getInt(FRAG_INDEX)

        val ivIntro = mView.findViewById<ImageView>(R.id.iv_intro)
        val tvIntroH1 = mView.findViewById<TextView>(R.id.tv_intro_h1)
        val tvIntroH2 = mView.findViewById<TextView>(R.id.tv_intro_h2)

        when (idx) {
            0 -> {
                ivIntro.setImageResource(R.mipmap.intro_one)
                tvIntroH1.text = getString(R.string.intro_one_h1)
                tvIntroH2.text = getString(R.string.intro_one_h2)
            }
            1 -> {
                ivIntro.setImageResource(R.mipmap.intro_two)
                tvIntroH1.text = getString(R.string.intro_two_h1)
                tvIntroH2.text = getString(R.string.intro_two_h2)
            }
            2 -> {
                ivIntro.setImageResource(R.mipmap.intro_three)
                tvIntroH1.text = getString(R.string.intro_three_h1)
                tvIntroH2.text = getString(R.string.intro_three_h2)
            }
        }

        return mView
    }
}
