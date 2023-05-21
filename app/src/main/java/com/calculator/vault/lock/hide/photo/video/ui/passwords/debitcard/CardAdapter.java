package com.calculator.vault.lock.hide.photo.video.ui.passwords.debitcard;



import static com.calculator.vault.lock.hide.photo.video.ui.passwords.debitcard.CardActivity.no_card;

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
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Card_Data;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private final Database database;
    CardActivity activity;
    ArrayList<Card_Data> card_data;
    LayoutInflater inflater;

    public CardAdapter(CardActivity card_activity, ArrayList<Card_Data> card_data) {
        activity = card_activity;
        this.card_data = card_data;
        database = new Database(activity);
        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.card_name.setText(card_data.get(position).getCard_bname() + " (" + card_data.get(position).getCard_type() + ")");
        holder.card_number.setText(card_data.get(position).getCard_number());
        holder.card_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(activity, holder.card_option);
                popup.inflate(R.menu.menu_bank);
                popup.getMenu().findItem(R.id.menu_view).setVisible(false);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_edit:
                                Intent intent = new Intent(activity, AddCardActivity.class);
                                intent.putExtra("type", 1);
                                intent.putExtra("data", card_data.get(position));
                                activity.startActivity(intent);
                                break;
                            case R.id.menu_delete:
                                AlertDialog deletedialog = new AlertDialog.Builder(activity)
                                        .setTitle("Delete")
                                        .setMessage("Are you sure,you want to delete this card detail?")
                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                database.deleteCard(card_data.get(position).getId());
                                                card_data.remove(position);
                                                notifyDataSetChanged();
                                                if (card_data.size() == 0) {
                                                    no_card.setVisibility(View.VISIBLE);
                                                } else {
                                                    no_card.setVisibility(View.GONE);
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
    }

    @Override
    public int getItemCount() {
        return card_data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView card_name;
        TextView card_number;
        ImageView card_option;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card_name = (TextView) itemView.findViewById(R.id.card_name);
            card_number = (TextView) itemView.findViewById(R.id.card_number);
            card_option = (ImageView) itemView.findViewById(R.id.card_option);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, AddCardActivity.class);
                    intent.putExtra("type", 2);
                    intent.putExtra("data", card_data.get(getAdapterPosition()));
                    activity.startActivity(intent);
                }
            });
        }
    }
}
