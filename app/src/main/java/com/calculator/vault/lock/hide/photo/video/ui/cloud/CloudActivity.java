package com.calculator.vault.lock.hide.photo.video.ui.cloud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

;
import com.calculator.vault.lock.hide.photo.video.service.BackgroundService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.drive.DriveScopes;
import com.calculator.vault.lock.hide.photo.video.App;
import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.common.utils.DriveServiceHelper;

public class CloudActivity extends AppCompatActivity implements View.OnClickListener {

    private Button link_drive;
    DriveServiceHelper driveServiceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Cloud Backup");
        initView();
        initListener();
        if(App.Companion.getInstance().getPref().getSyncOn()){
            loginData();
        }
    }

    private void initListener() {
        link_drive.setOnClickListener(this);
    }

    private void initView() {
        link_drive = (Button) findViewById(R.id.link_drive);
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
            case R.id.link_drive:
                requestSignin();
                break;
        }
    }

    private void requestSignin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        startActivityForResult(googleSignInClient.getSignInIntent(), 100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                handleSignInIntent(data);
                if (data != null) {
                    data.getAction();
                }
                break;
        }
    }

    private void handleSignInIntent(Intent data) {
        GoogleSignIn.getSignedInAccountFromIntent(data).addOnSuccessListener(googleSignInAccount -> {
            Log.e("asd", "onSucces: " + googleSignInAccount.getDisplayName());
            loginData();
        }).addOnFailureListener(e -> {
            Log.e("asd", "onFailure: " + e.getMessage());
            e.printStackTrace();
        });
    }

    private void loginData() {
        Intent intent = new Intent(this, BackgroundService.class);
        startService(intent);

        Intent intent1 = new Intent(CloudActivity.this, BackupActivity.class);
        startActivity(intent1);
        App.Companion.getInstance().getPref().setSyncOn(true);
       // App.Companion.getInstance().getPref().setSyncWifi(false);
        finish();
    }
}

