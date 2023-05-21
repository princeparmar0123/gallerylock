package com.calculator.vault.lock.hide.photo.video.ui.photos;

import static android.os.Build.VERSION.SDK_INT;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.calculator.vault.lock.hide.photo.video.App;
import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Hide_Data;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data;
import com.calculator.vault.lock.hide.photo.video.common.utils.Constant;
import com.calculator.vault.lock.hide.photo.video.common.utils.CreateFile;
import com.calculator.vault.lock.hide.photo.video.common.utils.DriveServiceHelper;
import com.calculator.vault.lock.hide.photo.video.common.utils.Utils;
import com.calculator.vault.lock.hide.photo.video.ui.video.SpinnerAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Folder_Photo_Activity extends AppCompatActivity implements View.OnClickListener {

    private Spinner navigationSpinner;
    private Toolbar photo_toolbar;
    ArrayList<Media_Data> media_datas = new ArrayList<>();
    ArrayList<Media_Data> folder_media_datas = new ArrayList<>();
    public static ArrayList<Media_Data> select_photo_datas = new ArrayList<>();
    ArrayList<String> folder = new ArrayList<>();
    private RecyclerView local_photo_recycler;
    public static Button photo_hide;
    private Photos_Adapters photos_adapter;
    public static boolean IsSelectAll = false;
    public static MenuItem selecter_fphoto;
    private Database database;
    private LinearLayout no_photo_folder;
    private ProgressBar progress;
    View mBG;
    TextView mCount;

    public int CREATE_HIDDEN_FOLDER = 123;
    public int ACCESS_HIDDEN_FOLDER = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_photo);
        initView();
        initListener();
    }


    private void initListener() {
        navigationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getColor(R.color.White));

                if (position != 0) {
                    folder_media_datas.clear();
                    for (int i = 0; i < media_datas.size(); i++) {
                        if (media_datas.get(i).getFolder() != null) {
                            if (media_datas.get(i).getFolder().equals(folder.get(position))) {
                                folder_media_datas.add(media_datas.get(i));
                            }
                        }
                    }
                    initAdapter(folder_media_datas);
                } else {
                    initAdapter(media_datas);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        photo_hide.setOnClickListener(this);
    }

    private void initView() {
        database = new Database(Folder_Photo_Activity.this);

        photo_toolbar = findViewById(R.id.photo_toolbar);
        navigationSpinner = photo_toolbar.findViewById(R.id.navigationSpinner);
        local_photo_recycler = findViewById(R.id.local_photo_recycler);
        no_photo_folder = findViewById(R.id.no_photo_folder);
        progress = findViewById(R.id.progress);
        mBG = findViewById(R.id.bg_l);
        mCount = findViewById(R.id.count);
        photo_hide = findViewById(R.id.photo_hide);
        setSupportActionBar(photo_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        progress.setVisibility(View.VISIBLE);
        new Thread(this::getImages).start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (progress.getVisibility() != View.VISIBLE) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    break;
                case R.id.select:
                    if (!IsSelectAll) {
                        IsSelectAll = true;
                        item.setIcon(getResources().getDrawable(R.drawable.iv_select));
                        if (navigationSpinner.getSelectedItemPosition() == 0) {
                            select_photo_datas.clear();
                            select_photo_datas.addAll(media_datas);
                        } else {
                            select_photo_datas.clear();
                            select_photo_datas.addAll(folder_media_datas);
                        }
                    } else {
                        IsSelectAll = false;
                        item.setIcon(getResources().getDrawable(R.drawable.iv_unselect));
                        select_photo_datas.clear();
                        selecter_fphoto.setVisible(false);
                    }
                    photo_hide.setText("Hide (" + select_photo_datas.size() + ")");
                    photos_adapter.notifyDataSetChanged();
                    break;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select, menu);
        selecter_fphoto = menu.findItem(R.id.select);
        selecter_fphoto.setVisible(false);
        return true;
    }

    public void getImages() {
        media_datas.clear();
        folder.clear();
        folder.add("All Images");
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.TITLE, MediaStore.Images.Media.DATE_MODIFIED, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DATE_ADDED};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        try {
            if (cursor != null && cursor.moveToFirst()) {
//                File file = null;
                while (cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(projection[0]));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(projection[1]));
                    if (title != null) {
                    } else {
                        title = "";
                    }
                    String modified_date = cursor.getString(cursor.getColumnIndexOrThrow(projection[2]));
                    String bucketName = cursor.getString(cursor.getColumnIndexOrThrow(projection[3]));
                    long size = cursor.getLong(4);
                    String added_date = cursor.getString(cursor.getColumnIndexOrThrow(projection[5]));
//                    try {
//                        file = new File(path);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                    if (!folder.contains(bucketName)) {
                        if (bucketName != null) {
                            folder.add(bucketName);
                        }
                    }

//                    if (file != null && file.exists()) {
                    Media_Data media_data = new Media_Data();
                    media_data.setName(title);
                    media_data.setPath(path);
                    media_data.setFolder(bucketName);
                    media_data.setLength(String.valueOf(size));
                    media_data.setAddeddate(added_date);
                    media_data.setModifieddate(modified_date);
                    media_datas.add(media_data);
