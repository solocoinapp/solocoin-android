package app.solocoin.solocoin.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import app.solocoin.solocoin.R;

public class AppGuideActivity extends AppCompatActivity {
    private WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_guide);
        webview=findViewById(R.id.webview);
        Intent intent =getIntent();
        String link=intent.getStringExtra("link");
        WebSettings webSettings =webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        MyWebViewClient webViewClient=new MyWebViewClient();
        webview.setWebViewClient(webViewClient);
        webview.loadUrl(link);
    }
    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.indexOf("solocoin.app")>-1) return false;
            Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode==KeyEvent.KEYCODE_BACK) && webview.canGoBack() ) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}