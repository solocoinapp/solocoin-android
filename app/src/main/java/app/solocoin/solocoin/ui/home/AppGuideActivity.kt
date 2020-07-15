package app.solocoin.solocoin.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import app.solocoin.solocoin.R
import kotlinx.android.synthetic.main.activity_app_guide.*
/**
 * Created by Karandeep Singh on 10/07/2020
 */
class AppGuideActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_guide)

        val intent = getIntent()
        val link = intent.getStringExtra("link")
        val webSettings = webview.getSettings()
        webSettings.javaScriptEnabled = true
        val webViewClient = MyWebViewClient()
        webview.setWebViewClient(webViewClient)
        webview.loadUrl(link)
    }
    inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.indexOf("solocoin.app") > -1) return false
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
         startActivity(intent)
            return true
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webview!!.canGoBack()) {
            webview!!.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}