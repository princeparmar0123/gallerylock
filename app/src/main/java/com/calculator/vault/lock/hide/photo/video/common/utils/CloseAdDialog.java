package com.calculator.vault.lock.hide.photo.video.common.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.graphics.drawable.DrawableCompat;

import com.calculator.vault.lock.hide.photo.video.App;
import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.rating.RotationRatingBar;


public class CloseAdDialog extends Dialog {
    public Activity mActivity;
    public Dialog mDialog;

    Button closeDialog;

    onExitClick listener;
    RotationRatingBar ratingBar;
    Button rate_btn;
    float rating_count = 0;
    TextView descriptionOfRating, title;
    LinearLayout lout_exit, lout_rate;

   // int theme1[];
   // int themeColor = 0;

    public interface onExitClick {
        void onClick();
    }

    public CloseAdDialog(Activity mActivity, onExitClick listener) {
        super(mActivity);
        this.mActivity = mActivity;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.close_ad_dialog);

//        TypedArray grandiant1Array = mActivity.getResources().obtainTypedArray(R.array.bubble_gradiant1);
//        theme1 = new int[grandiant1Array.length()];
//        for (int i = 0; i < grandiant1Array.length(); i++) {
//            theme1[i] = grandiant1Array.getColor(i, 0);
//        }
       // themeColor = Application.getBubbleColor();

        title = findViewById(R.id.title);
        descriptionOfRating = findViewById(R.id.descriptionOfRating);
        closeDialog = findViewById(R.id.closeDialog);
        ratingBar = findViewById(R.id.ratingBar);
        rate_btn = findViewById(R.id.rate_btn);
        lout_exit = findViewById(R.id.lout_exit);
        lout_rate = findViewById(R.id.lout_rate);

//        TextView txt_title_rate = findViewById(R.id.txt_title_rate);
//        txt_title_rate.setTextColor(Color.parseColor("#bdbdbd"));
//        closeDialog.setTextColor(Color.parseColor("#bdbdbd"));
//        setBgColor(closeDialog);
//        setBgColor(rate_btn);



        if (App.Companion.getInstance().getRate()) {
            lout_exit.setVisibility(View.VISIBLE);
            lout_rate.setVisibility(View.GONE);
            title.setText(mActivity.getString(R.string.exit));
            closeDialog.setText(mActivity.getString(R.string.cancel));
            rate_btn.setText(mActivity.getString(R.string.exit));
            descriptionOfRating.setText(mActivity.getString(R.string.description_of_exit));
            ratingBar.setVisibility(View.GONE);
        } else {
            lout_exit.setVisibility(View.GONE);
            lout_rate.setVisibility(View.VISIBLE);
            ratingBar.setVisibility(View.VISIBLE);
            rate_btn.setVisibility(View.VISIBLE);
        }

        ratingBar.setOnRatingChangeListener((ratingBar, rating, fromUser) -> rating_count = rating);

        rate_btn.setOnClickListener(view -> {
            if (App.Companion.getInstance().getRate()) {
                listener.onClick();
                dismiss();
            } else {
                if (rating_count >= 4) {
                    App.Companion.getInstance().putRate(true);
                    String url = "https://play.google.com/store/apps/details?id=" + mActivity.getPackageName();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    mActivity.startActivity(i);
                    dismiss();
                } else if (rating_count <= 3 && ratingBar.getRating() > 0) {
                    App.Companion.getInstance().putRate(true);
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                            Uri.parse("mailto:" + Uri.encode(mActivity.getResources().getString(R.string.send_email))));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, mActivity.getResources().getString(R.string.app_name));

                    try {
                        mActivity.startActivity(Intent.createChooser(emailIntent, mActivity.getString(R.string.send_email_view)));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(mActivity,
                                mActivity.getString(R.string.no_email_installed), Toast.LENGTH_SHORT)
                                .show();
                    }
                    dismiss();
                } else {
                    Toast.makeText(mActivity, mActivity.getString(R.string.error_of_rating), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        closeDialog.setOnClickListener(view -> {
            if (App.Companion.getInstance().getRate()) {
                dismiss();
            } else {
                listener.onClick();
                dismiss();
            }
        });

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void setBgColor(Button view) {
        Drawable buttonDrawable = view.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, Color.parseColor("#bdbdbd"));
        view.setBackground(buttonDrawable);
    }
}