//                    }
                }
                cursor.close();
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    initSpinner();
                    initAdapter(media_datas);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initAdapter(ArrayList<Media_Data> data) {
        photos_adapter = new Photos_Adapters(this, data);
        local_photo_recycler.setLayoutManager(new GridLayoutManager(Folder_Photo_Activity.this, 3, RecyclerView.VERTICAL, false));
        local_photo_recycler.setAdapter(photos_adapter);
        local_photo_recycler.setItemViewCacheSize(data.size());
        local_photo_recycler.setHasFixedSize(true);
        local_photo_recycler.setDrawingCacheEnabled(true);
        local_photo_recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    private void initSpinner() {
        if (media_datas.size() > 0) {
            navigationSpinner.setVisibility(View.VISIBLE);
            photo_hide.setVisibility(View.VISIBLE);
            no_photo_folder.setVisibility(View.GONE);
            SpinnerAdapter spinner_adapter = new SpinnerAdapter(Folder_Photo_Activity.this, folder);
            spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            navigationSpinner.setAdapter(spinner_adapter);
        } else {
            navigationSpinner.setVisibility(View.GONE);
            photo_hide.setVisibility(View.GONE);
            no_photo_folder.setVisibility(View.VISIBLE);
        }
    }

    ArrayList<Uri> mUris = new ArrayList<>();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo_hide:
//                if (SDK_INT >= Build.VERSION_CODES.R) {
//                    hideAboveR();
//                } else {
                photo_hide.setEnabled(false);
                progress.setVisibility(View.VISIBLE);
                mBG.setVisibility(View.VISIBLE);
                mCount.setVisibility(View.VISIBLE);
                navigationSpinner.setEnabled(false);
                mCount.setText("00/" + select_photo_datas.size());
                new Thread(() -> {
                    for (int i = 0; i < select_photo_datas.size(); i++) {
                        String sourcePath = select_photo_datas.get(i).getPath();
                        File source = new File(sourcePath);
                        int id = database.getID();
                        String destinationPath = Constant.photos_path + "/" + id + "_" + source.getName();
                        File destination = new File(destinationPath);
                        try {
                            moveFile(source, destination);

                            if (App.Companion.getInstance().getPref().getSyncOn()) {
                                googleSync(destination);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int finalI = i;
                        if (SDK_INT >= Build.VERSION_CODES.R) mUris.add(getImageUriFromFile(sourcePath, this));
                        runOnUiThread(() -> mCount.setText((finalI < 10 ? "0" : "") + finalI + "/" + select_photo_datas.size()));
                    }
                    runOnUiThread(() -> {
                        if (SDK_INT >= Build.VERSION_CODES.R) requestDeletePermission(mUris);

                        photo_hide.setEnabled(true);
                        progress.setVisibility(View.GONE);
                        mBG.setVisibility(View.GONE);
                        navigationSpinner.setEnabled(true);
                        mCount.setVisibility(View.GONE);
                        Toast.makeText(Folder_Photo_Activity.this, "Hide successfully", Toast.LENGTH_SHORT).show();
                        select_photo_datas.clear();
                        finish();
                    });
                }).start();
//                }

                break;
        }
    }

    private void googleSync(File destination) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(Folder_Photo_Activity.this);
        GoogleAccountCredential googleAccountCredential = GoogleAccountCredential.usingOAuth2(Folder_Photo_Activity.this, Collections.singleton(DriveScopes.DRIVE_FILE));
        googleAccountCredential.setSelectedAccount(googleSignInAccount.getAccount());
        Drive drive = new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                googleAccountCredential)
                .setApplicationName(getResources().getString(R.string.app_name))
                .build();
        DriveServiceHelper driveServiceHelper = new DriveServiceHelper(drive);
        final ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (App.Companion.getInstance().getPref().getSyncWifi()) {
            if (wifi.isConnectedOrConnecting()) {
                File finalDestination = destination;
                driveServiceHelper.checkImage(destination.getName()).addOnSuccessListener(new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (!aBoolean) {
                            Log.d("Data", "onSuccess: ====> Upload " + finalDestination.getName());
                            driveServiceHelper.insertImageFile(finalDestination.getName(), finalDestination.getPath(), App.Companion.getInstance().getPref().getImageFolder());
                        }
                    }
                });
            }
        } else {
            File finalDestination1 = destination;
            driveServiceHelper.checkImage(destination.getName()).addOnSuccessListener(new OnSuccessListener<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    if (!aBoolean) {
                        Log.d("Data", "onSuccess: ====> Upload " + finalDestination1.getName());
                        driveServiceHelper.insertImageFile(finalDestination1.getName(), finalDestination1.getPath(), App.Companion.getInstance().getPref().getImageFolder());
                    }
                }
            });
        }
    }

    private void hideAboveR() {
        photo_hide.setEnabled(false);
        progress.setVisibility(View.VISIBLE);
        mBG.setVisibility(View.VISIBLE);
        mCount.setVisibility(View.VISIBLE);
        navigationSpinner.setEnabled(false);
        mCount.setText("00/" + select_photo_datas.size());
        new Thread(() -> {
            for (int i = 0; i < select_photo_datas.size(); i++) {
                String sourcePath = select_photo_datas.get(i).getPath();
                File source = new File(sourcePath);
                int id = database.getID();
                String destinationPath = Constant.photos_path + "/" + id + "_" + source.getName();
                File destination = new File(destinationPath);
                try {
                    destinationPath = Constant.photos_path;
//                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + File.separator +
//                                    ".Calculator Lock";
                    destination = new File(destinationPath);
                    moveFileonAboveR(source, new File(destination + File.separator + source.getName()));
                    mUris.add(getImageUriFromFile(sourcePath, this));
                    if (App.Companion.getInstance().getPref().getSyncOn()) {
                        googleSync(destination);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int finalI = i;
                runOnUiThread(() -> mCount.setText((finalI < 10 ? "0" : "") + finalI + "/" + select_photo_datas.size()));
            }
            requestDeletePermission(mUris);
            runOnUiThread(() -> {
                photo_hide.setEnabled(true);
                progress.setVisibility(View.GONE);
                mBG.setVisibility(View.GONE);
                navigationSpinner.setEnabled(true);
                mCount.setVisibility(View.GONE);
                Toast.makeText(Folder_Photo_Activity.this, "Hide successfully", Toast.LENGTH_SHORT).show();
                select_photo_datas.clear();
                finish();
            });
        }).start();
    }

    public Uri getImageUriFromFile(final String path, Context context) {
        ContentResolver resolver = context.getContentResolver();

        Cursor filecursor = resolver.query(MediaStore.Images.Media.getContentUri("external"),
                new String[]{BaseColumns._ID}, MediaStore.Images.ImageColumns.DATA + " = ?",
                new String[]{path}, MediaStore.Images.ImageColumns.DATE_ADDED + " desc");
        filecursor.moveToFirst();

        if (filecursor.isAfterLast()) {
            filecursor.close();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.ImageColumns.DATA, path);
            return resolver.insert(MediaStore.Images.Media.getContentUri("external"), values);
        } else {
            int imageId = filecursor.getInt(filecursor.getColumnIndexOrThrow(BaseColumns._ID));
            Uri uri = MediaStore.Images.Media.getContentUri("external").buildUpon().appendPath(
                    Integer.toString(imageId)).build();
            filecursor.close();
            return uri;
        }
    }

    private void requestDeletePermission(List<Uri> uriList) {
        for (int i = 0; i < uriList.size(); i++) {
            System.out.println("AFTER HIDE : " + uriList.get(i));
        }
        try {
            PendingIntent pi = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                pi = MediaStore.createDeleteRequest(getContentResolver(), uriList);
            }
            startIntentSenderForResult(pi.getIntentSender(), 1001, null, 0, 0,
                    0, null);
        } catch (Exception e) {
            System.out.println("AFTER HIDE : " + e.getMessage());
        }
        getImages();
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

    private void moveFileonAboveR(File source, File destination) throws IOException {
        if (!destination.getParentFile().exists())
            destination.getParentFile().mkdirs();
        FileChannel source_channel = null;
        FileChannel destination_channel = null;
        try {
            Boolean isRename = source.renameTo(destination);
            if (isRename) {
                if (!source.getPath().contains("'")) {
//                    getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "='" + source.getPath() + "'", null);
                }
            } else {
                if (!destination.exists()) {
                    ContentResolver contentResolver = getContentResolver();
                    Uri rootUri = Uri.parse(App.Companion.getInstance().getPref().getHiddenUri());
                    contentResolver.takePersistableUriPermission(
                            rootUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    );
                    String rootDocumentId = "";
                    DocumentFile tree = DocumentFile.fromTreeUri(this, rootUri);
                    for (DocumentFile documentFile : tree.listFiles()) {
                        if (documentFile.isDirectory()) {
                            if (documentFile.getName().contains("Photos")) {
                                rootUri = documentFile.getUri();
                                rootDocumentId = DocumentsContract.getTreeDocumentId(documentFile.getUri());
                            }
                        }
                    }
//                    String rootDocumentId = DocumentsContract.getTreeDocumentId(rootUri);
                    if (rootDocumentId.isEmpty())
                        rootDocumentId = DocumentsContract.getTreeDocumentId(rootUri);

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
        Hide_Data hide_data = new Hide_Data();
        hide_data.setName(destination.getName());
        hide_data.setPath(source.getPath());
        database.addHide(hide_data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IsSelectAll = false;
    }

    @Override
    public void onBackPressed() {
        if (progress.getVisibility() != View.VISIBLE) {
            if (select_photo_datas.size() != 0) {
                IsSelectAll = false;
                selecter_fphoto.setIcon(getResources().getDrawable(R.drawable.iv_unselect));
                select_photo_datas.clear();
                selecter_fphoto.setVisible(false);
                photo_hide.setText("Hide (" + select_photo_datas.size() + ")");
                photos_adapter.notifyDataSetChanged();
            } else {
                super.onBackPressed();
            }
        }

    }
}
