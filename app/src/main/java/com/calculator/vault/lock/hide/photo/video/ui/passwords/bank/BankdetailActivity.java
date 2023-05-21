package com.calculator.vault.lock.hide.photo.video.ui.passwords.bank;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.calculator.vault.lock.hide.photo.video.R;

public class BankdetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView bank_title;
    private TextView bank_website;
    private ImageView bank_copy;
    private ImageView bank_link;
    private String name;
    private String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankdetail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Bank Redirect");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initListener();
    }

    private void initListener() {
        bank_copy.setOnClickListener(this);
        bank_link.setOnClickListener(this);
    }

    private void initView() {
        bank_title = (TextView) findViewById(R.id.bank_title);
        bank_website = (TextView) findViewById(R.id.bank_website);
        bank_copy = (ImageView) findViewById(R.id.bank_copy);
        bank_link = (ImageView) findViewById(R.id.bank_link);

        name = getIntent().getStringExtra("name");
        link = getIntent().getStringExtra("link");

        bank_title.setText(name);
        bank_website.setText(link);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bank_copy:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", bank_title.getText().toString().trim());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Text Copied...!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bank_link:
                Intent httpIntent = new Intent(Intent.ACTION_VIEW);
                httpIntent.setData(Uri.parse(link));
                if (httpIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(httpIntent);
                } else {
                    Toast.makeText(this, "Not Valid Website...!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }
}
