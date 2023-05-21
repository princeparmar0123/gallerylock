package com.calculator.vault.lock.hide.photo.video.ui.passwords.bank;

import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Bank_Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddBankActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText bank_name;
    private EditText bank_acno;
    private EditText bank_holder;
    private EditText bank_type;
    private EditText bank_ifsc;
    private EditText bank_swift;
    private EditText bank_email;
    private EditText bank_pass;
    private EditText bank_trapass;
    private EditText bank_userid;
    private Button bank_save;
    private Database database;
    private int type;
    private Bank_Data bank_datas;
    private EditText bank_url;
    private FrameLayout adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add Bank");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        type = getIntent().getIntExtra("type", 0);
        adContainer = findViewById(R.id.adContainer);
        initView();
        initListener();
        if (type == 1) {
            bank_datas = (Bank_Data) getIntent().getSerializableExtra("data");
            bank_name.setText(bank_datas.getBank_name());
            bank_acno.setText(bank_datas.getAccount_nummber());
            bank_holder.setText(bank_datas.getHolder_name());
            bank_type.setText(bank_datas.getAccount_type());
            bank_ifsc.setText(bank_datas.getIfsc_code());
            bank_swift.setText(bank_datas.getSwift_code());
            bank_email.setText(bank_datas.getEmail());
            bank_userid.setText(bank_datas.getUser_id());
            bank_pass.setText(bank_datas.getPass());
            bank_trapass.setText(bank_datas.getTrapass());
            bank_url.setText(bank_datas.getUrl());
            bank_save.setText("Update");
        } else if (type == 2) {

            bank_pass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            bank_datas = (Bank_Data) getIntent().getSerializableExtra("data");
            bank_name.setText(bank_datas.getBank_name());
            bank_acno.setText(bank_datas.getAccount_nummber());
            bank_holder.setText(bank_datas.getHolder_name());
            bank_type.setText(bank_datas.getAccount_type());
            bank_ifsc.setText(bank_datas.getIfsc_code());
            bank_swift.setText(bank_datas.getSwift_code());
            bank_email.setText(bank_datas.getEmail());
            bank_userid.setText(bank_datas.getUser_id());
            bank_pass.setText(bank_datas.getPass());
            bank_trapass.setText(bank_datas.getTrapass());
            bank_url.setText(bank_datas.getUrl());
            bank_name.setInputType(InputType.TYPE_NULL);
            bank_acno.setInputType(InputType.TYPE_NULL);
            bank_holder.setInputType(InputType.TYPE_NULL);
            bank_type.setInputType(InputType.TYPE_NULL);
            bank_ifsc.setInputType(InputType.TYPE_NULL);
            bank_swift.setInputType(InputType.TYPE_NULL);
            bank_email.setInputType(InputType.TYPE_NULL);
            bank_userid.setInputType(InputType.TYPE_NULL);
            bank_pass.setInputType(InputType.TYPE_NULL);
            bank_trapass.setInputType(InputType.TYPE_NULL);
            bank_url.setInputType(InputType.TYPE_NULL);
            bank_save.setVisibility(View.GONE);
        }else{

        }
    }





    private void initListener() {
        bank_save.setOnClickListener(this);
    }

    private void initView() {
        bank_name = findViewById(R.id.bank_name);
        bank_acno = findViewById(R.id.bank_acno);
        bank_holder = findViewById(R.id.bank_holder);
        bank_type = findViewById(R.id.bank_type);
        bank_ifsc = findViewById(R.id.bank_ifsc);
        bank_swift = findViewById(R.id.bank_swift);
        bank_email = findViewById(R.id.bank_email);
        bank_userid = findViewById(R.id.bank_userid);
        bank_pass = findViewById(R.id.bank_pass);
        bank_trapass = findViewById(R.id.bank_trapass);
        bank_url = findViewById(R.id.bank_url);

        bank_save = findViewById(R.id.bank_save);

        database = new Database(AddBankActivity.this);
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
            case R.id.bank_save:
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                Pattern pattern = Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE);
                if (bank_name.getText().toString().trim().length() > 0) {
                    if (bank_acno.getText().toString().trim().length() > 0) {
                        if (bank_holder.getText().toString().trim().length() > 0) {
                            if (bank_type.getText().toString().trim().length() > 0) {
                                if (bank_ifsc.getText().toString().trim().length() > 0) {
                                    if (bank_swift.getText().toString().trim().length() > 0) {
                                        if (bank_email.getText().toString().trim().length() > 0) {
                                            Matcher matcher = pattern.matcher(bank_email.getText().toString());
                                            if (matcher.matches() == true) {
                                                if (bank_userid.getText().toString().trim().length() > 0) {
                                                    if (bank_pass.getText().toString().trim().length() > 0) {
                                                        if (bank_trapass.getText().toString().trim().length() > 0) {
                                                            if (bank_trapass.getText().toString().trim().length() > 0) {
                                                                Bank_Data bank_data = new Bank_Data();
                                                                bank_data.setBank_name(bank_name.getText().toString().trim());
                                                                bank_data.setAccount_nummber(bank_acno.getText().toString().trim());
                                                                bank_data.setHolder_name(bank_holder.getText().toString().trim());
                                                                bank_data.setAccount_type(bank_type.getText().toString().trim());
                                                                bank_data.setIfsc_code(bank_ifsc.getText().toString().trim());
                                                                bank_data.setSwift_code(bank_swift.getText().toString().trim());
                                                                bank_data.setEmail(bank_email.getText().toString().trim());
                                                                bank_data.setUser_id(bank_userid.getText().toString().trim());
                                                                bank_data.setPass(bank_pass.getText().toString().trim());
                                                                bank_data.setTrapass(bank_trapass.getText().toString().trim());
                                                                bank_data.setUrl(bank_url.getText().toString().trim());
                                                                if (type == 0) {
                                                                    database.addBank(bank_data);
                                                                } else if (type == 1) {
                                                                    bank_data.setId(bank_datas.getId());
                                                                    database.updateBank(bank_data);
                                                                    Toast.makeText(this, "Bank Data Updated...", Toast.LENGTH_SHORT).show();
                                                                }
                                                                finish();
//                                                                loadAds.showFullAd();
                                                            } else {
                                                                bank_url.setError("Enter bank website");
                                                            }
                                                        } else {
                                                            bank_trapass.setError("Enter transaction password");
                                                        }
                                                    } else {
                                                        bank_pass.setError("Enter login password");
                                                    }
                                                } else {
                                                    bank_userid.setError("Enter user id");
                                                }
                                            } else {
                                                bank_email.setError("Enter correct email");
                                            }
                                        } else {
                                            bank_email.setError("Enter email");
                                        }
                                    } else {
                                        bank_swift.setError("Enter swift code");
                                    }
                                } else {
                                    bank_ifsc.setError("Enter IFSC code");
                                }
                            } else {
                                bank_type.setError("Enter account type");
                            }
                        } else {
                            bank_holder.setError("Enter holder name");
                        }
                    } else {
                        bank_acno.setError("Enter account number");
                    }
                } else {
                    bank_name.setError("Enter bank name");
                }
                break;
        }
    }
}
