package com.calculator.vault.lock.hide.photo.video.ui.settings;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.calculator.vault.lock.hide.photo.video.BuildConfig;
import com.calculator.vault.lock.hide.photo.video.R;
import com.suke.widget.SwitchButton;

public class HideappActivity extends AppCompatActivity {

    private SwitchButton hideapp_switch;
    ComponentName LAUNCHER_COMPONENT_NAME = new ComponentName(BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID + ".ui.SplashActivity");

    public static int REQUEST_PERMISSIONS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hideapp);
       // Amplitude.getInstance().logEvent("Create Hideapp_Activity");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Hide App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initListener();
    }

    private void initListener() {
        hideapp_switch.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked) {
                fn_hideicon();
            } else {
                fn_unhide();
            }
        });
    }

    private void initView() {
        hideapp_switch = findViewById(R.id.hideapp_switch);
        hideapp_switch.setChecked(!isLauncherIconVisible());
    }

    private boolean isLauncherIconVisible() {
        int enabledSetting = getPackageManager().getComponentEnabledSetting(LAUNCHER_COMPONENT_NAME);
        return enabledSetting != PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
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

    private void fn_hideicon() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Important!");
        builder.setMessage("To launch the app again, Open Mail and go to find out this app Hiper Link and click.");
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            getPackageManager().setComponentEnabledSetting(LAUNCHER_COMPONENT_NAME, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            Intent i = new Intent(Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_SUBJECT, "Calculator Vault Link");
            i.putExtra(Intent.EXTRA_TEXT, "https://calcVault.com");
            i.setType("text/html");
            i.setPackage("com.google.android.gm");
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(HideappActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.show();
    }

    private void fn_unhide() {
        getPackageManager().setComponentEnabledSetting(LAUNCHER_COMPONENT_NAME, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}
