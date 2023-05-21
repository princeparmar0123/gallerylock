package com.calculator.vault.lock.hide.photo.video.ui.photos;

import android.app.Activity;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data;
import com.calculator.vault.lock.hide.photo.video.custom.TouchImageView;


import java.util.ArrayList;

public class SwipeAdapter extends PagerAdapter {
    Activity activity;
    ArrayList<Media_Data> image_list;
    LayoutInflater inflater;
    public static TouchImageView imageView;
    public static VideoView videoView;

    public SwipeAdapter(Activity image_show_activity, ArrayList<Media_Data> image_list) {
        activity = image_show_activity;
        this.image_list = image_list;
        inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return image_list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view1 = inflater.inflate(R.layout.item_slide_image, container, false);
        TouchImageView imageView = (TouchImageView) view1.findViewById(R.id.imageView);
        Glide.with(activity).load(image_list.get(position).getPath()).into(imageView);
        activity.setTitle(image_list.get(position).getName());
        container.addView(view1);
//        imageView.setOnClickListener(v -> {
//            if (toolbar_image.getVisibility() == View.VISIBLE) {
//                toolbar_image.setVisibility(View.GONE);
//            } else {
//                toolbar_image.setVisibility(View.VISIBLE);
//                new Handler().postDelayed(() -> toolbar_image.setVisibility(View.GONE), 3000);
//            }
//        });
        return view1;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}