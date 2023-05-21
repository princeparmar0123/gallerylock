package com.calculator.vault.lock.hide.photo.video.ui.passwords.social;



import static com.calculator.vault.lock.hide.photo.video.ui.passwords.social.SocialActivity.no_social;

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
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Social_Data;

import java.util.ArrayList;

public class SocialAdapter extends RecyclerView.Adapter<SocialAdapter.ViewHolder> {
    private final Database database;
    SocialActivity activity;
    ArrayList<Social_Data> social_data;
    LayoutInflater inflater;

    public SocialAdapter(SocialActivity email_activity, ArrayList<Social_Data> social_data) {
        activity = email_activity;
        this.social_data = social_data;
        database = new Database(activity);
        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_social, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.social_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(activity, holder.social_option);
                popup.inflate(R.menu.menu_bank);
                popup.getMenu().findItem(R.id.menu_view).setVisible(false);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_edit:
                                Intent intent = new Intent(activity, AddSocialActivity.class);
                                intent.putExtra("type", 1);
                                intent.putExtra("data", social_data.get(position));
                                activity.startActivity(intent);
                                break;
                            case R.id.menu_delete:
                                AlertDialog deletedialog = new AlertDialog.Builder(activity)
                                        .setTitle("Delete")
                                        .setMessage("Are you sure,you want to delete this social detail?")
                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                database.deleteSocial(social_data.get(position).getId());
                                                social_data.remove(position);
                                                notifyDataSetChanged();
                                                if (social_data.size()==0){
                                                    no_social.setVisibility(View.VISIBLE);
                                                }else {
                                                    no_social.setVisibility(View.GONE);
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
        holder.social_show_type.setText(social_data.get(position).getSocial_type());
        holder.social_show_name.setText(social_data.get(position).getSocial_name());
//        holder.social_show_email.setText(social_data.get(position).getSocial_email());
//        holder.social_show_pass.setText(social_data.get(position).getSocial_pass());
    }

    @Override
    public int getItemCount() {
        return social_data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView social_show_type;
        TextView social_show_name;
//        TextView social_show_email;
//        TextView social_show_pass;
        ImageView social_option;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            social_show_type = (TextView) itemView.findViewById(R.id.social_show_type);
            social_show_name = (TextView) itemView.findViewById(R.id.social_show_name);
//            social_show_email = (TextView) itemView.findViewById(R.id.social_show_email);
//            social_show_pass = (TextView) itemView.findViewById(R.id.social_show_pass);
            social_option = (ImageView) itemView.findViewById(R.id.social_option);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, AddSocialActivity.class);
                    intent.putExtra("type", 2);
                    intent.putExtra("data", social_data.get(getAdapterPosition()));
                    activity.startActivity(intent);
                }
            });
        }

    }
}
