package com.calculator.vault.lock.hide.photo.video.ui.passwords.email;



import static com.calculator.vault.lock.hide.photo.video.ui.passwords.email.EmailActivity.no_email;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;


import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Email_Data;

import java.util.ArrayList;

public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.ViewHolder> {
    private final Database database;
    EmailActivity activity;
    ArrayList<Email_Data> email_data;
    LayoutInflater inflater;

    public EmailAdapter(EmailActivity email_activity, ArrayList<Email_Data> email_data) {
        activity = email_activity;
        this.email_data = email_data;
        database = new Database(activity);
        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_email, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.email_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(activity, holder.email_option);
                popup.inflate(R.menu.menu_bank);
                popup.getMenu().findItem(R.id.menu_view).setVisible(false);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_edit:
                                Intent intent = new Intent(activity, AddEmailActivity.class);
                                intent.putExtra("type", 1);
                                intent.putExtra("data", email_data.get(position));
                                activity.startActivity(intent);
                                break;
                            case R.id.menu_delete:
                                AlertDialog deletedialog = new AlertDialog.Builder(activity)
                                        .setTitle("Delete")
                                        .setMessage("Are you sure,you want to delete this email detail?")
                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                database.deleteEmail(email_data.get(position).getId());
                                                email_data.remove(position);
                                                notifyDataSetChanged();
                                                if (email_data.size() == 0) {
                                                    no_email.setVisibility(View.VISIBLE);
                                                } else {
                                                    no_email.setVisibility(View.GONE);
                                                }
                                                dialog.dismiss();
                                            }
                                        })
                                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .create();
                                deletedialog.show();
                                break;
                        }
                        return true;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
        holder.email_show_name.setText(email_data.get(position).getName());
//        holder.email_show.setText(email_data.get(position).getEmail());
//        holder.email_show_pass.setText(email_data.get(position).getPassword());
    }

    @Override
    public int getItemCount() {
        return email_data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView email_show_name;
        //        TextView email_show;
//        TextView email_show_pass;
        ImageView email_option;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            email_show_name = (TextView) itemView.findViewById(R.id.email_show_name);
//            email_show = (TextView) itemView.findViewById(R.id.email_show);
//            email_show_pass = (TextView) itemView.findViewById(R.id.email_show_pass);
            email_option = (ImageView) itemView.findViewById(R.id.email_option);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, AddEmailActivity.class);
                    intent.putExtra("type", 2);
                    intent.putExtra("data", email_data.get(getAdapterPosition()));
                    activity.startActivity(intent);
                }
            });
        }
    }
}
