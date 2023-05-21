package com.calculator.vault.lock.hide.photo.video.ui.passwords.social;

import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Social_Data;
import com.calculator.vault.lock.hide.photo.video.common.utils.Constant;
import com.calculator.vault.lock.hide.photo.video.ui.video.SpinnerAdapter;

import java.util.ArrayList;

public class AddSocialActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Spinner spinner_social;
    private EditText social_type;
    private Button social_save;
    private EditText social_name;
    private EditText social_userid;
    private EditText social_pass;
    private int type;
    int temp = -1;
    private Social_Data social_datas;
    private Database database;

    private FrameLayout adContainer;

    ArrayList<String> social = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_social);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add Social Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        type = getIntent().getIntExtra("type", 0);
        adContainer = findViewById(R.id.adContainer);

        initView();
        initListener();
        initAdapter();
        if (type == 1) {
            social_datas = (Social_Data) getIntent().getSerializableExtra("data");
            for (int i = 0; i < Constant.social.length; i++) {
                if (Constant.social[i].equals(social_datas.getSocial_type())) {
                    temp = i;
                    break;
                }
            }
            if (temp == -1 || temp == 6) {
                social_type.setVisibility(View.VISIBLE);
                spinner_social.setSelection(6);
                social_type.setText(social_datas.getSocial_type());
            } else {
                social_type.setVisibility(View.GONE);
                spinner_social.setSelection(temp);
            }
            social_name.setText(social_datas.getSocial_name());
            social_userid.setText(social_datas.getSocial_email());
            social_pass.setText(social_datas.getSocial_pass());
            social_save.setText("Update");
        } else if (type == 2) {
//            loadAds.showFullAdRandom(4,Add_Social_Activity.this);
            social_pass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            social_datas = (Social_Data) getIntent().getSerializableExtra("data");
            for (int i = 0; i < Constant.social.length; i++) {
                if (Constant.social[i].equals(social_datas.getSocial_type())) {
                    temp = i;
                    break;
                }
            }
            if (temp == -1 || temp == 6) {
                social_type.setVisibility(View.VISIBLE);
                spinner_social.setSelection(6);
                social_type.setText(social_datas.getSocial_type());
            } else {
                social_type.setVisibility(View.GONE);
                spinner_social.setSelection(temp);
            }
            social_name.setText(social_datas.getSocial_name());
            social_userid.setText(social_datas.getSocial_email());
            social_pass.setText(social_datas.getSocial_pass());
            social_type.setInputType(InputType.TYPE_NULL);
            social_name.setInputType(InputType.TYPE_NULL);
            social_userid.setInputType(InputType.TYPE_NULL);
            social_pass.setInputType(InputType.TYPE_NULL);
            spinner_social.setEnabled(false);
            social_save.setVisibility(View.GONE);
        } else {
//            loadAds.showFullAd(Add_Social_Activity.this);

        }
    }





    private void initAdapter() {
        social.clear();
        for (int i = 0; i < Constant.social.length; i++) {
            social.add(Constant.social[i]);
        }
        SpinnerAdapter spinner_adapter = new SpinnerAdapter(AddSocialActivity.this, social);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_social.setAdapter(spinner_adapter);
    }

    private void initListener() {
        social_save.setOnClickListener(this);
        spinner_social.setOnItemSelectedListener(this);
    }

    private void initView() {
        spinner_social = findViewById(R.id.spinner_social);
        social_type = findViewById(R.id.social_type);
        social_name = findViewById(R.id.social_name);
        social_userid = findViewById(R.id.social_userid);
        social_pass = findViewById(R.id.social_pass);

        social_save = findViewById(R.id.social_save);
        database = new Database(AddSocialActivity.this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.social_save) {
            if (social_name.getText().toString().trim().length() > 0) {
                if (social_userid.getText().toString().trim().length() > 0) {
                    if (social_pass.getText().toString().trim().length() > 0) {
                        if (spinner_social.getSelectedItemPosition() == 6) {
                            if (social_type.getText().toString().trim().length() > 0) {
                                addData(social_type.getText().toString().trim());
                            } else {
                                social_type.setError("Enter social type");
                            }
                        } else {
                            addData(spinner_social.getSelectedItem().toString());
                        }
                    } else {
                        social_pass.setError("Enter password");
                    }
                } else {
                    social_userid.setError("Enter email");
                }

            } else {
                social_name.setError("Enter name");
            }
        }
    }

    private void addData(String trim) {
        Social_Data social_data = new Social_Data();
        social_data.setSocial_type(trim);
        social_data.setSocial_name(social_name.getText().toString().trim());
        social_data.setSocial_email(social_userid.getText().toString().trim());
        social_data.setSocial_pass(social_pass.getText().toString().trim());
        if (type == 0) {
            database.addSocial(social_data);
        } else if (type == 1) {
            social_data.setId(social_datas.getId());
            database.updateSocial(social_data);
        }
        finish();
//        loadAds.showFullAd();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 6) {
            social_type.setVisibility(View.VISIBLE);
        } else {
            social_type.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
