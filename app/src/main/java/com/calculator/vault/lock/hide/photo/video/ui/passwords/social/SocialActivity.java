package com.calculator.vault.lock.hide.photo.video.ui.passwords.social;

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
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Social_Data;
import com.calculator.vault.lock.hide.photo.video.floatingbutton.FloatingActionButton;

import java.util.ArrayList;

public class SocialActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView social_recycler;
    private FloatingActionButton floating_btn_social;
    private Database database;
    ArrayList<Social_Data> social_data = new ArrayList<>();
    public static LinearLayout no_social;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Social");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initListener();
        initAdapter();
    }

    private void initAdapter() {
        if (social_data.size() > 0) {
            social_recycler.setVisibility(View.VISIBLE);
            no_social.setVisibility(View.GONE);
            SocialAdapter social_adapter = new SocialAdapter(SocialActivity.this, social_data);
            social_recycler.setLayoutManager(new LinearLayoutManager(SocialActivity.this, LinearLayoutManager.VERTICAL, false));
            social_recycler.setAdapter(social_adapter);
        } else {
            social_recycler.setVisibility(View.GONE);
            no_social.setVisibility(View.VISIBLE);
        }

    }

    private void initListener() {
        floating_btn_social.setOnClickListener(this);
    }

    private void initView() {
        social_data.clear();
        social_recycler = (RecyclerView) findViewById(R.id.social_recycler);
        no_social = (LinearLayout) findViewById(R.id.no_social);
        floating_btn_social = (FloatingActionButton) findViewById(R.id.floating_btn_social);

        database = new Database(SocialActivity.this);
        social_data.addAll(database.getAllSocial());

//        loadAds.showFullAdRandom(3,Social_Activity.this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_btn_social:
                Intent intent = new Intent(SocialActivity.this, AddSocialActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        social_data.clear();
        social_data.addAll(database.getAllSocial());
        initAdapter();
    }
}
