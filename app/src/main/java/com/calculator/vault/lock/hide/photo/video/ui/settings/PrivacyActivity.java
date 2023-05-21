package com.calculator.vault.lock.hide.photo.video.ui.settings;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.calculator.vault.lock.hide.photo.video.R;

public class PrivacyActivity extends AppCompatActivity {

    private WebView privacy_web;
    private ProgressBar privacy_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Privacy Policy");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }

    private void initView() {
        privacy_web = (WebView) findViewById(R.id.privacy_web);
        privacy_web.setWebViewClient(new MyWebViewClient());
        openURL();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
            privacy_web.setVisibility(View.VISIBLE);
        }
    }

    public void openURL() {
        privacy_web.loadUrl("https://www.privacypolicycenter.com/view.php?v=ZGdZMWMzc3lOK0F2Umo1NmFBZWZWUT09&n=Calculator-Vault-:-Hide-Photo,-Video");
        privacy_web.requestFocus();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return false;

    }
}
