package com.calculator.vault.lock.hide.photo.video.ui.video;


import static android.os.Build.VERSION.SDK_INT;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
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

import com.calculator.vault.lock.hide.photo.video.App;
import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Delet_Data;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Hide_Data;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data;
import com.calculator.vault.lock.hide.photo.video.common.utils.Constant;
import com.calculator.vault.lock.hide.photo.video.common.utils.CreateFile;
import com.calculator.vault.lock.hide.photo.video.common.utils.FileUtils;
import com.calculator.vault.lock.hide.photo.video.common.utils.Utils;
import com.calculator.vault.lock.hide.photo.video.custom.CustomViewPager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class VideoShowActivity extends AppCompatActivity {

    public static Toolbar toolbar;
    private ArrayList<Media_Data> videoData;
    public static int pos;
    private int type;
    ArrayList<Hide_Data> hide_data = new ArrayList<>();
    Boolean temp = false;
    private Database database;
    public static CustomViewPager video_viewpager;
    private VideoSwipeAdapter video_swipe_adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_show);
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");

        pos = args.getInt("pos", 0);
        type = args.getInt("type", 0);
        videoData = (ArrayList<Media_Data>) args.getSerializable("videodata");

//        pos = getIntent().getIntExtra("pos", 0);
//        videoData = (ArrayList<Media_Data>) getIntent().getSerializableExtra("videodata");
//        type = getIntent().getIntExtra("type", 0);

