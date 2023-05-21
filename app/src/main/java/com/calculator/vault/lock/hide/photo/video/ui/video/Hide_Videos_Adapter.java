package com.calculator.vault.lock.hide.photo.video.ui.video;



import static com.calculator.vault.lock.hide.photo.video.ui.video.Videos_Activity.floating_btn_video;
import static com.calculator.vault.lock.hide.photo.video.ui.video.Videos_Activity.selected_hide_video_data;
import static com.calculator.vault.lock.hide.photo.video.ui.video.Videos_Activity.video_btn_lay;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Hide_Videos_Adapter extends RecyclerView.Adapter<Hide_Videos_Adapter.ViewHolder> {
    Activity activity;
    ArrayList<Media_Data> media_datas;
    LayoutInflater inflater;
    public static boolean IsLongClick = false;
    private String formatted;

    public Hide_Videos_Adapter(Activity activity, ArrayList<Media_Data> media_datas) {
        this.activity = activity;
        this.media_datas = media_datas;
        inflater = LayoutInflater.from(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        int margin = activity.getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int deviceheight = (displaymetrics.widthPixels - margin) / 3;
        holder.images_set.getLayoutParams().height = deviceheight;
        Glide.with(activity).load(media_datas.get(position).getPath()).into(holder.images_set);
        if (selected_hide_video_data.contains(media_datas.get(position))) {
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
                    Intent intent = new Intent(activity, VideoShowActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("videodata", media_datas);
                    bundle.putInt("pos", position);
                    bundle.putInt("type", 0);
                    intent.putExtra("BUNDLE", bundle);

//                    intent.putExtra("pos", position);
//                    intent.putExtra("videodata", media_datas);
//                    intent.putExtra("type", 0);
                    activity.startActivity(intent);
                }
            }
        });
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(activity, Uri.fromFile(new File(media_datas.get(position).getPath())));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            int timeInMillisec = Integer.parseInt(time);
            int duration = timeInMillisec / 1000;
            int hours = duration / 3600;
            int minutes = (duration / 60) - (hours * 60);
            int seconds = duration - (hours * 3600) - (minutes * 60);
            if (hours == 0) {
                formatted = String.format("%02d:%02d", minutes, seconds);
            } else {
                formatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            }

            try {
                retriever.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
            holder.duration.setText(formatted);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
    }

    public void addRemoveSelectionList(ViewHolder holder, int position) {
        try {
            holder.selected.setVisibility(View.VISIBLE);
            floating_btn_video.setVisibility(View.GONE);
            video_btn_lay.setVisibility(View.VISIBLE);
            Videos_Activity.selecter.setVisible(true);
            if (selected_hide_video_data.contains(media_datas.get(position))) {
                selected_hide_video_data.remove(media_datas.get(position));
                holder.selected.setVisibility(View.GONE);
            } else {
                selected_hide_video_data.add(media_datas.get(position));
                holder.selected.setVisibility(View.VISIBLE);
            }
            if (selected_hide_video_data.size() == 0) {
                IsLongClick = false;
                floating_btn_video.setVisibility(View.VISIBLE);
                video_btn_lay.setVisibility(View.GONE);
                notifyItemChanged(position);
                Videos_Activity.IsSelectAll = false;
                Videos_Activity.selecter.setVisible(false);
                Videos_Activity.selecter.setIcon(activity.getResources().getDrawable(R.drawable.iv_unselect));
            }
            if (selected_hide_video_data.size() == media_datas.size()) {
                Videos_Activity.IsSelectAll = true;
                Videos_Activity.selecter.setIcon(activity.getResources().getDrawable(R.drawable.iv_select));
            } else {
                Videos_Activity.IsSelectAll = false;
                Videos_Activity.selecter.setIcon(activity.getResources().getDrawable(R.drawable.iv_unselect));
            }
            if (selected_hide_video_data.size() == 0) {
                activity.setTitle("Videos");
            } else {
                activity.setTitle("Videos(" + selected_hide_video_data.size() + ")");
            }
        } catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        return media_datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView duration;
        ImageView images_set;
        ImageView selected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            images_set = itemView.findViewById(R.id.imaegs);
            selected = itemView.findViewById(R.id.selected);
            duration = itemView.findViewById(R.id.duration);
        }
    }
}
