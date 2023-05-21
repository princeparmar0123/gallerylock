package com.calculator.vault.lock.hide.photo.video.ui.recyclebin;



import static com.calculator.vault.lock.hide.photo.video.ui.recyclebin.Recyclebin_Activity.btn_lay;
import static com.calculator.vault.lock.hide.photo.video.ui.recyclebin.Recyclebin_Activity.selected_recycle_data;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data;


import java.io.File;
import java.util.ArrayList;

public class Recyclebin_Adapter extends RecyclerView.Adapter<Recyclebin_Adapter.ViewHolder> {
    Activity activity;
    ArrayList<Media_Data> media_datas;
    LayoutInflater inflater;
    public static boolean IsLongClick = false;
    private String formatted;


    public Recyclebin_Adapter(Activity activity, ArrayList<Media_Data> media_datas) {
        this.activity = activity;
        this.media_datas = media_datas;
        inflater = LayoutInflater.from(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder,  int position) {
        int margin = activity.getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) activity).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int deviceheight = (displaymetrics.widthPixels - margin) / 3;
        holder.images_set.getLayoutParams().height = deviceheight;
        Glide.with(activity).load(media_datas.get(position).getPath()).into(holder.images_set);
        if (selected_recycle_data.contains(media_datas.get(position))) {
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
                    Intent intent = new Intent(activity, RecyclebinShowActivity.class);
                    intent.putExtra("pos", position);
                    intent.putExtra("videodata", media_datas);
                    activity.startActivity(intent);
                }
            }
        });
        boolean isImage = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            DocumentFile file = DocumentFile.fromSingleUri(activity, Uri.parse(media_datas.get(position).getPath()));
            try {
                isImage = isImage(file.getUri());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            isImage = isImage(new File(media_datas.get(position).getPath()));
        }

        if (isImage) {
            holder.duration.setVisibility(View.GONE);
        } else {
            holder.duration.setVisibility(View.VISIBLE);
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                File file = new File(media_datas.get(position).getPath());
//                FileInputStream input = new FileInputStream(media_datas.get(position).getPath());
                if (file.exists()) {
                    retriever.setDataSource(activity, Uri.fromFile(file));
//                    retriever.setDataSource(input.getFD());
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isImage(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        return options.outWidth != -1 && options.outHeight != -1;
    }

    public static boolean isImage(Uri file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        return options.outWidth != -1 && options.outHeight != -1;
    }


    public void addRemoveSelectionList(ViewHolder holder, int position) {
        try {
            holder.selected.setVisibility(View.VISIBLE);
            Recyclebin_Activity.selecter.setVisible(true);
            btn_lay.setVisibility(View.VISIBLE);
            if (selected_recycle_data.contains(media_datas.get(position))) {
                selected_recycle_data.remove(media_datas.get(position));
                holder.selected.setVisibility(View.GONE);
            } else {
                selected_recycle_data.add(media_datas.get(position));
                holder.selected.setVisibility(View.VISIBLE);
            }
            if (selected_recycle_data.size() == 0) {
                IsLongClick = false;
                notifyItemChanged(position);
                Recyclebin_Activity.selecter.setVisible(false);
                Recyclebin_Activity.IsSelectAll = false;
                Recyclebin_Activity.selecter.setIcon(activity.getResources().getDrawable(R.drawable.iv_unselect));
                btn_lay.setVisibility(View.GONE);
            }
            if (selected_recycle_data.size() == media_datas.size()) {
                Recyclebin_Activity.IsSelectAll = true;
                Recyclebin_Activity.selecter.setIcon(activity.getResources().getDrawable(R.drawable.iv_select));
            } else {
                Recyclebin_Activity.IsSelectAll = false;
                Recyclebin_Activity.selecter.setIcon(activity.getResources().getDrawable(R.drawable.iv_unselect));
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
        TextView duration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            images_set = (ImageView) itemView.findViewById(R.id.imaegs);
            selected = (ImageView) itemView.findViewById(R.id.selected);
            duration = (TextView) itemView.findViewById(R.id.duration);
        }
    }
}
