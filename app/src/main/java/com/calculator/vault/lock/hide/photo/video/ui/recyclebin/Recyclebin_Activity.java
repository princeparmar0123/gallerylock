package com.calculator.vault.lock.hide.photo.video.ui.recyclebin;

import static android.os.Build.VERSION.SDK_INT;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data;
import com.calculator.vault.lock.hide.photo.video.common.utils.Constant;
import com.calculator.vault.lock.hide.photo.video.common.utils.CreateFile;
import com.calculator.vault.lock.hide.photo.video.common.utils.FileUtils;
import com.calculator.vault.lock.hide.photo.video.common.utils.Utils;
import com.calculator.vault.lock.hide.photo.video.ui.video.VideoShowActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class Recyclebin_Activity extends AppCompatActivity implements View.OnClickListener {

    public static RecyclerView recyclebin_recycler;
    private LinearLayout recycler_restore;
    private LinearLayout recycler_delete;
    ArrayList<Media_Data> recycle_data = new ArrayList<>();
    public static ArrayList<Media_Data> selected_recycle_data = new ArrayList<>();
    ArrayList<Delet_Data> check_delet_data = new ArrayList<>();
    Recyclebin_Adapter recyclebin_adapter;
    public static boolean IsSelectAll = false;
    public static MenuItem selecter;
    private Database database;
    public static LinearLayout no_recycler;
    public static LinearLayout btn_lay;
    private FrameLayout adContainer;
    public int CREATE_HIDDEN_FOLDER = 111;
    public int ACCESS_HIDDEN_FOLDER = 121;
    public int ACCESS_RECYCLE_FOLDER = 131;
    TextView permissionText;
    Button permissionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclebin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Recycle Bin");
        try {
            initView();
            initListener();
            // setPermissionButton();
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
            } else if (!App.Companion.getInstance().getPref().getHiddenPermission()) {
                permissionBtn.setText(R.string.hidden_folder_permission_btn);
                permissionBtn.setVisibility(View.VISIBLE);
                permissionText.setVisibility(View.VISIBLE);
            } else {
                try {
                    getRecycleData();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                permissionBtn.setVisibility(View.GONE);
                permissionText.setVisibility(View.GONE);
            }
        }
    }

    private void initListener() {
        recycler_delete.setOnClickListener(this);
        recycler_restore.setOnClickListener(this);
    }

    private void initView() throws ParseException {
        permissionBtn = findViewById(R.id.permissionBtn);
        permissionText = findViewById(R.id.permissionText);
        database = new Database(Recyclebin_Activity.this);
        recyclebin_recycler = findViewById(R.id.recyclebin_recycler);
        recycler_restore = findViewById(R.id.recycler_restore);
        recycler_delete = findViewById(R.id.recycler_delete);
        btn_lay = findViewById(R.id.btn_lay);
        no_recycler = findViewById(R.id.no_recycler);

//        adContainer = findViewById(R.id.adContainer);
//        AdmobAdManager.getInstance(Recyclebin_Activity.this).LoadAdaptiveBanner(Recyclebin_Activity.this, adContainer, getString(R.string.banner_id), null);
    }

    private void getRecycleData() throws ParseException {
        recycle_data.clear();
        if (SDK_INT > Build.VERSION_CODES.Q) {
            if (!TextUtils.isEmpty(App.Companion.getInstance().getPref().getHiddenUri())) {
                Uri treeUri = Uri.parse(App.Companion.getInstance().getPref().getHiddenUri());
                if (treeUri != null) {
                    this.getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    DocumentFile tree = DocumentFile.fromTreeUri(this, treeUri);
                    if (tree != null && tree.isDirectory()) {
                        for (DocumentFile documentFile : tree.listFiles()) {
                            if (documentFile.isDirectory() && documentFile.getName().equals(Constant.Recyclebin_Folder)) {
                                for (DocumentFile file : documentFile.listFiles()) {
                                    Media_Data media_data = new Media_Data();
                                    media_data.setName(file.getName());
                                    media_data.setPath(file.getUri().toString());
                                    File mFile = new File(String.valueOf(file.getUri()));
                                    media_data.setLength(String.valueOf(mFile.length()));
                                    Date lastModDate = new Date(mFile.lastModified());
                                    media_data.setModifieddate(String.valueOf(lastModDate));
                                    media_data.setAddeddate(String.valueOf(lastModDate));
                                    if (Utils.isImage(getFileType(file.getName())) || Utils.isVideo(getFileType(file.getName()))) {
                                        recycle_data.add(media_data);
                                    }
                                }
                            }
                        }
                    }
                    Collections.reverse(recycle_data);
                }
                initAdapter();
            } else {
                recyclebin_recycler.setVisibility(View.GONE);
                no_recycler.setVisibility(View.VISIBLE);
            }
        } else {
            File folder = new File(Constant.recycle_path);
            if (folder.isDirectory()) {
                File[] allFiles = folder.listFiles();
                for (int i = 0; i < allFiles.length; i++) {
                    Media_Data media_data = new Media_Data();
                    media_data.setName(allFiles[i].getName());
                    media_data.setPath(allFiles[i].getAbsolutePath());
                    media_data.setFolder(allFiles[i].getParent());
                    File file = new File(allFiles[i].getAbsolutePath());
                    media_data.setLength(String.valueOf(file.length()));
                    Date lastModDate = new Date(file.lastModified());
                    media_data.setModifieddate(String.valueOf(lastModDate));
                    media_data.setAddeddate(String.valueOf(lastModDate));
                    recycle_data.add(media_data);
                }
                Collections.reverse(recycle_data);
            }
            initCheck();
            initAdapter();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ACCESS_RECYCLE_FOLDER) {
                Uri uri = data.getData();
                App.Companion.getInstance().getPref().setRecyclePermission(true);
                App.Companion.getInstance().getPref().setRecycleUri(uri.toString());
            } else if (requestCode == CREATE_HIDDEN_FOLDER) {
                if (!App.Companion.getInstance().getPref().getHiddenPermission()) {
                    askPermissionForFragment(
                            "Pictures%2F.Calculator Lock",
                            ACCESS_HIDDEN_FOLDER);
                }
                Uri rootUri = data.getData();
                ContentResolver contentResolver = this.getContentResolver();
                contentResolver.takePersistableUriPermission(rootUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                String rootDocumentId = DocumentsContract.getTreeDocumentId(rootUri);

                String path = File.separator + ".Calculator Lock" +
                        File.separator + ".nomedia";

                CreateFile createFile = new CreateFile(getApplicationContext(), contentResolver, path, rootUri, rootDocumentId
                        , false, "", false, "*/*");

                boolean isFileCreated = createFile.createNewFile(true, false);
            } else if (requestCode == ACCESS_RECYCLE_FOLDER) {
                Uri rootUri1 = data.getData();
                ContentResolver contentResolver1 = this.getContentResolver();
                contentResolver1.takePersistableUriPermission(rootUri1, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                String rootDocumentId1 = DocumentsContract.getTreeDocumentId(rootUri1);
                App.Companion.getInstance().getPref().setHiddenUri(rootUri1.toString());
                App.Companion.getInstance().getPref().setHiddenPermission(true);
                try {
                    getRecycleData();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void restoreFileAboveQ(File source, File destination, Uri muri, String newName) throws
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
                if (!destination.exists()) {
                    ContentResolver contentResolver = getContentResolver();
                    Uri treeUri = Uri.parse(App.Companion.getInstance().getPref().getHiddenUri());

                    Uri rootUri = null;
                    DocumentFile tree = DocumentFile.fromTreeUri(this, treeUri);
                    if (tree != null && tree.isDirectory()) {
                        for (DocumentFile documentFile : tree.listFiles()) {

                            if (Utils.isImage(Utils.getFileType(newName)) && documentFile.isDirectory() && documentFile.getName().equals(Constant.photo_Folder))
                                rootUri = documentFile.getUri();
                            else if (Utils.isVideo(Utils.getFileType(newName)) && documentFile.isDirectory() && documentFile.getName().equals(Constant.video_Folder))
                                rootUri = documentFile.getUri();
                        }
                    }
                    if (rootUri != null) {
                        contentResolver.takePersistableUriPermission(rootUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        String rootDocumentId = DocumentsContract.getTreeDocumentId(rootUri);
                        try {
                            FileInputStream fileInputStream = (FileInputStream) contentResolver.openInputStream(muri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        CreateFile createFile = new CreateFile(this, contentResolver, File.separator + newName
                                , rootUri, rootDocumentId, true, muri, true,
                                Utils.getMimeType(source.getName()));
                        boolean isFileCreated = createFile.createNewFile(false, true);
                        Log.e("","isFileCreated"+ isFileCreated);
                    }
                }
//                moveFile2(source, destination);
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

    private void initCheck() throws ParseException {
        check_delet_data.clear();
        check_delet_data.addAll(database.getAllDelete());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -10);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String temp_date = dateFormat.format(calendar.getTime());
        Date date = dateFormat.parse(temp_date);
        for (int i = 0; i < check_delet_data.size(); i++) {
            if (calendar.getTimeInMillis() > dateFormat.parse(check_delet_data.get(i).getDate()).getTime()) {
                for (int j = 0; j < recycle_data.size(); j++) {
                    if (check_delet_data.get(i).getName().equals(recycle_data.get(j).getName())) {
                        String sourcePath = recycle_data.get(j).getPath();
                        File source = new File(sourcePath);
                        source.delete();
                        database.deleteData(check_delet_data.get(i).getName());
                        database.deleteHide(check_delet_data.get(i).getName());
                        recycle_data.remove(recycle_data.get(j));
                    }
                }
            }
        }
    }

    private void initAdapter() {
        if (recycle_data.size() > 0) {
            recyclebin_recycler.setVisibility(View.VISIBLE);
            no_recycler.setVisibility(View.GONE);
            recyclebin_adapter = new Recyclebin_Adapter(Recyclebin_Activity.this, recycle_data);
            recyclebin_recycler.setLayoutManager(new GridLayoutManager(Recyclebin_Activity.this, 3, RecyclerView.VERTICAL, false));
            recyclebin_recycler.setAdapter(recyclebin_adapter);
        } else {
            recyclebin_recycler.setVisibility(View.GONE);
            no_recycler.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.select:
                if (recyclebin_adapter != null) {
                    if (!IsSelectAll) {
                        IsSelectAll = true;
                        Recyclebin_Adapter.IsLongClick = true;
                        item.setIcon(getResources().getDrawable(R.drawable.iv_select));
                        btn_lay.setVisibility(View.VISIBLE);
                        selected_recycle_data.clear();
                        selected_recycle_data.addAll(recycle_data);
                    } else {
                        IsSelectAll = false;
                        Recyclebin_Adapter.IsLongClick = false;
                        item.setIcon(getResources().getDrawable(R.drawable.iv_unselect));
                        selected_recycle_data.clear();
                        btn_lay.setVisibility(View.GONE);
                        selecter.setVisible(false);
                    }
                    recyclebin_adapter.notifyDataSetChanged();
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select, menu);
        selecter = menu.findItem(R.id.select);
        selecter.setVisible(false);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recycler_delete:
                if (selected_recycle_data.size() > 0) {
                    AlertDialog deletedialog = new AlertDialog.Builder(Recyclebin_Activity.this)
                            .setTitle("Delete")
                            .setMessage("Are you sure,you want to delete this files?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    for (int i = 0; i < selected_recycle_data.size(); i++) {
                                        if (SDK_INT > Build.VERSION_CODES.Q) {
                                            DocumentFile file = DocumentFile.fromSingleUri(Recyclebin_Activity.this, Uri.parse(selected_recycle_data.get(i).getPath()));
                                            file.delete();
                                        } else {
                                            String sourcePath = selected_recycle_data.get(i).getPath();
                                            File source = new File(sourcePath);
                                            source.delete();
                                        }
                                        database.deleteHide(selected_recycle_data.get(i).getName());
                                        database.deleteData(selected_recycle_data.get(i).getName());
                                        recycle_data.remove(selected_recycle_data.get(i));
                                    }
                                    Toast.makeText(Recyclebin_Activity.this, "Delete Data...", Toast.LENGTH_SHORT).show();
                                    selected_recycle_data.clear();
                                    IsSelectAll = false;
                                    Recyclebin_Adapter.IsLongClick = false;
                                    recyclebin_adapter.notifyDataSetChanged();
                                    selecter.setIcon(getResources().getDrawable(R.drawable.iv_unselect));
                                    selecter.setVisible(false);
                                    btn_lay.setVisibility(View.GONE);
                                    if (recycle_data.size() > 0) {
                                        recyclebin_recycler.setVisibility(View.VISIBLE);
                                        no_recycler.setVisibility(View.GONE);
                                    } else {
                                        recyclebin_recycler.setVisibility(View.GONE);
                                        no_recycler.setVisibility(View.VISIBLE);
                                    }
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    deletedialog.show();
                } else {
                    Toast.makeText(this, "select images and videos...", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.recycler_restore:
                if (selected_recycle_data.size() > 0) {
                    for (int i = 0; i < selected_recycle_data.size(); i++) {
                        String sourcePath = selected_recycle_data.get(i).getPath();
                        File source = new File(sourcePath);
                        if (selected_recycle_data.get(i).getName().contains("Intruder_")) {
                            String destinationPath = Constant.intruder_path + "/" + selected_recycle_data.get(i).getName();
                            File destination = new File(destinationPath);
                            try {
                                restoreFile(source, destination);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (SDK_INT > Build.VERSION_CODES.Q) {
                                String destinationPath = Constant.home_path + File.separator + selected_recycle_data.get(i).getName();
                                if (Utils.isImage(Utils.getFileType(selected_recycle_data.get(i).getName())))
                                    destinationPath = Constant.photos_path + File.separator + selected_recycle_data.get(i).getName();
                                else if (Utils.isVideo(Utils.getFileType(selected_recycle_data.get(i).getName())))
                                    destinationPath = Constant.videos_path + File.separator + selected_recycle_data.get(i).getName();

                                File destination = new File(destinationPath);
                                String path = FileUtils.getRealPath(Recyclebin_Activity.this, Uri.parse(sourcePath));
                                if (path != null)
                                    source = new File(path);

                                try {
                                    restoreFileAboveQ(source, destination, Uri.parse(selected_recycle_data.get(i).getPath()), selected_recycle_data.get(i).getName());
                                    // deleteSelectedFile(selected_recycle_data.get(i).getName());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (isImage(source)) {
                                    String destinationPath = Constant.photos_path + "/" + selected_recycle_data.get(i).getName();
                                    File destination = new File(destinationPath);
                                    try {
                                        restoreFile(source, destination);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    String destinationPath = Constant.videos_path + "/" + selected_recycle_data.get(i).getName();
                                    File destination = new File(destinationPath);
                                    try {
                                        restoreFile(source, destination);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        recycle_data.remove(selected_recycle_data.get(i));
                        database.deleteData(selected_recycle_data.get(i).getName());
                    }
                    Toast.makeText(this, "Restore Data...", Toast.LENGTH_SHORT).show();
                    selected_recycle_data.clear();
                    recyclebin_adapter.notifyDataSetChanged();
                    selecter.setVisible(false);
                    IsSelectAll = false;
                    Recyclebin_Adapter.IsLongClick = false;
                    btn_lay.setVisibility(View.GONE);
                    selecter.setIcon(getResources().getDrawable(R.drawable.iv_unselect));
                    if (recycle_data.size() > 0) {
                        recyclebin_recycler.setVisibility(View.VISIBLE);
                        no_recycler.setVisibility(View.GONE);
                    } else {
                        recyclebin_recycler.setVisibility(View.GONE);
                        no_recycler.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(this, "select images and videos...", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void deleteSelectedFile(String imageName) {
        if (!TextUtils.isEmpty(App.Companion.getInstance().getPref().getRecycleUri())) {
            Uri treeUri = Uri.parse(App.Companion.getInstance().getPref().getRecycleUri());
            if (treeUri != null) {
                getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                DocumentFile tree = DocumentFile.fromTreeUri(this, treeUri);
                if (tree != null && tree.isDirectory()) {
                    for (DocumentFile file : tree.listFiles()) {
                        if (file.getName().equals(imageName)) {
                            file.delete();
                        }
                    }
                }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IsSelectAll = false;
        selected_recycle_data.clear();
        Recyclebin_Adapter.IsLongClick = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            getRecycleData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (selected_recycle_data.size() != 0) {
            IsSelectAll = false;
            selecter.setIcon(getResources().getDrawable(R.drawable.iv_unselect));
            selected_recycle_data.clear();
            selecter.setVisible(false);
            btn_lay.setVisibility(View.GONE);
            Recyclebin_Adapter.IsLongClick = false;
            recyclebin_adapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }
}


