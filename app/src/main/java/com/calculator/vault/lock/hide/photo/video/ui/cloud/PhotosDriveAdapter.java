package com.calculator.vault.lock.hide.photo.video.ui.cloud;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data;
import com.calculator.vault.lock.hide.photo.video.ui.photos.ImageShowActivity;
import com.calculator.vault.lock.hide.photo.video.ui.photos.PhotosActivity;
import com.calculator.vault.lock.hide.photo.video.ui.video.VideoShowActivity;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PhotosDriveAdapter extends RecyclerView.Adapter<PhotosDriveAdapter.ViewHolder> {
    int i;
    FragmentActivity activity;
    ArrayList<Media_Data> googleDriveFiles;
    LayoutInflater inflater;
    private String formatted;

    public PhotosDriveAdapter(FragmentActivity activity, ArrayList<Media_Data> googleDriveFiles, int i) {
        this.activity = activity;
        this.i = i;
        this.googleDriveFiles = googleDriveFiles;
        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (i == 0) {
            holder.duration.setVisibility(View.GONE);
        } else if (i == 1) {
            holder.duration.setVisibility(View.VISIBLE);
        }
        int margin = activity.getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) activity).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int deviceheight = (displaymetrics.widthPixels - margin) / 3;
        holder.imagetext.getLayoutParams().height = deviceheight;
        Glide.with(activity).load(googleDriveFiles.get(position).getPath()).into(holder.imagetext);
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            FileInputStream input = new FileInputStream(googleDriveFiles.get(position).getPath());
            Log.e("Drive", "googleDriveFiles path " + googleDriveFiles.get(position).getPath());
            File file = new File(googleDriveFiles.get(position).getPath());
            if (i == 1)
                if (file.exists()) {
//                retriever.setDataSource(googleDriveFiles.get(position).getPath());
                    retriever.setDataSource(input.getFD());
                    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    if (!TextUtils.isEmpty(time) && TextUtils.isDigitsOnly(time)) {
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
                    } else {
                        formatted = String.format("%02d:%02d", 00, 00);
                    }
                    retriever.release();
                    holder.duration.setText(formatted);
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return googleDriveFiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView duration;
        ImageView imagetext;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagetext = (ImageView) itemView.findViewById(R.id.imaegs);
            duration = (TextView) itemView.findViewById(R.id.duration);

            itemView.setOnClickListener(v -> {
                if (i == 0) {
                    //App.Companion.getInstance()
                    PhotosActivity.hide_media_data = new ArrayList<>();
                    PhotosActivity.hide_media_data.addAll(googleDriveFiles);

                    Intent intent = new Intent(activity, ImageShowActivity.class);
                    intent.putExtra("pos", getAdapterPosition());
//                        intent.putExtra("photodata", googleDriveFiles);
                    intent.putExtra("type", 2);
                    activity.startActivity(intent);
                } else if (i == 1) {


//                    val intent = Intent(context, Video_Show_Activity::class.java)
//                    val bundle = Bundle()
//                    bundle.putSerializable("videodata", displayList as Serializable?)
//                    bundle.putInt("pos", position)
//                    bundle.putInt("type", 0)
//                    intent.putExtra("BUNDLE", bundle)
//                    context.startActivity(intent)

                    Intent intent = new Intent(activity, VideoShowActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("videodata",googleDriveFiles);
                    bundle.putInt("pos",getAdapterPosition());
                    bundle.putInt("type",1);
                    intent.putExtra("BUNDLE", bundle);
                    activity.startActivity(intent);


//                    Intent intent = new Intent(activity, Video_Show_Activity.class);
//                    intent.putExtra("pos", getAdapterPosition());
//                    intent.putExtra("videodata", googleDriveFiles);
//                    intent.putExtra("type", 1);
//                    activity.startActivity(intent);
                }

            });
        }
    }
}
