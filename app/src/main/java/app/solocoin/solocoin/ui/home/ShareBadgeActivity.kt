package app.solocoin.solocoin.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import app.solocoin.solocoin.R
import app.solocoin.solocoin.model.Badge
import app.solocoin.solocoin.util.GlobalUtils
import com.google.android.material.button.MaterialButton

class ShareBadgeActivity : AppCompatActivity() {

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_badge)

        val badge: Badge = intent.extras?.getParcelable("EXTRA_INFO")!!

        findViewById<TextView>(R.id.badge_name).apply {
            text = badge.name.capitalize()
        }
        findViewById<TextView>(R.id.badge_level).apply {
            text = ("Level ${badge.level}")
        }
        findViewById<TextView>(R.id.one_liner).apply {
            text = badge.oneLiner.capitalize()
        }
        findViewById<ImageView>(R.id.badge_iv).apply {
            if (badge.level == "1") {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
                GlobalUtils.loadImageNetworkCachePlaceholder(
                    getString(R.string.image_base_url) + badge.imageUrl,
                    this
                )
            }
        }
        findViewById<MaterialButton>(R.id.share).apply {
            val message =
                "I just earned ${(badge.name).capitalize()} on SoloCoin app which rewards you based on your location from home, mall, store and parks. Earn real world rewards with Solocoin. Challenge friends and achieve milestones and badges like me.\n\nDownload the app now: ${getString(
                    R.string.app_link
                )}"
//            val imageUri = Uri.parse(getString(R.string.image_base_url) + badge.imageUrl)
            setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND)
//                shareIntent.type = "image/jpeg"
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, message)
//                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
//                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(
                    Intent.createChooser(
                        shareIntent,
                        context.getString(R.string.invite_title)
                    )
                )
            }
        }
    }
}

