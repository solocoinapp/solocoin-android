package app.solocoin.solocoin.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import app.solocoin.solocoin.R
import kotlinx.android.synthetic.main.activity_onboard.*

class OnboardActivity : AppCompatActivity(), ViewPager.OnPageChangeListener, View.OnClickListener {

    companion object {
        private const val FRAG_COUNT = 3
    }

    private var VISIBLE_FRAG_INDEX = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard)

        tv_get_started.setOnClickListener(this)
        tv_skip.setOnClickListener(this)

        val adapter = OnboardFragmentAdapter(supportFragmentManager)
        view_pager?.adapter = adapter
        view_pager?.addOnPageChangeListener(this)

        page_indicator?.setViewPager(view_pager)
    }

    class OnboardFragmentAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return OnboardFragment.instance(position)
        }
        override fun getCount(): Int {
            return FRAG_COUNT
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        VISIBLE_FRAG_INDEX = position
        when (position) {
            0 -> tv_get_started.text = getString(R.string.get_started)
            1 -> tv_get_started.text = getString(R.string.next)
            2 -> tv_get_started.text = getString(R.string.create_account)
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_get_started -> {
                when (VISIBLE_FRAG_INDEX) {
                    0 -> view_pager?.setCurrentItem(VISIBLE_FRAG_INDEX+1, true)
                    1 -> view_pager?.setCurrentItem(VISIBLE_FRAG_INDEX+1, true)
                    2 -> startActivity(Intent(this, LoginSignupActivity::class.java))
                }
            }
            R.id.tv_skip -> startActivity(Intent(this, LoginSignupActivity::class.java))
        }
    }
}
