package app.solocoin.solocoin.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import app.solocoin.solocoin.R
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.fragment_quiz.*

/**
 * Created by Ankur Kumar on 14/05/20
 */

class QuizFragment : Fragment(), View.OnClickListener{

    private var mView : View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_quiz, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        option1_card.setOnClickListener(this)
        option2_card.setOnClickListener(this)
        option3_card.setOnClickListener(this)
        option4_card.setOnClickListener(this)



    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.option1_card -> verifyAnswer(option1_card)
            R.id.option2_card -> verifyAnswer(option2_card)
            R.id.option3_card -> verifyAnswer(option3_card)
            R.id.option4_card -> verifyAnswer(option4_card)
        }
    }

    private fun verifyAnswer(optionCard: MaterialCardView) {
        optionCard.strokeColor = ResourcesCompat.getColor(resources, R.color.green_stroke_color, null)

        option1_card.apply {
            setOnClickListener(null)
            isClickable = false
        }
        option2_card.apply {
            setOnClickListener(null)
            isClickable = false
        }
        option3_card.apply {
            setOnClickListener(null)
            isClickable = false
        }
        option4_card.apply {
            setOnClickListener(null)
            isClickable = false
        }

    }

}