package com.calculator.vault.lock.hide.photo.video.ui.passwords.email;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Email_Data;
import com.calculator.vault.lock.hide.photo.video.floatingbutton.FloatingActionButton;

import java.util.ArrayList;

public class EmailActivity extends AppCompatActivity implements View.OnClickListener {
    ArrayList<Email_Data> email_data = new ArrayList<>();
    private FloatingActionButton floating_btn_email;
    private RecyclerView email_recycler;
    private Database database;
    public static LinearLayout no_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Email");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initListener();
        initAdapter();
    }

    private void initAdapter() {
        if (email_data.size() > 0) {
            email_recycler.setVisibility(View.VISIBLE);
            no_email.setVisibility(View.GONE);
            EmailAdapter email_adapter = new EmailAdapter(EmailActivity.this, email_data);
            email_recycler.setLayoutManager(new LinearLayoutManager(EmailActivity.this, LinearLayoutManager.VERTICAL, false));
            email_recycler.setAdapter(email_adapter);
        } else {
            email_recycler.setVisibility(View.GONE);
            no_email.setVisibility(View.VISIBLE);
        }
    }

    private void initListener() {
        floating_btn_email.setOnClickListener(this);
    }

    private void initView() {
        email_data.clear();
        email_recycler = (RecyclerView) findViewById(R.id.email_recycler);
        no_email = (LinearLayout) findViewById(R.id.no_email);
        floating_btn_email = (FloatingActionButton) findViewById(R.id.floating_btn_email);

        database = new Database(EmailActivity.this);
        email_data.addAll(database.getAllEmail());

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_btn_email:
                Intent intent = new Intent(EmailActivity.this, AddEmailActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        email_data.clear();
        email_data.addAll(database.getAllEmail());
        initAdapter();
    }
}
