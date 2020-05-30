package app.solocoin.solocoin.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import app.solocoin.solocoin.R
import app.solocoin.solocoin.model.Badge
import app.solocoin.solocoin.util.GlobalUtils
import com.google.android.material.button.MaterialButton

class ShareBadgeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_badge)

        val badge: Badge = intent.extras?.getParcelable("EXTRA_INFO")!!

        findViewById<TextView>(R.id.badge_name).apply {
            text = badge.name
        }
        findViewById<TextView>(R.id.badge_level).apply {
            text = badge.level
        }
        findViewById<TextView>(R.id.one_liner).apply {
            text = badge.oneLiner
        }
        findViewById<ImageView>(R.id.badge_iv).apply {
            GlobalUtils.loadImageNetworkCachePlaceholder(badge.imageUrl, this)
        }
        findViewById<MaterialButton>(R.id.share).apply {
            setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    context.getString(R.string.badge_invite_subject)
                )
                shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    context.getString(R.string.badge_invite_message_start) + badge.name + context.getString(
                        R.string.badge_invite_message_end
                    )
                )
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
