package app.solocoin.solocoin.ui.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp.Companion.sharedPrefs
import app.solocoin.solocoin.util.GlobalUtils
import app.solocoin.solocoin.util.enums.Status
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class MyProfileActivity : AppCompatActivity() {
    private val viewModel: HomeFragmentViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        val my_avatar = sharedPrefs?.avatar
        when(my_avatar){
            "bluedude" ->{
                avatar.setImageDrawable(getDrawable(R.drawable.bluedude))
            }
            "bluelady" ->{
                avatar.setImageDrawable(getDrawable(R.drawable.bluelady))
            }
            "greendude" ->{
                avatar.setImageDrawable(getDrawable(R.drawable.greendude))
            }
            "jacketdude" ->{
                avatar.setImageDrawable(getDrawable(R.drawable.jacketdude))
            }
            "orangedude" ->{
                avatar.setImageDrawable(getDrawable(R.drawable.orangedude))
            }
            "orangelady" ->{
                avatar.setImageDrawable(getDrawable(R.drawable.orangelady))
            }
            "striped" ->{
                avatar.setImageDrawable(getDrawable(R.drawable.striped))
            }
            "yellowlady" ->{
                avatar.setImageDrawable(getDrawable(R.drawable.yellowlady))
            }
        }
        viewModel.getProfile().observe(this, Observer { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    name.text=response.data?.name
                    mobile.text=response.data?.mobile
                    coins.text=response.data?.wallet_balance
                    home_time.text= GlobalUtils.formattedHomeDuration(response.data?.home_duration_in_seconds?.toLong())
                    offers_redeemed.text=response.data?.redeemed_rewards?.size.toString()
                    refercode.text=response.data?.referral?.refercode
                }
                Status.ERROR -> {}
                Status.LOADING -> {}
                }
        })
        avatar.setOnClickListener {
            showDialog()
        }
        back_arrow.setOnClickListener {
            finish()
        }
    }
    fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.avatar_dialog)
        val bluedude:ImageView=dialog.findViewById(R.id.bluedude)
        val bluelady:ImageView=dialog.findViewById(R.id.bluelady)
        val greendude:ImageView=dialog.findViewById(R.id.greendude)
        val jacketdude:ImageView=dialog.findViewById(R.id.jacketdude)
        val orangedude:ImageView=dialog.findViewById(R.id.orangedude)
        val orangelady:ImageView=dialog.findViewById(R.id.orangelady)
        val striped:ImageView=dialog.findViewById(R.id.striped)
        val yellowlady:ImageView=dialog.findViewById(R.id.yellowlady)

        bluedude.setOnClickListener {
            sharedPrefs?.avatar = "bluedude"
            avatar.setImageDrawable(getDrawable(R.drawable.bluedude))
            dialog.dismiss()
        }
        bluelady.setOnClickListener {
            sharedPrefs?.avatar = "bluelady"
            avatar.setImageDrawable(getDrawable(R.drawable.bluelady))
            dialog.dismiss()
        }
        greendude.setOnClickListener {
            sharedPrefs?.avatar = "greendude"
            avatar.setImageDrawable(getDrawable(R.drawable.greendude))
            dialog.dismiss()
        }
        jacketdude.setOnClickListener {
            sharedPrefs?.avatar = "jacketdude"
            avatar.setImageDrawable(getDrawable(R.drawable.jacketdude))
            dialog.dismiss()
        }
        orangedude.setOnClickListener {
            sharedPrefs?.avatar = "orangedude"
            avatar.setImageDrawable(getDrawable(R.drawable.orangedude))
            dialog.dismiss()
        }
        orangelady.setOnClickListener {
            sharedPrefs?.avatar = "orangelady"
            avatar.setImageDrawable(getDrawable(R.drawable.orangelady))
            dialog.dismiss()
        }
        striped.setOnClickListener {
            sharedPrefs?.avatar = "striped"
            avatar.setImageDrawable(getDrawable(R.drawable.striped))
            dialog.dismiss()
        }
        yellowlady.setOnClickListener {
            sharedPrefs?.avatar = "yellowlady"
            avatar.setImageDrawable(getDrawable(R.drawable.yellowlady))
            dialog.dismiss()
        }
        dialog.show()
    }
}