//        pos = getIntent().getIntExtra("pos", 0);
//        videoData = (ArrayList<Media_Data>) getIntent().getSerializableExtra("videodata");
//        type = getIntent().getIntExtra("type", 0);


        initView();
        initAdapter();
    }


    private void initAdapter() {
        video_swipe_adapter = new VideoSwipeAdapter(VideoShowActivity.this, videoData);
        video_viewpager.setAdapter(video_swipe_adapter);
        video_viewpager.setOffscreenPageLimit(0);
        video_viewpager.setCurrentItem(pos);

        video_viewpager.setOnPageChangeListener(new CustomViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                toolbar.setVisibility(View.VISIBLE);
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

    private void initView() {
        video_viewpager = findViewById(R.id.video_viewpager);
        database = new Database(VideoShowActivity.this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);

//        initVideo(pos);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.menu_unhide:
                hide_data.clear();
                hide_data.addAll(database.getAllHide());
                String sourcePath = videoData.get(video_viewpager.getCurrentItem()).getPath();
                File source = new File(sourcePath);
                temp = false;
                for (int j = 0; j < hide_data.size(); j++) {
                    if (SDK_INT > Build.VERSION_CODES.Q) {
                        Uri treeUri = Uri.parse(App.Companion.getInstance().getPref().getHiddenUri());
                        if (treeUri != null) {
                            this.getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            DocumentFile tree = DocumentFile.fromTreeUri(this, treeUri);
                            for (DocumentFile documentFile : tree.listFiles()) {
                                if (documentFile.isDirectory() && documentFile.getName().equals(Constant.video_Folder)) {
                                    for (DocumentFile file : documentFile.listFiles()) {
                                        if (file.getName().equals(videoData.get(video_viewpager.getCurrentItem()).getName())) {
                                            unhideFileOnAboveQ(this, file.getUri(), videoData.get(video_viewpager.getCurrentItem()).getName());
                                            file.delete();
                                            database.deleteHide(hide_data.get(j).getName());
                                            videoData.remove(videoData.get(video_viewpager.getCurrentItem()));
                                            temp = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (source.getName().equals(hide_data.get(j).getName())) {
                            String destinationPath = hide_data.get(j).getPath();
                            File destination = new File(destinationPath);
                            try {
                                deleteFile(source, destination);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            temp = true;
                            database.deleteHide(hide_data.get(j).getName());
                            videoData.remove(videoData.get(video_viewpager.getCurrentItem()));
                        }
                    }

                    if (temp)
                        break;
                }
                if (!temp) {
                    String destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/" + source.getName();
                    File destination = new File(destinationPath);
                    try {
                        deleteFile(source, destination);
                        videoData.remove(videoData.get(video_viewpager.getCurrentItem()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (videoData.size() == 0) {
                    finish();
                } else {
                    initAdapter();
                }
                break;
            case R.id.menu_delete:
                AlertDialog deletedialog = new AlertDialog.Builder(VideoShowActivity.this)
                        .setTitle("Delete")
                        .setMessage("Are you sure,you want to delete this video?")
                        .setPositiveButton("Delete", (dialog, whichButton) -> {
                            String sourcePath1 = videoData.get(video_viewpager.getCurrentItem()).getPath();
                            File source1 = new File(sourcePath1);
                            String destinationPath = Constant.recycle_path + "/" + videoData.get(video_viewpager.getCurrentItem()).getName();
                            File destination = new File(destinationPath);
                            if (SDK_INT > Build.VERSION_CODES.Q) {
                                destinationPath = Constant.recycle_path;
                                destination = new File(destinationPath);

                                String path = FileUtils.getRealPath(VideoShowActivity.this,Uri.parse(sourcePath1));
                                if (path != null)
                                    source1 = new File(path);

                                try {
                                    moveToRecycleAboveQ(source1,
                                            new File(destination + File.separator + videoData.get(video_viewpager.getCurrentItem()).getName()),
                                            Uri.parse(videoData.get(video_viewpager.getCurrentItem()).getPath()),
                                            videoData.get(video_viewpager.getCurrentItem()).getName());

//                                    DocumentFile file = DocumentFile.fromSingleUri(VideoShowActivity.this, Uri.parse(videoData.get(video_viewpager.getCurrentItem()).getPath()));
//                                    file.delete();

                                    Delet_Data delet_data = new Delet_Data();
                                    Calendar calendar = Calendar.getInstance();
                                    delet_data.setName(videoData.get(video_viewpager.getCurrentItem()).getName());
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    delet_data.setDate(dateFormat.format(calendar.getTime()));
                                    database.addDelete(delet_data);
                                    videoData.remove(videoData.get(video_viewpager.getCurrentItem()));
                                    if (videoData.size() == 0) {
                                        finish();
                                    } else {
                                        initAdapter();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                try {
                                    deleteFile(source1, destination);
                                    Delet_Data delet_data = new Delet_Data();
                                    Calendar calendar = Calendar.getInstance();
                                    delet_data.setName(videoData.get(video_viewpager.getCurrentItem()).getName());
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    delet_data.setDate(dateFormat.format(calendar.getTime()));
                                    database.addDelete(delet_data);
                                    videoData.remove(videoData.get(video_viewpager.getCurrentItem()));
                                    if (videoData.size() == 0) {
                                        finish();
                                    } else {
                                        initAdapter();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            dialog.dismiss();
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                deletedialog.show();

                break;
        }
        return false;
    }

    public boolean unhideFileOnAboveQ(Context context, Uri uri, String name) {

        ContentResolver contentResolver = context.getContentResolver();
        FileOutputStream fos = null;
        String folderName = "";

        String relativePath = Environment.DIRECTORY_PICTURES + File.separator +
                "Calculator Vault Unhide";

        String mimeType = getFileType(name);

        try {
            if (Utils.isImage(name.split("\\.")[1])) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name.split("\\.")[0]);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath);

                Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                FileInputStream inputStream = (FileInputStream) contentResolver.openInputStream(uri);
                fos = (FileOutputStream) contentResolver.openOutputStream(imageUri);
                FileChannel inChannel = inputStream.getChannel();
                FileChannel outChannel = fos.getChannel();
                outChannel.transferFrom(inChannel, 0, inChannel.size());
                inChannel.close();
                outChannel.close();
                return true;
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name.split("\\.")[0]);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/" + mimeType);
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath);

                Uri imageUri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);

                fos = (FileOutputStream) contentResolver.openOutputStream(imageUri);
                FileInputStream inStream = (FileInputStream) contentResolver.openInputStream(uri);
                FileChannel inChannel = inStream.getChannel();
                FileChannel outChannel = fos.getChannel();
                inChannel.transferTo(0, inChannel.size(), outChannel);
                inStream.close();
                fos.close();
                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getFileType(String fileName) {
        if (fileName != null) {
            int typeIndex = fileName.lastIndexOf(".");
            if (typeIndex != -1) {
                String fileType = fileName.substring(typeIndex + 1)
                        .toLowerCase();
                return fileType;
            }
        }
        return "";
    }

    private void moveFile2(File source, File destination) throws IOException {
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
            MediaScannerConnection.scanFile(this, new String[]{source.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                }
            });
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

    private void moveToRecycleAboveQ(File source, File destination, Uri muri, String newName) throws
            IOException {
        if (!destination.getParentFile().exists()) {
            destination.getParentFile().mkdirs();
        }

//        FileChannel source_channel = null;
//        FileChannel destination_channel = null;
        try {
            Boolean isRename = source.renameTo(destination);
            if (isRename) {
                if (!source.getPath().contains("'") && source.exists()) {
//                    getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "='" + source.getPath() + "'", null);
                }
            } else {
                moveFile2(source, destination);
//                if (!destination.exists()) {
//                    ContentResolver contentResolver = getContentResolver();
//                    Uri rootUri;
//                    Uri treeUri = Uri.parse(App.Companion.getInstance().getPref().getHiddenUri());
//
//                    File file = new File(Constant.recycle_path);
//                    if (!file.exists())
//                        file.mkdirs();
//
//                    DocumentFile tree = DocumentFile.fromTreeUri(this, treeUri);
//                    for (DocumentFile documentFile : tree.listFiles()) {
//                        if (documentFile.isDirectory() && documentFile.getName().equals(Constant.Recyclebin_Folder)) {
//                            rootUri = documentFile.getUri();
//
//                            contentResolver.takePersistableUriPermission(rootUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                            String rootDocumentId = DocumentsContract.getTreeDocumentId(rootUri);
//                            try {
//                                FileInputStream fileInputStream = (FileInputStream) contentResolver.openInputStream(muri);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            CreateFile createFile = new CreateFile(this, contentResolver, File.separator + newName
//                                    , rootUri, rootDocumentId, true, muri, true,
//                                    Utils.getMimeType(source.getName()));
//                            boolean isFileCreated = createFile.createNewFile(false, true);
//
//                            break;
////                            for (DocumentFile file : documentFile.listFiles()) {
////                                if (file.getName().equals(selected_hide_video_data.get(i).getName())) {
////                                    unhideFileOnAboveQ(this, file.getUri(), selected_hide_video_data.get(i).getName());
////                                    file.delete();
////                                    hide_video_data.remove(selected_hide_video_data.get(i));
////                                    database.deleteHide(selected_hide_video_data.get(i).getName());
////                                }
////                            }
//                        }
//                    }
//                }
                source.delete();
            }
            MediaScannerConnection.scanFile(this, new String[]{source.getPath()}, null, (path, uri) -> Log.i("ExternalStorage", "Scanned " + path + ":" + uri));
            MediaScannerConnection.scanFile(this, new String[]{destination.getPath()}, null, (path, uri) -> Log.i("ExternalStorage", "Scanned " + path + ":" + uri));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hide, menu);
        if (type == 0) {
            menu.findItem(R.id.menu_unhide).setVisible(true);
            menu.findItem(R.id.menu_delete).setVisible(true);
        } else if (type == 1) {
            menu.findItem(R.id.menu_unhide).setVisible(false);
            menu.findItem(R.id.menu_delete).setVisible(false);
        }
        return true;
    }

}
