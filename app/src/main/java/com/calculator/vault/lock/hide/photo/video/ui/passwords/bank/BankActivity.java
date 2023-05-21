package com.calculator.vault.lock.hide.photo.video.ui.passwords.bank;

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
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Bank_Data;
import com.calculator.vault.lock.hide.photo.video.floatingbutton.FloatingActionButton;

import java.util.ArrayList;

public class BankActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView bank_recycler;
    private FloatingActionButton floating_btn_bank;
    ArrayList<Bank_Data> bank_data = new ArrayList<>();
    private Database database;
    public static LinearLayout no_bank;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Bank");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initListener();
        initAdapter();
    }

    private void initAdapter() {
        if (bank_data.size() > 0) {
            bank_recycler.setVisibility(View.VISIBLE);
            no_bank.setVisibility(View.GONE);
            BankAdapter bank_adapter = new BankAdapter(BankActivity.this, bank_data);
            bank_recycler.setLayoutManager(new LinearLayoutManager(BankActivity.this, LinearLayoutManager.VERTICAL, false));
            bank_recycler.setAdapter(bank_adapter);
        } else {
            no_bank.setVisibility(View.VISIBLE);
            bank_recycler.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        floating_btn_bank.setOnClickListener(this);
    }

    private void initView() {
        bank_data.clear();
        bank_recycler = (RecyclerView) findViewById(R.id.bank_recycler);
        no_bank = (LinearLayout) findViewById(R.id.no_bank);
        floating_btn_bank = (FloatingActionButton) findViewById(R.id.floating_btn_bank);

        database = new Database(BankActivity.this);
        bank_data.addAll(database.getAllBank());

//        loadAds.showFullAdRandom(3,Bank_Activity.this);

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
            case R.id.floating_btn_bank:
                Intent intent = new Intent(BankActivity.this, AddBankActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bank_data.clear();
        bank_data.addAll(database.getAllBank());
        initAdapter();
    }
}
