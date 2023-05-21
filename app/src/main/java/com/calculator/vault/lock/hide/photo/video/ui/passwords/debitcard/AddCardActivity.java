package com.calculator.vault.lock.hide.photo.video.ui.passwords.debitcard;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.RackMonthPicker;
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Card_Data;
import com.calculator.vault.lock.hide.photo.video.listener.DateMonthDialogListener;
import com.calculator.vault.lock.hide.photo.video.listener.OnCancelMonthDialogListener;

import java.text.DecimalFormat;
import java.util.Locale;

public class AddCardActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText card_bank_name;
    private EditText card_type;
    private EditText card_number;
    private EditText card_holder;
    private TextView card_date;
    private EditText card_pin;
    private EditText card_cvv;
    private Button card_save;
    private Database database;
    private int type;
    int smonth, syear;
    boolean temp_date = false;
    private Card_Data card_datas;
    private FrameLayout adContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Card Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        type = getIntent().getIntExtra("type", 0);
        initView();
        initListener();
        //adContainer = findViewById(R.id.adContainer);
       // AdmobAdManager.getInstance(Add_Card_Activity.this).LoadAdaptiveBanner(Add_Card_Activity.this, adContainer, getString(R.string.banner_id), null);
        if (type == 1) {
            card_datas = (Card_Data) getIntent().getSerializableExtra("data");
            card_bank_name.setText(card_datas.getCard_bname());
            card_type.setText(card_datas.getCard_type());
            card_number.setText(card_datas.getCard_number());
            card_holder.setText(card_datas.getCard_holder());
            card_date.setText(card_datas.getCard_expire());
            card_pin.setText(card_datas.getCard_pin());
            card_cvv.setText(card_datas.getCard_cvv());
            card_save.setText("Update");
        } else if (type == 2) {
//            loadAds.showFullAdRandom(4,Add_Card_Activity.this);
            //showInter(4);
            card_pin.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            card_datas = (Card_Data) getIntent().getSerializableExtra("data");
            card_bank_name.setText(card_datas.getCard_bname());
            card_type.setText(card_datas.getCard_type());
            card_number.setText(card_datas.getCard_number());
            card_holder.setText(card_datas.getCard_holder());
            card_date.setText(card_datas.getCard_expire());
            card_pin.setText(card_datas.getCard_pin());
            card_cvv.setText(card_datas.getCard_cvv());
            card_bank_name.setEnabled(false);
            card_type.setInputType(InputType.TYPE_NULL);
            card_number.setInputType(InputType.TYPE_NULL);
            card_holder.setInputType(InputType.TYPE_NULL);
            card_date.setInputType(InputType.TYPE_NULL);
            card_pin.setInputType(InputType.TYPE_NULL);
            card_cvv.setInputType(InputType.TYPE_NULL);
            card_date.setOnClickListener(null);
            card_save.setVisibility(View.GONE);
        }else{
//            loadAds.showFullAd(Add_Card_Activity.this);
           // showInter();
        }
    }
//    private void showInter(){
//        AdmobAdManager.getInstance(this).loadInterstitialAd(this, getString(R.string.interstitial_id), 1, new AdmobAdManager.OnAdClosedListener() {
//            @Override
//            public void onAdClosed() {
//
//            }
//        });
//    }

//    private void showInter(int n){
//        AdmobAdManager.getInstance(this).loadInterstitialAd(this, getString(R.string.interstitial_id), n, new AdmobAdManager.OnAdClosedListener() {
//            @Override
//            public void onAdClosed() {
//
//            }
//        });
//    }

    private void initExpireDate() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (temp_date) {
            new RackMonthPicker(this)
                    .setLocale(Locale.ENGLISH)
                    .setSelectedMonth(smonth)
                    .setSelectedYear(syear)
                    .setColorTheme(getResources().getColor(R.color.colorAccent))
                    .setPositiveButton(new DateMonthDialogListener() {
                        @Override
                        public void onDateMonth(int month, int startDate, int endDate, int year, String monthLabel) {
                            smonth = month;
                            syear = year;
                            card_date.setText(new DecimalFormat("00").format(month) + "/" + year);
                            temp_date = true;
                        }
                    })
                    .setNegativeButton(dialog -> dialog.dismiss()).show();
        } else {
            new RackMonthPicker(this)
                    .setLocale(Locale.ENGLISH)
                    .setColorTheme(getResources().getColor(R.color.colorAccent))
                    .setPositiveButton((month, startDate, endDate, year, monthLabel) -> {
                        smonth = month;
                        syear = year;
                        card_date.setText(new DecimalFormat("00").format(month) + "/" + year);
                        temp_date = true;
                    })
                    .setNegativeButton(new OnCancelMonthDialogListener() {
                        @Override
                        public void onCancel(AlertDialog dialog) {
                            dialog.dismiss();
                        }
                    }).show();
        }

    }

    private void initListener() {
        card_save.setOnClickListener(this);
        card_date.setOnClickListener(this);
    }

    private void initView() {
        card_bank_name = findViewById(R.id.card_bank_name);
        card_type = findViewById(R.id.card_type);
        card_number = findViewById(R.id.card_number);
        card_holder = findViewById(R.id.card_holder);
        card_date = findViewById(R.id.card_date);
        card_pin = findViewById(R.id.card_pin);
        card_cvv = findViewById(R.id.card_cvv);

        card_save = findViewById(R.id.card_save);

        database = new Database(AddCardActivity.this);
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
            case R.id.card_save:
                if (card_bank_name.getText().toString().trim().length() > 0) {
                    if (card_type.getText().toString().trim().length() > 0) {
                        if (card_number.getText().toString().trim().length() > 0) {
                            if (card_holder.getText().toString().trim().length() > 0) {
                                if (temp_date) {
                                    if (card_pin.getText().toString().trim().length() > 0) {
                                        if (card_cvv.getText().toString().trim().length() > 0) {
                                            Card_Data card_data = new Card_Data();
                                            card_data.setCard_bname(card_bank_name.getText().toString().trim());
                                            card_data.setCard_type(card_type.getText().toString().trim());
                                            card_data.setCard_number(card_number.getText().toString().trim());
                                            card_data.setCard_holder(card_holder.getText().toString().trim());
                                            card_data.setCard_expire(card_date.getText().toString().trim());
                                            card_data.setCard_pin(card_pin.getText().toString().trim());
                                            card_data.setCard_cvv(card_cvv.getText().toString().trim());
                                            if (type == 0) {
                                                database.addCard(card_data);
                                            } else if (type == 1) {
                                                card_data.setId(card_datas.getId());
                                                database.updateCard(card_data);
                                            }
                                            finish();
//                                            loadAds.showFullAd();
                                        } else {
                                            card_cvv.setError("Enter card cvv");
                                        }
                                    } else {
                                        card_pin.setError("Enter card pin");
                                    }
                                } else {
                                    Toast.makeText(this, "Select card expire date", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                card_holder.setError("Enter card holder name");
                            }
                        } else {
                            card_number.setError("Enter card number");
                        }
                    } else {
                        card_type.setError("Enter card type");
                    }
                } else {
                    card_bank_name.setError("Enter card bank name");
                }
                break;
            case R.id.card_date:
                initExpireDate();
                break;
        }
    }
}
