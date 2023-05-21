package com.calculator.vault.lock.hide.photo.video.ui.passwords.debitcard;

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
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Card_Data;
import com.calculator.vault.lock.hide.photo.video.floatingbutton.FloatingActionButton;

import java.util.ArrayList;

public class CardActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView card_recycler;
    private FloatingActionButton floating_btn_card;
    ArrayList<Card_Data> card_data = new ArrayList<>();
    private Database database;
    public static LinearLayout no_card;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Debit/Credit Card");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initListener();
        initAdapter();
    }

    private void initAdapter() {
        if (card_data.size() > 0) {
            card_recycler.setVisibility(View.VISIBLE);
            no_card.setVisibility(View.GONE);
            CardAdapter card_adapter = new CardAdapter(CardActivity.this, card_data);
            card_recycler.setLayoutManager(new LinearLayoutManager(CardActivity.this, LinearLayoutManager.VERTICAL, false));
            card_recycler.setAdapter(card_adapter);
        } else {
            no_card.setVisibility(View.VISIBLE);
            card_recycler.setVisibility(View.GONE);
        }

    }

    private void initListener() {
        floating_btn_card.setOnClickListener(this);
    }

    private void initView() {
        card_recycler = (RecyclerView) findViewById(R.id.card_recycler);
        no_card = (LinearLayout) findViewById(R.id.no_card);
        floating_btn_card = (FloatingActionButton) findViewById(R.id.floating_btn_card);

        database = new Database(CardActivity.this);

        card_data.clear();
        card_data.addAll(database.getAllCard());

//        loadAds.showFullAdRandom(3,Card_Activity.this);
        //showInter(3);
    }

//    private void showInter(int n){
//                AdmobAdManager.getInstance(this).loadInterstitialAd(this, getString(R.string.interstitial_id), n, new AdmobAdManager.OnAdClosedListener() {
//            @Override
//            public void onAdClosed() {
//
//            }
//        });
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_btn_card:
                Intent intent = new Intent(CardActivity.this, AddCardActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
                break;
        }
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
    protected void onResume() {
        super.onResume();
        card_data.clear();
        card_data.addAll(database.getAllCard());
        initAdapter();
    }
}
