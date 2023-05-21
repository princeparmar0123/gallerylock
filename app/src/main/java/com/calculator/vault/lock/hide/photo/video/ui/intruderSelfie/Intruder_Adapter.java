package com.calculator.vault.lock.hide.photo.video.ui.intruderSelfie;



import static com.calculator.vault.lock.hide.photo.video.ui.intruderSelfie.Intruder_Activity.floating_btn_intruder;
import static com.calculator.vault.lock.hide.photo.video.ui.intruderSelfie.Intruder_Activity.selected_intruder_data;

import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data;
import com.calculator.vault.lock.hide.photo.video.ui.photos.ImageShowActivity;
import com.calculator.vault.lock.hide.photo.video.ui.photos.PhotosActivity;

import java.util.ArrayList;

public class Intruder_Adapter extends RecyclerView.Adapter<Intruder_Adapter.ViewHolder> {
    Activity activity;
    ArrayList<Media_Data> media_datas;
    LayoutInflater inflater;
    public static boolean IsLongClick = false;

    public Intruder_Adapter(Activity activity, ArrayList<Media_Data> media_datas) {
        this.activity = activity;
        this.media_datas = media_datas;
        inflater = LayoutInflater.from(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        int margin = activity.getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) activity).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int deviceheight = (displaymetrics.widthPixels - margin) / 3;
        holder.images_set.getLayoutParams().height = deviceheight;
        Glide.with(activity).load(media_datas.get(position).getPath()).into(holder.images_set);
        if (selected_intruder_data.contains(media_datas.get(position))) {
            holder.selected.setVisibility(View.VISIBLE);
        } else {
            holder.selected.setVisibility(View.GONE);
        }
        holder.images_set.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!IsLongClick) {
                    IsLongClick = true;
                    addRemoveSelectionList(holder, position);
                }
                return true;
            }
        });
        holder.images_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IsLongClick) {
                    addRemoveSelectionList(holder, position);
                } else {
                    PhotosActivity.hide_media_data = new ArrayList<>();
                    PhotosActivity.hide_media_data.addAll(media_datas);

                    Intent intent = new Intent(activity, ImageShowActivity.class);
                    intent.putExtra("pos", position);
//                    intent.putExtra("photodata", media_datas);
                    intent.putExtra("type",1);
                    activity.startActivity(intent);
                }
            }
        });
    }

    public void addRemoveSelectionList(ViewHolder holder, int position) {
        try {
            holder.selected.setVisibility(View.VISIBLE);
            floating_btn_intruder.setVisibility(View.VISIBLE);
            Intruder_Activity.selecter.setVisible(true);
            if (selected_intruder_data.contains(media_datas.get(position))) {
                selected_intruder_data.remove(media_datas.get(position));
                holder.selected.setVisibility(View.GONE);
            } else {
                selected_intruder_data.add(media_datas.get(position));
                holder.selected.setVisibility(View.VISIBLE);
            }
            if (selected_intruder_data.size() == 0) {
                IsLongClick = false;
                floating_btn_intruder.setVisibility(View.GONE);
                notifyItemChanged(position);
                Intruder_Activity.selecter.setVisible(false);
                Intruder_Activity.IsSelectAll = false;
                Intruder_Activity.selecter.setIcon(activity.getResources().getDrawable(R.drawable.iv_unselect));
            }
            if (selected_intruder_data.size() == media_datas.size()) {
                Intruder_Activity.IsSelectAll = true;
                Intruder_Activity.selecter.setIcon(activity.getResources().getDrawable(R.drawable.iv_select));
            } else {
                Intruder_Activity.IsSelectAll = false;
                Intruder_Activity.selecter.setIcon(activity.getResources().getDrawable(R.drawable.iv_unselect));
            }
        } catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        return media_datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView images_set;
        ImageView selected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            images_set = (ImageView) itemView.findViewById(R.id.imaegs);
            selected = (ImageView) itemView.findViewById(R.id.selected);
        }
    }
}
