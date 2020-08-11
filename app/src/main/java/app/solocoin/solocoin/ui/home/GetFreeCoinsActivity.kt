package app.solocoin.solocoin.ui.home

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import app.solocoin.solocoin.R
import kotlinx.android.synthetic.main.activity_get_free_coins.*

class GetFreeCoinsActivity : AppCompatActivity() {
    private lateinit var context: GetFreeCoinsActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_free_coins)
        context=this
        apply.setOnClickListener {
            if(valid(coupon.text.toString())) {
                showDialog()
//                Toast.makeText(context, "Coupon Code Applied Successfully!!" +
//                        "\nReward coins will be credited in your wallet shortly",
//                        Toast.LENGTH_LONG).show()
            }
            else {
               // Toast.makeText(context, "Coupon Code is Invalid!!", Toast.LENGTH_LONG).show()
                coupon.setError("Invalid Coupon Code!")
            }
        }

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