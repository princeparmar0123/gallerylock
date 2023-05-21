package com.calculator.vault.lock.hide.photo.video.ui.cloud;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.calculator.vault.lock.hide.photo.video.service.BackgroundService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.calculator.vault.lock.hide.photo.video.App;
import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.common.utils.DriveServiceHelper;
import com.suke.widget.SwitchButton;

import java.util.Collections;


public class BackupActivity extends AppCompatActivity implements View.OnClickListener {

    private DriveServiceHelper driveServiceHelper;
    private Button log_out;
    private TextView user_name;
    private TextView user_email;
    private SwitchButton sync_all;
    private SwitchButton sync_wifi;
    private LinearLayout drivefolder;
    private TextView email;


  //  private FrameLayout adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_backup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Cloud Backup");

       // adContainer = findViewById(R.id.adContainer);
       // AdmobAdManager.getInstance(Backup_Activity.this).LoadAdaptiveBanner(Backup_Activity.this, adContainer, getString(R.string.banner_id), null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initListener();
    }

    private void initListener() {
        log_out.setOnClickListener(this);
        drivefolder.setOnClickListener(this);
    }

    private void initView() {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        GoogleAccountCredential googleAccountCredential = GoogleAccountCredential.usingOAuth2(BackupActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));
        googleAccountCredential.setSelectedAccount(googleSignInAccount.getAccount());
        Drive drive = new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                googleAccountCredential)
                .setApplicationName(getResources().getString(R.string.app_name))
                .build();
        driveServiceHelper = new DriveServiceHelper(drive);

        log_out = findViewById(R.id.log_out);
        sync_all = findViewById(R.id.sync_all);
        sync_wifi = findViewById(R.id.sync_wifi);
        drivefolder = findViewById(R.id.drivefolder);
        email = findViewById(R.id.email);

        sync_all.setChecked(App.Companion.getInstance().getPref().getSyncOn());
        sync_wifi.setChecked(App.Companion.getInstance().getPref().getSyncWifi());
        final ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        email.setText(googleSignInAccount.getEmail());
        /*user_profile = (CircleImageView) findViewById(R.id.user_profile);
        user_name = (TextView) findViewById(R.id.user_name);
        user_email = (TextView) findViewById(R.id.user_email);

        user_name.setText("Name: " + googleSignInAccount.getDisplayName());
        user_email.setText("Email: " + googleSignInAccount.getEmail());
        Glide.with(Backup_Activity.this).load(googleSignInAccount.getPhotoUrl()).into(user_profile);*/
        sync_all.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked) {
                App.Companion.getInstance().getPref().setSyncOn(true);
                if (App.Companion.getInstance().getPref().getSyncWifi()) {
                    if (wifi.isConnectedOrConnecting()) {
                        Intent intent = new Intent(BackupActivity.this, BackgroundService.class);
                        startService(intent);
                    }
                } else {
                    Intent intent = new Intent(BackupActivity.this, BackgroundService.class);
                    startService(intent);
                }

            } else {
                App.Companion.getInstance().getPref().setSyncOn(false);
                Intent intent = new Intent(BackupActivity.this, BackgroundService.class);
                stopService(intent);
            }
        });
        sync_wifi.setOnCheckedChangeListener((view, isChecked) -> {
            Intent intent = new Intent(BackupActivity.this, BackgroundService.class);
            stopService(intent);
            if (isChecked) {
                App.Companion.getInstance().getPref().setSyncWifi(true);
                if (wifi.isConnectedOrConnecting()) {
                    Intent intent1 = new Intent(BackupActivity.this, BackgroundService.class);
                    startService(intent1);
                }
            } else {
                App.Companion.getInstance().getPref().setSyncWifi(false);
                Intent intent1 = new Intent(BackupActivity.this, BackgroundService.class);
                startService(intent1);
            }
        });
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
            case R.id.log_out:
                GoogleSignInOptions gso = new GoogleSignInOptions.
                        Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                        build();
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(BackupActivity.this, gso);
                googleSignInClient.signOut();
                Intent intent = new Intent(BackupActivity.this, CloudActivity.class);
                startActivity(intent);
                App.Companion.getInstance().getPref().setSyncOn(false);
                App.Companion.getInstance().getPref().setSyncWifi(false);
                Toast.makeText(this, "successfully Sign Out Account", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.drivefolder:
                Intent intent1 = new Intent(BackupActivity.this, DrivefolderActivity.class);
                startActivity(intent1);
                break;
        }

    }
}
