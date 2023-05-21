package com.calculator.vault.lock.hide.photo.video.ui.passwords;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Bank_Data;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Card_Data;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Email_Data;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Social_Data;
import com.calculator.vault.lock.hide.photo.video.ui.passwords.bank.BankActivity;
import com.calculator.vault.lock.hide.photo.video.ui.passwords.debitcard.CardActivity;
import com.calculator.vault.lock.hide.photo.video.ui.passwords.email.EmailActivity;
import com.calculator.vault.lock.hide.photo.video.ui.passwords.social.SocialActivity;


import java.util.ArrayList;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.ViewHolder> {
    private final Database database;
    PasswordsActivity activity;
    String[] password_name;
    LayoutInflater inflater;
    int[] password_icon;

    public PasswordAdapter(PasswordsActivity password_activity, String[] password_name, int[] password_icon) {
        activity = password_activity;
        this.password_name = password_name;
        this.password_icon = password_icon;
        database = new Database(activity);
        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_password, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (position == 0) {
            ArrayList<Card_Data> temp_card = new ArrayList<>();
            temp_card.clear();
            temp_card.addAll(database.getAllCard());
            if (temp_card.size() != 0) {
                holder.pass_count.setVisibility(View.VISIBLE);
                holder.pass_count.setText(String.valueOf(temp_card.size()));
            } else {
                holder.pass_count.setVisibility(View.GONE);
            }
        } else if (position == 1) {
            ArrayList<Bank_Data> temp_bank = new ArrayList<>();
            temp_bank.clear();
            temp_bank.addAll(database.getAllBank());
            if (temp_bank.size() != 0) {
                holder.pass_count.setVisibility(View.VISIBLE);
                holder.pass_count.setText(String.valueOf(temp_bank.size()));
            } else {
                holder.pass_count.setVisibility(View.GONE);
            }
        } else if (position == 2) {
            ArrayList<Social_Data> temp_social = new ArrayList<>();
            temp_social.clear();
            temp_social.addAll(database.getAllSocial());
            if (temp_social.size() != 0) {
                holder.pass_count.setVisibility(View.VISIBLE);
                holder.pass_count.setText(String.valueOf(temp_social.size()));
            } else {
                holder.pass_count.setVisibility(View.GONE);
            }
        } else if (position == 3) {
            ArrayList<Email_Data> temp_email = new ArrayList<>();
            temp_email.clear();
            temp_email.addAll(database.getAllEmail());
            if (temp_email.size() != 0) {
                holder.pass_count.setVisibility(View.VISIBLE);
                holder.pass_count.setText(String.valueOf(temp_email.size()));
            } else {
                holder.pass_count.setVisibility(View.GONE);
            }
        } else if (position == 4) {
//            ArrayList<Other_Data> temp_other = new ArrayList<>();
//            temp_other.clear();
//            temp_other.addAll(database.getAllOther());
//            if (temp_other.size() != 0) {
//                holder.pass_count.setVisibility(View.VISIBLE);
//                holder.pass_count.setText(String.valueOf(temp_other.size()));
//            } else {
//                holder.pass_count.setVisibility(View.GONE);
//            }
        }
        holder.pass_name.setText(password_name[position]);
        Glide.with(activity).load(password_icon[position]).into(holder.pass_icon);
        holder.pass_card.setOnClickListener(v -> {
            switch (position) {
                case 0:
                        Intent intent = new Intent(activity, CardActivity.class);
                        activity.startActivity(intent);
                    break;
                case 1:
                        Intent intent1 = new Intent(activity, BankActivity.class);
                        activity.startActivity(intent1);
                    break;
                case 2:
                        Intent intent2 = new Intent(activity, SocialActivity.class);
                        activity.startActivity(intent2);
                    break;
                case 3:
                        Intent intent3 = new Intent(activity, EmailActivity.class);
                        activity.startActivity(intent3);
                    break;
                case 4:
//                        Intent intent4 = new Intent(activity, Other_Activity.class);
//                        activity.startActivity(intent4);
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return password_name.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pass_icon;
        TextView pass_name;
        TextView pass_count;
        CardView pass_card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pass_icon = (ImageView) itemView.findViewById(R.id.pass_icon);
            pass_name = (TextView) itemView.findViewById(R.id.pass_name);
            pass_count = (TextView) itemView.findViewById(R.id.pass_count);
            pass_card = (CardView) itemView.findViewById(R.id.pass_card);
        }
    }
}
