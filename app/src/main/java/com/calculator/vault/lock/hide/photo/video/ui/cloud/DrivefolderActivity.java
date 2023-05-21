package com.calculator.vault.lock.hide.photo.video.ui.cloud;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;


import com.google.android.material.tabs.TabLayout;
import com.calculator.vault.lock.hide.photo.video.R;

public class DrivefolderActivity extends AppCompatActivity {

    private TabLayout tablayout;
    private ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivefolder);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Drive Folder");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        getSupportActionBar().setElevation(0);
    }

    private void initView() {
        tablayout = (TabLayout) findViewById(R.id.tablayout);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        tablayout.addTab(tablayout.newTab().setText("Photos"));
        tablayout.addTab(tablayout.newTab().setText("Videos"));
        tablayout.setTabGravity(TabLayout.GRAVITY_FILL);

        DrivefolderAdapter drivefolder_adapter = new DrivefolderAdapter(getSupportFragmentManager(), tablayout.getTabCount());
        viewpager.setAdapter(drivefolder_adapter);

        viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

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
