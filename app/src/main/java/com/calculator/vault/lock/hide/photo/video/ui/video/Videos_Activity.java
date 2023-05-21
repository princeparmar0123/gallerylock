package com.calculator.vault.lock.hide.photo.video.ui.video;

import static android.os.Build.VERSION.SDK_INT;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.BaseColumns;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.calculator.vault.lock.hide.photo.video.App;
import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Delet_Data;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Hide_Data;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data;
import com.calculator.vault.lock.hide.photo.video.common.utils.Constant;
import com.calculator.vault.lock.hide.photo.video.common.utils.Constants;
import com.calculator.vault.lock.hide.photo.video.common.utils.CreateFile;
import com.calculator.vault.lock.hide.photo.video.common.utils.DriveServiceHelper;
import com.calculator.vault.lock.hide.photo.video.common.utils.FileUtils;
import com.calculator.vault.lock.hide.photo.video.common.utils.ImageFilePath;
import com.calculator.vault.lock.hide.photo.video.common.utils.Utils;
import com.calculator.vault.lock.hide.photo.video.ui.photos.Folder_Photo_Activity;
import com.calculator.vault.lock.hide.photo.video.ui.photos.PhotosActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Videos_Activity extends AppCompatActivity implements View.OnClickListener {

    public static LinearLayout floating_btn_video;

    private RecyclerView video_recycler;
    ArrayList<Media_Data> hide_video_data = new ArrayList<>();
    public static ArrayList<Media_Data> selected_hide_video_data = new ArrayList<>();
    private Hide_Videos_Adapter hide_videos_adapter;
    public static boolean IsSelectAll = false;
    public static LinearLayout video_btn_lay;
    private LinearLayout video_unhide;
    private LinearLayout video_delete;
    public static MenuItem selecter;
    private Database database;
    Boolean temp = false;
    private LinearLayout no_video;

    public static boolean temp_ads_video;

    private ProgressBar progress;
    View mBG;
    TextView mCount, permissionText;
    Button permissionBtn;

    boolean isDialogAllow = false;
    int resumeCount = 0;

    public int CREATE_HIDDEN_FOLDER = 123;
    public int ACCESS_HIDDEN_FOLDER = 12;
    public int ACCESS_RECYCLE_FOLDER = 121;

    File recycle_source;
    File recycle_destination;
    String recycle_name;
    private MediaMetadataRetriever retriever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Videos");
        toolbar.setTitleTextColor(getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        temp_ads_video = false;
        initView();
        setPermissionButton();
        initListener();
    }

    private void setPermissionButton() {
        if (SDK_INT > Build.VERSION_CODES.Q) {
            String destinationPath =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                            + File.separator +
                            ".Calculator Lock";
            File destination = new File(destinationPath);
            if (!destination.exists()) {
                permissionBtn.setText(R.string.pictures_folder_permission_btn);
                permissionBtn.setVisibility(View.VISIBLE);
                permissionText.setVisibility(View.VISIBLE);
                floating_btn_video.setVisibility(View.GONE);
            } else if (!App.Companion.getInstance().getPref().getHiddenPermission()) {
                permissionBtn.setText(R.string.hidden_folder_permission_btn);
                permissionBtn.setVisibility(View.VISIBLE);
                permissionText.setVisibility(View.VISIBLE);
                floating_btn_video.setVisibility(View.GONE);
            } else {
                permissionBtn.setVisibility(View.GONE);
                permissionText.setVisibility(View.GONE);
                floating_btn_video.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initListener() {
        video_unhide.setOnClickListener(this);
        video_delete.setOnClickListener(this);
        permissionBtn.setOnClickListener(this);
    }

    private void initView() {
        floating_btn_video = findViewById(R.id.floating_btn_video);
        video_recycler = findViewById(R.id.video_recycler);
        video_btn_lay = findViewById(R.id.video_btn_lay);
        video_unhide = findViewById(R.id.video_unhide);
        video_delete = findViewById(R.id.video_delete);
        no_video = findViewById(R.id.no_video);
        progress = findViewById(R.id.progress);
        mBG = findViewById(R.id.bg_l);
        mCount = findViewById(R.id.count);
        permissionBtn = findViewById(R.id.permissionBtn);
        permissionText = findViewById(R.id.permissionText);
        database = new Database(Videos_Activity.this);
        getVideos();
        floating_btn_video.setOnClickListener(view -> {
            bottomSheetDialog();
        });
    }

    private void bottomSheetDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_select, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        ImageView close = dialogView.findViewById(R.id.tvClose);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);

        LinearLayout gallery = dialogView.findViewById(R.id.lout_Gallery);
        LinearLayout folder = dialogView.findViewById(R.id.lout_Folder);

        tvTitle.setText("Choose Video From");

        close.setOnClickListener(view -> dialog.dismiss());
        gallery.setOnClickListener(view -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK);
            galleryIntent.setType("video/*");
            mUris.clear();
            startActivityForResult(galleryIntent, 101);
            dialog.dismiss();
            // floating_btn_photo.collapse();
        });
        folder.setOnClickListener(view -> {
            Intent intent1 = new Intent(this, VideoFolderActivity.class);
            startActivity(intent1);
            dialog.dismiss();
        });
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void getVideos() {
        hide_video_data.clear();
        if (SDK_INT > Build.VERSION_CODES.Q) {
            if (!TextUtils.isEmpty(App.Companion.getInstance().getPref().getHiddenUri())) {
                Uri treeUri = Uri.parse(App.Companion.getInstance().getPref().getHiddenUri());
                if (treeUri != null) {
                    this.getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    DocumentFile tree = DocumentFile.fromTreeUri(this, treeUri);
                    if (tree != null && tree.isDirectory()) {
                        for (DocumentFile documentFile : tree.listFiles()) {
                            if (documentFile.isDirectory() && documentFile.getName().equals(Constant.video_Folder)) {
                                for (DocumentFile file : documentFile.listFiles()) {
                                    Media_Data media_data = new Media_Data();
                                    media_data.setName(file.getName());
                                    media_data.setPath(file.getUri().toString());
                                    media_data.setFolder(String.valueOf(file.getParentFile()));
                                    File mFile = new File(file.getUri().getPath());
                                    media_data.setLength(String.valueOf(mFile.length()));
                                    Date lastModDate = new Date(mFile.lastModified());
                                    media_data.setModifieddate(String.valueOf(lastModDate));
                                    media_data.setAddeddate(String.valueOf(lastModDate));
                                    hide_video_data.add(media_data);
                                }
                            }
                        }
                    }
                    Collections.reverse(hide_video_data);
                }
                initAdapter();
            }
        } else {
            File folder = new File(Constant.videos_path);
            if (folder.isDirectory()) {
                File[] allFiles = folder.listFiles();
                for (File allFile : allFiles) {
                    Media_Data media_data = new Media_Data();
                    media_data.setName(allFile.getName());
                    media_data.setPath(allFile.getAbsolutePath());
                    media_data.setFolder(allFile.getParent());
                    File file = new File(allFile.getAbsolutePath());
                    media_data.setLength(String.valueOf(file.length()));
                    Date lastModDate = new Date(file.lastModified());
                    media_data.setModifieddate(String.valueOf(lastModDate));
                    media_data.setAddeddate(String.valueOf(lastModDate));
                    hide_video_data.add(media_data);
                }
                Collections.reverse(hide_video_data);
            }
            initAdapter();
        }

    }

    public String getVideoDuration(String filePath) {

        File file = new File(filePath);
        String formatted = "00:00";

        try {
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, Uri.parse(file.getAbsolutePath()));
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return formatted;
    }

    public static String getFileType(String fileName) {
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

    private void initAdapter() {
        if (hide_video_data.size() > 0) {
            video_recycler.setVisibility(View.VISIBLE);
            no_video.setVisibility(View.GONE);
            hide_videos_adapter = new Hide_Videos_Adapter(this, hide_video_data);
            video_recycler.setLayoutManager(new GridLayoutManager(Videos_Activity.this, 3, RecyclerView.VERTICAL, false));
            video_recycler.setAdapter(hide_videos_adapter);
        } else {
            video_recycler.setVisibility(View.GONE);
            no_video.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (progress.getVisibility() != View.VISIBLE) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    break;
                case R.id.select:
                    if (hide_videos_adapter != null) {
                        if (!IsSelectAll) {
                            IsSelectAll = true;
                            Hide_Videos_Adapter.IsLongClick = true;
                            item.setIcon(getResources().getDrawable(R.drawable.iv_select));
                            selected_hide_video_data.clear();
                            selected_hide_video_data.addAll(hide_video_data);
                            floating_btn_video.setVisibility(View.GONE);
                            video_btn_lay.setVisibility(View.VISIBLE);
                            setTitle("Photos(" + selected_hide_video_data.size() + ")");
                        } else {
                            IsSelectAll = false;
                            Hide_Videos_Adapter.IsLongClick = false;
                            floating_btn_video.setVisibility(View.VISIBLE);
                            video_btn_lay.setVisibility(View.GONE);
                            item.setIcon(getResources().getDrawable(R.drawable.iv_unselect));
                            selected_hide_video_data.clear();
                            selecter.setVisible(false);
                            setTitle("Videos");
                        }
                        hide_videos_adapter.notifyDataSetChanged();
                    }
                    break;
                case R.id.sort_adate:
                    Collections.sort(hide_video_data, (file1, file2) -> {
                        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                        long k = 0;
                        try {
                            Date date1 = format.parse(file1.getAddeddate());
                            Date date2 = format.parse(file2.getAddeddate());
                            if (date1 != null && date2 != null) {
                                k = date1.getTime() - date2.getTime();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (k > 0) {
                            return 1;
                        } else if (k == 0) {
                            return 0;
                        } else {
                            return -1;
                        }
                    });
                    Collections.reverse(hide_video_data);
                    initAdapter();
                    break;
                case R.id.sort_size:
                    Collections.sort(hide_video_data, (file1, file2) -> {
                        long k = Long.parseLong(file1.getLength()) - Long.parseLong(file2.getLength());
                        if (k > 0) {
                            return 1;
                        } else if (k == 0) {
                            return 0;
                        } else {
                            return -1;
                        }
                    });
                    Collections.reverse(hide_video_data);
                    initAdapter();
                    break;
                case R.id.sort_name:
                    Collections.sort(hide_video_data, (file1, file2) -> file1.getName().compareToIgnoreCase(file2.getName()));
                    Collections.reverse(hide_video_data);
                    initAdapter();
                    break;
            }
        }
        return false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sort_item, menu);
        selecter = menu.findItem(R.id.select);
        Videos_Activity.selecter.setVisible(false);
        return true;
    }

    @Override
    public void onBackPressed() {
//        if (progress.getVisibility() != View.VISIBLE) {
//            if (floating_btn_video.isExpanded()) {
//                floating_btn_video.collapse();
//            } else {
        if (video_btn_lay.getVisibility() == View.GONE) {
            super.onBackPressed();
        } else {
            IsSelectAll = false;
            Hide_Videos_Adapter.IsLongClick = false;
            floating_btn_video.setVisibility(View.VISIBLE);
            video_btn_lay.setVisibility(View.GONE);
            selecter.setIcon(getResources().getDrawable(R.drawable.iv_unselect));
            selected_hide_video_data.clear();
            selecter.setVisible(false);
            hide_videos_adapter.notifyDataSetChanged();
            setTitle("Videos");
        }
        //  }
        //  }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_unhide:
                progress.setVisibility(View.VISIBLE);
                mBG.setVisibility(View.VISIBLE);
                mCount.setVisibility(View.VISIBLE);
                mCount.setText("00/" + selected_hide_video_data.size());
                new Thread(() -> {
                    for (int i = 0; i < selected_hide_video_data.size(); i++) {
                        String sourcePath = selected_hide_video_data.get(i).getPath();
                        File source = new File(sourcePath);
                        Hide_Data hide_data = database.getHideData(source.getName());
                        if (SDK_INT > Build.VERSION_CODES.Q) {
                            Uri treeUri = Uri.parse(App.Companion.getInstance().getPref().getHiddenUri());

                            if (treeUri != null) {
                                this.getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                                DocumentFile tree = DocumentFile.fromTreeUri(this, treeUri);
                                for (DocumentFile documentFile : tree.listFiles()) {
                                    if (documentFile.isDirectory() && documentFile.getName().equals(Constant.video_Folder)) {
                                        for (DocumentFile file : documentFile.listFiles()) {
                                            if (file.getName().equals(selected_hide_video_data.get(i).getName())) {
                                                unhideFileOnAboveQ(this, file.getUri(), selected_hide_video_data.get(i).getName());
                                                file.delete();
                                                hide_video_data.remove(selected_hide_video_data.get(i));
                                                database.deleteHide(selected_hide_video_data.get(i).getName());
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            temp = false;
                            if (hide_data != null)
                                if (source.getName().equals(hide_data.getName())) {
                                    String destinationPath = hide_data.getPath();
                                    File destination;
                                    if (destinationPath == null) {
                                        destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/" + source.getName();
                                    }
                                    destination = new File(destinationPath);
                                    try {
                                        deleteFile(source, destination);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    temp = true;
                                    database.deleteHide(hide_data.getName());
                                    hide_video_data.remove(selected_hide_video_data.get(i));
                                }
                            if (!temp) {
                                String destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/" + source.getName();
                                File destination = new File(destinationPath);
                                try {
                                    deleteFile(source, destination);
                                    hide_video_data.remove(selected_hide_video_data.get(i));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            int finalI = i;
                            runOnUiThread(() -> mCount.setText((finalI < 10 ? "0" : "") + finalI + "/" + selected_hide_video_data.size()));
                        }
                    }
                    runOnUiThread(() -> {
                        Toast.makeText(this, "successfully UnHide", Toast.LENGTH_SHORT).show();
                        selected_hide_video_data.clear();
                        setTitle("Videos");
                        mCount.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                        mBG.setVisibility(View.GONE);
                        hide_videos_adapter.notifyDataSetChanged();
                        floating_btn_video.setVisibility(View.VISIBLE);
                        video_btn_lay.setVisibility(View.GONE);
                        Hide_Videos_Adapter.IsLongClick = false;
                        IsSelectAll = false;
                        selecter.setVisible(false);
                        selecter.setIcon(getResources().getDrawable(R.drawable.iv_unselect));
                        if (hide_video_data.size() > 0) {
                            video_recycler.setVisibility(View.VISIBLE);
                            no_video.setVisibility(View.GONE);
                        } else {
                            video_recycler.setVisibility(View.GONE);
                            no_video.setVisibility(View.VISIBLE);
                        }
                    });

                }).start();
                break;
            case R.id.video_delete:
                AlertDialog deletedialog = new AlertDialog.Builder(Videos_Activity.this)
                        .setTitle("Delete")
                        .setMessage("Are you sure,you want to delete this videos?")
                        .setPositiveButton("Delete", (dialog, whichButton) -> {
                            progress.setVisibility(View.VISIBLE);
                            mBG.setVisibility(View.VISIBLE);
                            mCount.setVisibility(View.VISIBLE);
                            mCount.setText("00/" + selected_hide_video_data.size());
                            new Thread(() -> {
                                for (int i = 0; i < selected_hide_video_data.size(); i++) {
                                    String sourcePath = selected_hide_video_data.get(i).getPath();
                                    File source = new File(sourcePath);
                                    String destinationPath = Constant.recycle_path + "/" + selected_hide_video_data.get(i).getName();
                                    File destination = new File(destinationPath);
                                    File recycleFile = new File(Constant.recycle_path);
                                    if (!recycleFile.exists())
                                        recycleFile.mkdirs();
                                    try {
                                        if (SDK_INT > Build.VERSION_CODES.Q) {
                                            destinationPath = Constant.recycle_path;
                                            destination = new File(destinationPath);

                                            String path = FileUtils.getRealPath(Videos_Activity.this,Uri.parse(sourcePath));
                                            if (path != null)
                                                source = new File(path);
//                                            if (!destination.exists()) {
//                                                Uri rootUri = Uri.parse(Prefrancemanager.getHiddenUri());
//                                                ContentResolver contentResolver = getContentResolver();
//                                                contentResolver.takePersistableUriPermission(rootUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                                                String rootDocumentId = DocumentsContract.getTreeDocumentId(rootUri);
//                                                String path = File.separator + "Recyclebin" +
//                                                        File.separator + ".nomedia";
//                                                CreateFile createFile = new CreateFile(getApplicationContext(), contentResolver, path, rootUri, rootDocumentId
//                                                        , false, "", false, "*/*");
//                                                boolean isFileCreated = createFile.createNewFile(true, false);
//                                            }
//                                            if (Prefrancemanager.getIsRecyclePermission()) {
                                            moveToRecycleAboveQ(source,
                                                    new File(destination + File.separator + selected_hide_video_data.get(i).getName()),
                                                    Uri.parse(selected_hide_video_data.get(i).getPath()),
                                                    selected_hide_video_data.get(i).getName());

//                                            DocumentFile file = DocumentFile.fromSingleUri(Videos_Activity.this, Uri.parse(selected_hide_video_data.get(i).getPath()));
//                                            file.delete();

//
// /                                            } else {
//                                                recycle_source = source;
//                                                recycle_destination = destination;
//                                                recycle_name = selected_hide_video_data.get(i).getName();
//                                                askPermissionForFragment(
//                                                        "Pictures%2F.Calculator Lock%2FRecyclebin",
//                                                        ACCESS_RECYCLE_FOLDER);
//                                            }
                                        } else {
                                            deleteFile(source, destination);
                                        }
                                        Delet_Data delet_data = new Delet_Data();
                                        Calendar calendar = Calendar.getInstance();
                                        delet_data.setName(selected_hide_video_data.get(i).getName());
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                        delet_data.setDate(dateFormat.format(calendar.getTime()));
                                        database.addDelete(delet_data);
                                        hide_video_data.remove(selected_hide_video_data.get(i));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                runOnUiThread(() -> {
                                    Toast.makeText(Videos_Activity.this, "successfully deleted", Toast.LENGTH_SHORT).show();
                                    selected_hide_video_data.clear();
                                    setTitle("Videos");
                                    progress.setVisibility(View.GONE);
                                    mBG.setVisibility(View.GONE);
                                    mCount.setVisibility(View.GONE);
                                    hide_videos_adapter.notifyDataSetChanged();
                                    Hide_Videos_Adapter.IsLongClick = false;
                                    IsSelectAll = false;
                                    floating_btn_video.setVisibility(View.VISIBLE);
                                    video_btn_lay.setVisibility(View.GONE);
                                    selecter.setVisible(false);
                                    selecter.setIcon(getResources().getDrawable(R.drawable.iv_unselect));
                                    if (hide_video_data.size() > 0) {
                                        video_recycler.setVisibility(View.VISIBLE);
                                        no_video.setVisibility(View.GONE);
                                    } else {
                                        video_recycler.setVisibility(View.GONE);
                                        no_video.setVisibility(View.VISIBLE);
                                    }
                                });
                            }).start();
                            dialog.dismiss();
                        })
                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                        .create();
                deletedialog.show();
                break;
            case R.id.permissionBtn:
                String destinationPath =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                                + File.separator +
                                ".Calculator Lock";
                File destination = new File(destinationPath);
                if (!destination.exists()) {
                    askPermissionForFragment(
                            "Pictures",
                            CREATE_HIDDEN_FOLDER);
                } else if (!App.Companion.getInstance().getPref().getHiddenPermission()) {
                    permissionBtn.setText(R.string.hidden_folder_permission_btn);
                    askPermissionForFragment(
                            "Pictures%2F.Calculator Lock",
                            ACCESS_HIDDEN_FOLDER);
                } else {
                    permissionBtn.setVisibility(View.GONE);
                    permissionText.setVisibility(View.GONE);
                    floating_btn_video.setVisibility(View.VISIBLE);
                }
                break;
        }
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


    ArrayList<Uri> mUris = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 101:
                    if (data != null) {
                        String filePath = ImageFilePath.getPath(Videos_Activity.this, data.getData());
                        File source = new File(filePath);
                        int id = database.getID();
                        String destinationPath = Constant.videos_path + "/" + id + "_" + source.getName();
                        File destination = new File(destinationPath);
                        try {
                            Hide_Data hide_data = new Hide_Data();
                            hide_data.setName(destination.getName());
                            hide_data.setPath(source.getPath());
                            database.addHide(hide_data);
                            if (source != null && destination != null) {
                                moveFile(source, destination);
                                if (SDK_INT > Build.VERSION_CODES.Q) {
//                                    destinationPath =
//                                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
//                                                    + File.separator +
//                                                    ".Calculator Lock";
//                                    destination = new File(destinationPath);
//                                    deleteFileOnAboveR(source, new File(destination + File.separator + source.getName()), filePath);
                                    mUris.add(getImageUriFromFile(filePath, this));
                                    requestDeletePermission(mUris);
                                    mUris.clear();
                                } else {
                                 //   deleteFile(source, destination);
                                }

                                if (App.Companion.getInstance().getPref().getSyncOn()) {
                                    GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
                                    GoogleAccountCredential googleAccountCredential = GoogleAccountCredential.usingOAuth2(Videos_Activity.this, Collections.singleton(DriveScopes.DRIVE_FILE));
                                    googleAccountCredential.setSelectedAccount(googleSignInAccount.getAccount());
                                    Drive drive = new Drive.Builder(
                                            AndroidHttp.newCompatibleTransport(),
                                            new GsonFactory(),
                                            googleAccountCredential)
                                            .setApplicationName(getResources().getString(R.string.app_name))
                                            .build();
                                    DriveServiceHelper driveServiceHelper = new DriveServiceHelper(drive);
                                    final ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                    final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                                    if (App.Companion.getInstance().getPref().getSyncWifi()) {
                                        if (wifi.isConnectedOrConnecting()) {
                                            File finalDestination1 = destination;
                                            driveServiceHelper.checkVideo(destination.getName()).addOnSuccessListener(aBoolean -> {
                                                if (!aBoolean) {
                                                    Log.d("Data", "onSuccess: ====> Upload " + finalDestination1.getName());
                                                    driveServiceHelper.insertVideoFile(finalDestination1.getName(), finalDestination1.getPath(), App.Companion.getInstance().getPref().getVideoFolder());
                                                }
                                            });
                                        }
                                    } else {
                                        File finalDestination = destination;
                                        driveServiceHelper.checkVideo(destination.getName()).addOnSuccessListener(aBoolean -> {
                                            if (!aBoolean) {
                                                Log.d("Data", "onSuccess: ====> Upload " + finalDestination.getName());
                                                driveServiceHelper.insertVideoFile(finalDestination.getName(), finalDestination.getPath(), App.Companion.getInstance().getPref().getVideoFolder());
                                            }
                                        });
                                    }
                                }
                                Toast.makeText(this, "successfully Added", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 123:
                    Uri rootUri = data.getData();
                    ContentResolver contentResolver = this.getContentResolver();
                    contentResolver.takePersistableUriPermission(rootUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    String rootDocumentId = DocumentsContract.getTreeDocumentId(rootUri);

                    String path = File.separator + ".Calculator Lock"
                            + File.separator + ".nomedia";

                    String destinationPath =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() +
                                    File.separator +
                                    ".Calculator Lock";
                    File destination = new File(destinationPath);
                    String filePath = ImageFilePath.getPath(Videos_Activity.this, data.getData());
                    File source = new File(filePath);

                    CreateFile createFile = new CreateFile(getApplicationContext(), contentResolver, path, rootUri, rootDocumentId
                            , false, "", false, "*/*");

                    boolean isFileCreated = createFile.createNewFile(true, false);
                    permissionBtn.setText(R.string.hidden_folder_permission_btn);
                    break;
                case 12:
                    Uri rootUri1 = data.getData();
                    ContentResolver contentResolver1 = this.getContentResolver();
                    contentResolver1.takePersistableUriPermission(rootUri1, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    String rootDocumentId1 = DocumentsContract.getTreeDocumentId(rootUri1);
                    App.Companion.getInstance().getPref().setHiddenUri(rootUri1.toString());
                    App.Companion.getInstance().getPref().setHiddenPermission(true);
                    permissionBtn.setVisibility(View.GONE);
                    permissionText.setVisibility(View.GONE);
                    floating_btn_video.setVisibility(View.VISIBLE);
                    break;
                case 121:
                    Uri rootUri2 = data.getData();
                    App.Companion.getInstance().getPref().setRecycleUri(rootUri2.toString());
                    App.Companion.getInstance().getPref().setRecyclePermission(true);
                    try {
                        moveToRecycleAboveQ(recycle_source,
                                new File(recycle_destination + File.separator + recycle_name),
                                Uri.parse(recycle_name),
                                recycle_name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    public void askPermissionForFragment(String targetDirectory, int requestCode) {
        if (SDK_INT > Build.VERSION_CODES.Q) {
            StorageManager storageManager =
                    (StorageManager) this.getSystemService(AppCompatActivity.STORAGE_SERVICE);
            Intent intent = storageManager.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
            Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");
            String scheme = uri.toString();
            scheme = scheme.replace("/root/", "/document/");
            scheme += "%3A" + targetDirectory;
            uri = Uri.parse(scheme);
            intent.putExtra("android.provider.extra.INITIAL_URI", uri);
            startActivityForResult(intent, requestCode);
        }

    }

    private void deleteFileOnAboveR(File source, File destination, String filePath) throws IOException {
        if (!destination.getParentFile().exists()) {
            destination.getParentFile().mkdirs();
        }

        FileChannel source_channel = null;
        FileChannel destination_channel = null;
        try {
            Boolean isRename = source.renameTo(destination);
            if (isRename) {
                if (!source.getPath().contains("'") && source.exists()) {
//                    getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "='" + source.getPath() + "'", null);
                }
            } else {
                if (!destination.exists()) {
                    ContentResolver contentResolver = getContentResolver();
                    Uri rootUri = Uri.parse(App.Companion.getInstance().getPref().getHiddenUri());
                    contentResolver.takePersistableUriPermission(
                            rootUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    );
                    String rootDocumentId = DocumentsContract.getTreeDocumentId(rootUri);
                    CreateFile createFile = new CreateFile(this, contentResolver, File.separator + source.getName()
                            , rootUri, rootDocumentId, true, source.getAbsolutePath(), true,
                            Utils.getMimeType(source.getName()));
                    boolean isFileCreated = createFile.createNewFile(false, true);
                }
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

    private void requestDeletePermission(List<Uri> uriList) {
        isDialogAllow = true;
        resumeCount = 0;
        for (int i = 0; i < uriList.size(); i++) {
            System.out.println("AFTER HIDE : " + uriList.get(i));
        }
        try {
            PendingIntent pi = null;
            if (SDK_INT > Build.VERSION_CODES.Q) {
                pi = MediaStore.createDeleteRequest(getContentResolver(), uriList);
            }
            startIntentSenderForResult(pi.getIntentSender(), 1001, null, 0, 0,
                    0, null);
        } catch (Exception e) {
            Log.e("TAG123", "requestDeletePermission: " + e.getMessage());
            System.out.println("AFTER HIDE : " + e.getMessage());
        }
        getVideos();
    }

    public Uri getImageUriFromFile(final String path, Context context) {
        ContentResolver resolver = context.getContentResolver();

        Cursor filecursor = resolver.query(MediaStore.Video.Media.getContentUri("external"),
                new String[]{BaseColumns._ID}, MediaStore.Video.VideoColumns.DATA + " = ?",
                new String[]{path}, MediaStore.Video.VideoColumns.DATE_ADDED + " desc");
        filecursor.moveToFirst();

        // try {

        if (filecursor.isAfterLast()) {
            filecursor.close();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Video.VideoColumns.DATA, path);
            return resolver.insert(MediaStore.Video.Media.getContentUri("external"), values);
        } else {
            int videoId = filecursor.getInt(filecursor.getColumnIndexOrThrow(BaseColumns._ID));
            Uri uri = MediaStore.Video.Media.getContentUri("external").buildUpon().appendPath(
                    Integer.toString(videoId)).build();
            filecursor.close();
            return uri;
        }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    private void moveToRecycleAboveQ(File source, File destination, Uri muri, String newName) throws
            IOException {
        if (!destination.getParentFile().exists()) {
            destination.getParentFile().mkdirs();
        }

        FileChannel source_channel = null;
        FileChannel destination_channel = null;
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
//                    Uri rootUri = Uri.parse(App.Companion.getInstance().getPref().getRecycleUri());
//                    contentResolver.takePersistableUriPermission(rootUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                    String rootDocumentId = DocumentsContract.getTreeDocumentId(rootUri);
//                    try {
//                        FileInputStream fileInputStream = (FileInputStream) contentResolver.openInputStream(muri);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    CreateFile createFile = new CreateFile(this, contentResolver, File.separator + newName
//                            , rootUri, rootDocumentId, true, muri, true,
//                            Utils.getMimeType(source.getName()));
//                    boolean isFileCreated = createFile.createNewFile(false, true);
//                }
                source.delete();
            }
            MediaScannerConnection.scanFile(this, new String[]{source.getPath()}, null, (path, uri) -> Log.i("ExternalStorage", "Scanned " + path + ":" + uri));
            MediaScannerConnection.scanFile(this, new String[]{destination.getPath()}, null, (path, uri) -> Log.i("ExternalStorage", "Scanned " + path + ":" + uri));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (source_channel != null) {
                source_channel.close();
            }
            if (destination_channel != null) {
                destination_channel.close();
            }
        }
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

    @Override
    protected void onPostResume() {
        Log.e("ONNPAUSEEE", "POST RESUME");
        if (isDialogAllow) {
            if (resumeCount == 1) {
                resumeCount = 0;
                isDialogAllow = false;
                Constants.isSplashScreen = false;
            }
            resumeCount += 1;

        }
        super.onPostResume();
    }

    @Override
    protected void onResume() {
        Log.e("ONNPAUSEEE", "RESUMME");
        getVideos();
        super.onResume();

    }

    private void moveFile(File source, File destination) throws IOException {
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
        Hide_Data hide_data = new Hide_Data();
        hide_data.setName(destination.getName());
        hide_data.setPath(source.getPath());
        database.addHide(hide_data);
    }

    private void deleteFile(File source, File destination) throws IOException {
        if (!destination.getParentFile().exists())
            destination.getParentFile().mkdirs();

        FileChannel source_channel = null;
        FileChannel destination_channel = null;
        try {
            Boolean isRename = source.renameTo(destination);
            if (isRename) {
                if (!source.getPath().contains("'") && source.exists()) {
                    getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaStore.Video.Media.DATA + "='" + source.getPath() + "'", null);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IsSelectAll = false;
        Hide_Videos_Adapter.IsLongClick = false;
    }
}
