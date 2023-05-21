package com.calculator.vault.lock.hide.photo.video.ui.settings;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.calculator.vault.lock.hide.photo.video.BuildConfig;
import com.calculator.vault.lock.hide.photo.video.R;


public class AboutusActivity extends AppCompatActivity {

    private TextView version;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        initView();
    }

    private void initView() {
        version = findViewById(R.id.version);
        ivBack = findViewById(R.id.ivBack);
        version.setText("( " + BuildConfig.VERSION_NAME + " )");
        ivBack.setOnClickListener(view -> onBackPressed());
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
