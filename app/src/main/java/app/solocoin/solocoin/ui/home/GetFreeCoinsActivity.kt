package app.solocoin.solocoin.ui.home

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.lifecycle.Observer
import android.widget.TextView
import android.widget.Toast
import app.solocoin.solocoin.R
import app.solocoin.solocoin.app.SolocoinApp
import app.solocoin.solocoin.repo.NoConnectivityException
import app.solocoin.solocoin.util.AppDialog
import app.solocoin.solocoin.util.enums.Status
import kotlinx.android.synthetic.main.activity_get_free_coins.*
import kotlinx.android.synthetic.main.fragment_quiz.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

@ExperimentalCoroutinesApi
@InternalCoroutinesApi

class GetFreeCoinsActivity : AppCompatActivity() {
    private lateinit var context: GetFreeCoinsActivity
    private val TAG ="GetFreeCoinsActivity"
    private val viewModel: ProfileViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_free_coins)
        context=this
        apply.setOnClickListener {
            viewModel.redeemcoupon(coupon.text.toString()).observe(this, Observer { response ->
                Log.d(TAG,"promocodetest:"+response)
                Log.d(TAG,"promocodetest:"+response.status)
                when (response.status) {
                    Status.SUCCESS -> {
                        when (response.code) {
                            200->{
                                showDialog()
                            }
                            422->{
                                coupon.setError("Invalid Coupon Code!")
                            }
                        }
                        Log.d(TAG,"promocodetest:"+response)
                    }
                    Status.ERROR -> {
                        coupon.setError("Invalid Coupon Code!")
                    }
                    Status.LOADING -> {
                    }
                }
            })
        }


//            if(valid(coupon.text.toString())) {
//                showDialog()
////                Toast.makeText(context, "Coupon Code Applied Successfully!!" +
////                        "\nReward coins will be credited in your wallet shortly",
////                        Toast.LENGTH_LONG).show()
//            }
//            else {
//               // Toast.makeText(context, "Coupon Code is Invalid!!", Toast.LENGTH_LONG).show()
//                coupon.setError("Invalid Coupon Code!")
//            }


        back_arrow.setOnClickListener {
            finish()
        }
    }

    private fun showDialog() {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_coupon)
        val okbtn:TextView=dialog.findViewById(R.id.okbtn)
        okbtn.setOnClickListener {
            finish()
        }
        dialog.show()
    }

    private fun valid(text: String?): Boolean {
            //Check if the coupon is valid from backend
        if(text?.length==6) return true
        else return false
    }

}