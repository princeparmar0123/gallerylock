package com.calculator.vault.lock.hide.photo.video.ui.photos;



import static com.calculator.vault.lock.hide.photo.video.ui.photos.Folder_Photo_Activity.photo_hide;
import static com.calculator.vault.lock.hide.photo.video.ui.photos.Folder_Photo_Activity.select_photo_datas;

import android.app.Activity;
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

import java.util.ArrayList;

public class Photos_Adapters extends RecyclerView.Adapter<Photos_Adapters.ViewHolder> {
    Activity activity;
    ArrayList<Media_Data> media_datas;
    LayoutInflater inflater;
    boolean IsLongClick = false;

    public Photos_Adapters(Activity activity, ArrayList<Media_Data> media_datas) {
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
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int deviceheight = (displaymetrics.widthPixels - margin) / 3;
        holder.images_set.getLayoutParams().height = deviceheight;
        Glide.with(activity).load(media_datas.get(position).getPath()).override(1080, 600).into(holder.images_set);
        if (select_photo_datas.contains(media_datas.get(position))) {
            holder.selected.setVisibility(View.VISIBLE);
        } else {
            holder.selected.setVisibility(View.GONE);
        }
        holder.images_set.setOnClickListener(v -> addRemoveSelectionList(holder, position));
    }

    public void addRemoveSelectionList(ViewHolder holder, int position) {
        try {
            holder.selected.setVisibility(View.VISIBLE);
            Folder_Photo_Activity.selecter_fphoto.setVisible(true);
            if (select_photo_datas.contains(media_datas.get(position))) {
                select_photo_datas.remove(media_datas.get(position));
                holder.selected.setVisibility(View.GONE);
            } else {
                select_photo_datas.add(media_datas.get(position));
                holder.selected.setVisibility(View.VISIBLE);
            }
            if (select_photo_datas.size() == 0) {
                IsLongClick = false;
                notifyItemChanged(position);
                Folder_Photo_Activity.IsSelectAll = false;
                Folder_Photo_Activity.selecter_fphoto.setVisible(false);
                Folder_Photo_Activity.selecter_fphoto.setIcon(activity.getResources().getDrawable(R.drawable.iv_unselect));
            }
            if (select_photo_datas.size() == media_datas.size()) {
                Folder_Photo_Activity.IsSelectAll = true;
                Folder_Photo_Activity.selecter_fphoto.setIcon(activity.getResources().getDrawable(R.drawable.iv_select));
            } else {
                Folder_Photo_Activity.IsSelectAll = false;
                Folder_Photo_Activity.selecter_fphoto.setIcon(activity.getResources().getDrawable(R.drawable.iv_unselect));
            }
            photo_hide.setText("Hide (" + select_photo_datas.size() + ")");
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
            images_set = itemView.findViewById(R.id.imaegs);
            selected = itemView.findViewById(R.id.selected);
        }
    }
}
