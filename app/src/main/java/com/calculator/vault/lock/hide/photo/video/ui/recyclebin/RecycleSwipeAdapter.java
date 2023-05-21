package com.calculator.vault.lock.hide.photo.video.ui.recyclebin;



import static com.calculator.vault.lock.hide.photo.video.ui.recyclebin.RecyclebinShowActivity.recycle_viewpager;
import static com.calculator.vault.lock.hide.photo.video.ui.recyclebin.RecyclebinShowActivity.recycler_toolbar;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data;
import com.calculator.vault.lock.hide.photo.video.custom.TouchImageView;

import java.io.File;
import java.util.ArrayList;


public class RecycleSwipeAdapter extends PagerAdapter {
    Activity activity;
    ArrayList<Media_Data> image_list;
    LayoutInflater inflater;
    private VideoView videoview;
    private LinearLayout controller;
    private TextView progress_time;
    private TextView end_time;
    private SeekBar seekbar;
    private ImageView play;
    private ImageView previous;
    private ImageView next;
    private Runnable onEverySecond;

    public RecycleSwipeAdapter(Activity image_show_activity, ArrayList<Media_Data> image_list) {
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
        //if(new ImageFileFilter().accept(new File(image_list.get(position).getPath())))
        if (new ImageFileFilter().accept(new File(image_list.get(position).getPath()))) {
            View view1 = inflater.inflate(R.layout.item_slide_image, container, false);
            TouchImageView imageView = view1.findViewById(R.id.imageView);
            Glide.with(activity).load(image_list.get(position).getPath()).into(imageView);
            container.addView(view1);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recycler_toolbar.getVisibility() == View.VISIBLE) {
                        recycler_toolbar.setVisibility(View.GONE);
                    } else {
                        recycler_toolbar.setVisibility(View.VISIBLE);
                    }
                }
            });
            return view1;
        } else {
            View view1 = inflater.inflate(R.layout.item_videoswipe, container, false);
            videoview = (VideoView) view1.findViewById(R.id.videoview);
            seekbar = (SeekBar) view1.findViewById(R.id.seekbar);
            play = (ImageView) view1.findViewById(R.id.play);
            previous = (ImageView) view1.findViewById(R.id.previous);
            next = (ImageView) view1.findViewById(R.id.next);
            end_time = (TextView) view1.findViewById(R.id.end_time);
            progress_time = (TextView) view1.findViewById(R.id.progress_time);
            controller = (LinearLayout) view1.findViewById(R.id.controller);
            videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    seekbar.setMax(videoview.getDuration());
                    int duration = mp.getDuration() / 1000;
                    int hours = duration / 3600;
                    int minutes = (duration / 60) - (hours * 60);
                    int seconds = duration - (hours * 3600) - (minutes * 60);
                    String formatted = String.format("%02d:%02d", minutes, seconds);
                    end_time.setText(formatted);
                    seekbar.postDelayed(onEverySecond, 1000);
                }
            });

            videoview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (controller.getVisibility() == View.VISIBLE) {
                        controller.setVisibility(View.GONE);
                        recycler_toolbar.setVisibility(View.GONE);
                    } else {
                        controller.setVisibility(View.VISIBLE);
                        recycler_toolbar.setVisibility(View.VISIBLE);
                    }
                }
            });

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (RecyclebinShowActivity.pos != image_list.size() - 1) {
                        RecyclebinShowActivity.pos++;
                        recycle_viewpager.setCurrentItem(RecyclebinShowActivity.pos, true);
                    }
                }
            });

            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (RecyclebinShowActivity.pos != 0) {
                        RecyclebinShowActivity.pos--;
                        recycle_viewpager.setCurrentItem(RecyclebinShowActivity.pos, true);
                    }
                }
            });

            onEverySecond = new Runnable() {
                @Override
                public void run() {
                    if (seekbar != null) {
                        seekbar.setProgress(videoview.getCurrentPosition());
                        int duration = videoview.getCurrentPosition() / 1000;
                        int hours = duration / 3600;
                        int minutes = (duration / 60) - (hours * 60);
                        int seconds = duration - (hours * 3600) - (minutes * 60);
                        String formatted = String.format("%02d:%02d", minutes, seconds);
                        progress_time.setText(formatted);
                    }
                    if (videoview.isPlaying()) {
                        seekbar.postDelayed(onEverySecond, 1000);
                    }

                }
            };
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    videoview.start();
                    if (videoview.isPlaying()) {
                        seekbar.postDelayed(onEverySecond, 1000);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    videoview.pause();
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int duration = videoview.getCurrentPosition() / 1000;
                    int hours = duration / 3600;
                    int minutes = (duration / 60) - (hours * 60);
                    int seconds = duration - (hours * 3600) - (minutes * 60);
                    String formatted = String.format("%02d:%02d", minutes, seconds);
                    progress_time.setText(formatted);
                    if (fromUser) {
                        videoview.seekTo(progress);
                    }
                }
            });
            videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    play.setImageResource(R.drawable.iv_play_button);
                }
            });
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoview.isPlaying()) {
                        play.setImageResource(R.drawable.iv_play_button);
                        videoview.pause();
                    } else {
                        play.setImageResource(R.drawable.iv_pause_button);
                        videoview.start();
                        seekbar.postDelayed(onEverySecond, 1000);
                    }
                }
            });
            seekbar.setProgress(0);
            play.setImageResource(R.drawable.iv_pause_button);
            Uri uri = Uri.parse(image_list.get(position).getPath());
            videoview.setVideoURI(uri);
            videoview.requestFocus();
            videoview.start();
            container.addView(view1);
            return view1;
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