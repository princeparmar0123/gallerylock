package com.calculator.vault.lock.hide.photo.video.ui.passwords.email;

import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Email_Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddEmailActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email_name;
    private EditText email;
    private EditText email_pass;
    private Button email_save;
    private Database database;
    private int type;
    private Email_Data email_datas;

    private FrameLayout adContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_email);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add Email");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        type = getIntent().getIntExtra("type", 0);

        initView();
        initListener();
        if (type == 1) {
            email_datas = (Email_Data) getIntent().getSerializableExtra("data");
            email_name.setText(email_datas.getName());
            email.setText(email_datas.getEmail());
            email_pass.setText(email_datas.getPassword());
            email_save.setText("Update");
        } else if (type == 2) {
//            loadAds.showFullAdRandom(4,Add_Email_Activity.this);
            email_pass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            email_datas = (Email_Data) getIntent().getSerializableExtra("data");
            email_name.setText(email_datas.getName());
            email.setText(email_datas.getEmail());
            email_pass.setText(email_datas.getPassword());
            email_name.setInputType(InputType.TYPE_NULL);
            email.setInputType(InputType.TYPE_NULL);
            email_pass.setInputType(InputType.TYPE_NULL);
            email_save.setVisibility(View.GONE);
        }else{
//            loadAds.showFullAd(Add_Email_Activity.this);

        }
    }



    private void initListener() {
        email_save.setOnClickListener(this);
    }

    private void initView() {
        email_name = findViewById(R.id.email_name);
        email = findViewById(R.id.email);
        email_pass = findViewById(R.id.email_pass);
        email_save = findViewById(R.id.email_save);

        database = new Database(AddEmailActivity.this);
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
            case R.id.email_save:
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                Pattern pattern = Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE);
                if (email_name.getText().toString().trim().length() > 0) {
                    if (email.getText().toString().trim().length() > 0) {
                        Matcher matcher = pattern.matcher(email.getText().toString());
                        if (matcher.matches() == true) {
                            if (email_pass.getText().toString().trim().length() > 0) {
                                Email_Data email_data = new Email_Data();
                                email_data.setName(email_name.getText().toString().trim());
                                email_data.setEmail(email.getText().toString().trim());
                                email_data.setPassword(email_pass.getText().toString().trim());
                                if (type == 0) {
                                    database.addEmail(email_data);
                                } else if (type == 1) {
                                    email_data.setId(email_datas.getId());
                                    database.updateEmail(email_data);
                                }
                                finish();
//                                loadAds.showFullAd();
                            } else {
                                email_pass.setError("Enter password");
                            }
                        } else {
                            email.setError("Enter correct email");
                        }
                    } else {
                        email.setError("Enter email");
                    }
                } else {
                    email_name.setError("Enter name");
                }
                break;
        }
    }
}
