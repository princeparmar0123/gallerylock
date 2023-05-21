package com.calculator.vault.lock.hide.photo.video.ui.recyclebin;



import static android.os.Build.VERSION.SDK_INT;

import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.documentfile.provider.DocumentFile;


import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data;
import com.calculator.vault.lock.hide.photo.video.common.utils.Constant;
import com.calculator.vault.lock.hide.photo.video.common.utils.FileUtils;
import com.calculator.vault.lock.hide.photo.video.custom.CustomViewPager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class RecyclebinShowActivity extends AppCompatActivity {

    public static CustomViewPager recycle_viewpager;
    public static Toolbar recycler_toolbar;
    public static int pos = -1;
    private ArrayList<Media_Data> videoData;
    private Database database;
    private RecycleSwipeAdapter recycle_swipe_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_recyclebin_show);
        pos = getIntent().getIntExtra("pos", 0);
        videoData = (ArrayList<Media_Data>) getIntent().getSerializableExtra("videodata");
        initView();
        initAdapter();
        setTitle(videoData.get(pos).getName());
    }


    private void initAdapter() {
        if (videoData.size() > 0) {
            recycle_swipe_adapter = new RecycleSwipeAdapter(RecyclebinShowActivity.this, videoData);
            recycle_viewpager.setAdapter(recycle_swipe_adapter);
            recycle_viewpager.setOffscreenPageLimit(0);
            recycle_viewpager.setCurrentItem(pos);

            recycle_viewpager.setOnPageChangeListener(new CustomViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    recycler_toolbar.setVisibility(View.VISIBLE);
                    setTitle(videoData.get(position).getName());
                    pos = position;
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    private void initView() {
        recycle_viewpager = findViewById(R.id.recycle_viewpager);
        recycler_toolbar = findViewById(R.id.toolbar);
        database = new Database(RecyclebinShowActivity.this);
        setSupportActionBar(recycler_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recycler_toolbar.getNavigationIcon().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.menu_unhide:
                if (videoData != null && pos != -1 && pos < videoData.size()) {
                    String sourcePath = videoData.get(pos).getPath();
                    File source = new File(sourcePath);
                    if (videoData.get(pos).getName().contains("Intruder_")) {
                        String path = FileUtils.getRealPath(RecyclebinShowActivity.this, Uri.parse(sourcePath));
                        if (path != null)
                            source = new File(path);
                        String destinationPath = Constant.intruder_path + "/" + videoData.get(pos).getName();
                        File destination = new File(destinationPath);
                        try {
                            restoreFile(source, destination);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

//                        String destinationPath = Constant.intruder_path + "/" + videoData.get(pos).getName();
//                        File destination = new File(destinationPath);
//                        try {
//                            deleteFile(source, destination);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    } else {
                        if (isImage(source)) {
                            String destinationPath = Constant.photos_path + "/" + videoData.get(pos).getName();
                            File destination = new File(destinationPath);
                            try {
                                deleteFile(source, destination);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            String destinationPath = Constant.videos_path + "/" + videoData.get(pos).getName();
                            File destination = new File(destinationPath);
                            try {
                                deleteFile(source, destination);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    database.deleteData(videoData.get(pos).getName());
                    videoData.remove(videoData.get(pos));
                    if (videoData.size() > 0) {
                       // recyclebin_recycler.setVisibility(View.VISIBLE);
                      //  no_recycler.setVisibility(View.GONE);
                        initAdapter();
                    } else {
                       // recyclebin_recycler.setVisibility(View.GONE);
                        //no_recycler.setVisibility(View.VISIBLE);
                        finish();
                    }
                }
                break;
            case R.id.menu_delete:
                AlertDialog deletedialog = new AlertDialog.Builder(RecyclebinShowActivity.this)
                        .setTitle("Delete")
                        .setMessage("Are you sure,you want to delete this file?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (videoData != null && videoData.size() != 0 && pos < videoData.size()) {
//                                    String sourcePath1 = videoData.get(pos).getPath();
//                                    File source1 = new File(sourcePath1);
//                                    source1.delete();

                                    if (SDK_INT > Build.VERSION_CODES.Q) {
                                        DocumentFile file = DocumentFile.fromSingleUri(RecyclebinShowActivity.this, Uri.parse(videoData.get(pos).getPath()));
                                        file.delete();
                                    } else {
                                        String sourcePath = videoData.get(pos).getPath();
                                        File source = new File(sourcePath);
                                        source.delete();
                                    }

                                    database.deleteHide(videoData.get(pos).getName());
                                    database.deleteData(videoData.get(pos).getName());
                                    videoData.remove(videoData.get(pos));
                                    if (videoData.size() > 0) {
                                        //recyclebin_recycler.setVisibility(View.VISIBLE);
                                       // no_recycler.setVisibility(View.GONE);
                                        initAdapter();
                                    } else {
                                       // recyclebin_recycler.setVisibility(View.GONE);
                                      //  no_recycler.setVisibility(View.VISIBLE);
                                        finish();
                                    }
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                        .create();
                deletedialog.show();

                break;
        }
        return false;
    }

    private void restoreFile(File source, File destination) throws IOException {
        if (!destination.getParentFile().exists())
            destination.getParentFile().mkdirs();

        FileChannel source_channel = null;
        FileChannel destination_channel = null;
        try {
            Boolean isRename = source.renameTo(destination);
            if (isRename) {
                if (!source.getPath().contains("'")) {
                    getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "='" + source.getPath() + "'", null);
                }
            } else {
                if (!destination.exists()) {
                    destination.createNewFile();
                }
                source_channel = new FileInputStream(source).getChannel();
                destination_channel = new FileOutputStream(destination).getChannel();
                destination_channel.transferFrom(source_channel, 0, source_channel.size());
                source_channel.close();
                source.delete();
            }
            MediaScannerConnection.scanFile(this, new String[]{destination.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                }
            });
        } finally {
            if (source_channel != null) {
                source_channel.close();
            }
            if (destination_channel != null) {
                destination_channel.close();
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

    private void deleteFile(File source, File destination) throws IOException {
        if (!destination.getParentFile().exists())
            destination.getParentFile().mkdirs();

        FileChannel source_channel = null;
        FileChannel destination_channel = null;
        try {
            Boolean isRename = source.renameTo(destination);
            if (isRename) {
                if (!source.getPath().contains("'")) {
                    getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "='" + source.getPath() + "'", null);
                }
            } else {
                if (!destination.exists()) {
                    destination.createNewFile();
                }
                source_channel = new FileInputStream(source).getChannel();
                destination_channel = new FileOutputStream(destination).getChannel();
                destination_channel.transferFrom(source_channel, 0, source_channel.size());
                source_channel.close();
                source.delete();
            }
            MediaScannerConnection.scanFile(this, new String[]{destination.getPath()}, null, (path, uri) -> Log.i("ExternalStorage", "Scanned " + path + ":" + uri));
        } finally {
            if (source_channel != null) {
                source_channel.close();
            }
            if (destination_channel != null) {
                destination_channel.close();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hide, menu);
        menu.findItem(R.id.menu_unhide).setTitle("Restore");
        return true;
    }
}
