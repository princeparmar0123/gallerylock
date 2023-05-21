package com.calculator.vault.lock.hide.photo.video.ui.video;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.calculator.vault.lock.hide.photo.video.R;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> resource;
    LayoutInflater inflater;

    public SpinnerAdapter(Context context, ArrayList<String> resource) {
        super(context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        this.context = context;
        this.resource = resource;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return resource.size();
    }

    @Override
    public String getItem(int position) {
        return resource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_spinner, parent, false);
        TextView spinner_item = (TextView) convertView.findViewById(R.id.spinner_item);
        spinner_item.setTextColor(context.getColor(R.color.black));
        spinner_item.setText(resource.get(position));
        return convertView;
    }
}
