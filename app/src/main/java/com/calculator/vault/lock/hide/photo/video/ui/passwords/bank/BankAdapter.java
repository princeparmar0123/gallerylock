package com.calculator.vault.lock.hide.photo.video.ui.passwords.bank;



import static com.calculator.vault.lock.hide.photo.video.ui.passwords.bank.BankActivity.no_bank;

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
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Bank_Data;

import java.util.ArrayList;

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.ViewHolder> {
    private final Database database;
    BankActivity activity;
    ArrayList<Bank_Data> bank_data;
    LayoutInflater inflater;

    public BankAdapter(BankActivity bank_activity, ArrayList<Bank_Data> bank_data) {
        this.activity = bank_activity;
        this.bank_data = bank_data;
        database = new Database(activity);
        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_bank, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bank_name.setText(bank_data.get(position).getBank_name());
        holder.account_number.setText(bank_data.get(position).getAccount_nummber());
        holder.bank_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(activity, holder.bank_option);
                popup.inflate(R.menu.menu_bank);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_edit:
                                Intent intent = new Intent(activity, AddBankActivity.class);
                                intent.putExtra("type", 1);
                                intent.putExtra("data", bank_data.get(position));
                                activity.startActivity(intent);
                                break;
                            case R.id.menu_delete:
                                AlertDialog deletedialog = new AlertDialog.Builder(activity)
                                        .setTitle("Delete")
                                        .setMessage("Are you sure,you want to delete this Bank Detail?")
                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                database.deleteBank(bank_data.get(position).getId());
                                                bank_data.remove(position);
                                                notifyDataSetChanged();
                                                if (bank_data.size() == 0) {
                                                    no_bank.setVisibility(View.VISIBLE);
                                                } else {
                                                    no_bank.setVisibility(View.GONE);
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
                            case R.id.menu_view:
                                Intent intent1 = new Intent(activity, BankdetailActivity.class);
                                intent1.putExtra("name", bank_data.get(position).getBank_name());
                                intent1.putExtra("link", bank_data.get(position).getUrl());
                                activity.startActivity(intent1);
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
        return bank_data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bank_profile;
        ImageView bank_option;
        TextView bank_name;
        TextView account_number;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bank_profile = (ImageView) itemView.findViewById(R.id.bank_profile);
            bank_option = (ImageView) itemView.findViewById(R.id.bank_option);
            bank_name = (TextView) itemView.findViewById(R.id.bank_name);
            account_number = (TextView) itemView.findViewById(R.id.account_number);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, AddBankActivity.class);
                    intent.putExtra("type", 2);
                    intent.putExtra("data", bank_data.get(getAdapterPosition()));
                    activity.startActivity(intent);
                }
            });
        }
    }
}
