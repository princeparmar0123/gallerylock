package com.calculator.vault.lock.hide.photo.video.ui.intruderSelfie;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.calculator.vault.lock.hide.photo.video.common.utils.CreateFile;
import com.calculator.vault.lock.hide.photo.video.common.utils.FileUtils;
import com.calculator.vault.lock.hide.photo.video.common.utils.Utils;
import com.calculator.vault.lock.hide.photo.video.floatingbutton.FloatingActionButton;
import com.calculator.vault.lock.hide.photo.video.ui.photos.PhotosActivity;
import com.suke.widget.SwitchButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

public class Intruder_Activity extends AppCompatActivity {

    private Dialog intruder_dialog;
    private RecyclerView intruder_recycler;
    ArrayList<Media_Data> intruder_data = new ArrayList<>();
    public static ArrayList<Media_Data> selected_intruder_data = new ArrayList<>();
    public static ArrayList<Media_Data> duplicate_selected_intruder_data = new ArrayList<>();
    private Intruder_Adapter intruder_adapter;
    public static boolean IsSelectAll = false;
    public static FloatingActionButton floating_btn_intruder;
    public static MenuItem selecter;
    private Database database;
    private int camera_per;
    private SwitchButton d_inruder_switch;
    private LinearLayout no_intruder;
    private FrameLayout adContainer;

    Button permissionBtn;
    TextView permissionText;
    public int CREATE_HIDDEN_FOLDER = 123;
    public int ACCESS_HIDDEN_FOLDER = 12;
    public int ACCESS_RECYCLE_FOLDER = 121;

    File recycle_source;
    File recycle_destination;
    String recycle_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intruder);
        //  Amplitude.getInstance().logEvent("Create Intruder_Activity");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Intruder Selfie");
        adContainer = findViewById(R.id.adContainer);
        //  AdmobAdManager.getInstance(Intruder_Activity.this).LoadAdaptiveBanner(Intruder_Activity.this, adContainer, getString(R.string.banner_id), null);
        if (!App.Companion.getInstance().getPref().getGetIntruder()) {
            initDialog();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                askPermission();
            }
        }
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
            } else if (!App.Companion.getInstance().getPref().getHiddenPermission()) {
                permissionBtn.setText(R.string.hidden_folder_permission_btn);
                permissionBtn.setVisibility(View.VISIBLE);
                permissionText.setVisibility(View.VISIBLE);
            } else {
                permissionBtn.setVisibility(View.GONE);
                permissionText.setVisibility(View.GONE);
            }
        }
    }

    public void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 100);
    }

    private void initListener() {
        floating_btn_intruder.setOnClickListener(v -> {
            AlertDialog deletedialog = new AlertDialog.Builder(Intruder_Activity.this)
                    .setTitle("Delete")
                    .setMessage("Are you sure,you want to delete this selfies?")
                    .setPositiveButton("Delete", (dialog, whichButton) -> {
                        for (int i = 0; i < selected_intruder_data.size(); i++) {
                            String sourcePath = selected_intruder_data.get(i).getPath();
                            File source = new File(sourcePath);
                            String destinationPath = Constant.recycle_path + "/" + selected_intruder_data.get(i).getName();
                            File destination = new File(destinationPath);
                            try {
                                if (SDK_INT > Build.VERSION_CODES.Q) {
                                    destinationPath = Constant.recycle_path + "/" + selected_intruder_data.get(i).getName();
                                    destination = new File(destinationPath);

                                    destinationPath = Constant.recycle_path;
                                    destination = new File(destinationPath);
                                    String path = FileUtils.getRealPath(Intruder_Activity.this,Uri.parse(sourcePath));
                                    if (path != null)
                                        source = new File(path);

                                    moveToRecycleAboveQ(source,
                                            new File(destination + File.separator + selected_intruder_data.get(i).getName()),
                                            Uri.parse(selected_intruder_data.get(i).getPath()),
                                            selected_intruder_data.get(i).getName());

//                                    if (!destination.exists()) {
//                                        Uri rootUri = Uri.parse(App.Companion.getInstance().getPref().getHiddenUri());
//                                        ContentResolver contentResolver = getContentResolver();
//                                        contentResolver.takePersistableUriPermission(rootUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                                        String rootDocumentId = DocumentsContract.getTreeDocumentId(rootUri);
//                                        String path = File.separator + "Recyclebin" +
//                                                File.separator + ".nomedia";
//                                        CreateFile createFile = new CreateFile(getApplicationContext(), contentResolver, path, rootUri, rootDocumentId
//                                                , false, "", false, "*/*");
//                                        boolean isFileCreated = createFile.createNewFile(true, false);
//                                    }
//                                    if (App.Companion.getInstance().getPref().isRecyclePermission()) {
//                                        moveToRecycleAboveQ(source, new File(destination + File.separator + selected_intruder_data.get(i).getName()),
//                                                Uri.parse(selected_intruder_data.get(i).getPath()),
//                                                selected_intruder_data.get(i).getName());
//                                        deleteSelectedFile(selected_intruder_data.get(i).getName());
//                                    } else {
//                                        recycle_source = source;
//                                        recycle_destination = destination;
//                                        recycle_name = selected_intruder_data.get(i).getName();
//                                        askPermissionForFragment(
//                                                "Pictures%2F.Calculator Lock%2FRecyclebin",
//                                                ACCESS_RECYCLE_FOLDER);
//                                    }
//                                        duplicate_selected_intruder_data.add(selected_intruder_data.get(i));
//                                        mUri.add(getImageUriFromFile(selected_intruder_data.get(i).getPath(),Intruder_Activity.this));
                                } else {
                                    deleteFile(source, destination);
                                }
                                Delet_Data delet_data = new Delet_Data();
                                Calendar calendar = Calendar.getInstance();
                                delet_data.setName(selected_intruder_data.get(i).getName());
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                delet_data.setDate(dateFormat.format(calendar.getTime()));
                                database.addDelete(delet_data);
                                intruder_data.remove(selected_intruder_data.get(i));
                                Toast.makeText(Intruder_Activity.this, "successfully deleted", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        selected_intruder_data.clear();
                        intruder_adapter.notifyDataSetChanged();
                        floating_btn_intruder.setVisibility(View.GONE);
                        Intruder_Adapter.IsLongClick = false;
                        IsSelectAll = false;
                        selecter.setVisible(false);
                        selecter.setIcon(getResources().getDrawable(R.drawable.iv_unselect));
                        if (intruder_data.size() > 0) {
                            intruder_recycler.setVisibility(View.VISIBLE);
                            no_intruder.setVisibility(View.GONE);
                        } else {
                            intruder_recycler.setVisibility(View.GONE);
                            no_intruder.setVisibility(View.VISIBLE);
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

        });
        permissionBtn.setOnClickListener(v -> {
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
            }
        });
    }

    private void moveToRecycleAboveQ(File source, File destination, Uri muri, String newName) throws
            IOException {
        if (!Objects.requireNonNull(destination.getParentFile()).exists()) {
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

    private void deleteSelectedFile(String imageName) {
        if (!TextUtils.isEmpty(App.Companion.getInstance().getPref().getHiddenUri())) {
            Uri treeUri = Uri.parse(App.Companion.getInstance().getPref().getHiddenUri());
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
            switch (requestCode) {
                case 123:
                    if (SDK_INT > Build.VERSION_CODES.Q) {
                        if (!App.Companion.getInstance().getPref().getHiddenPermission()) {
                            permissionBtn.setText(R.string.hidden_folder_permission_btn);
                        }
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
                    break;
                case 12:
                    Uri rootUri1 = data.getData();
                    ContentResolver contentResolver1 = this.getContentResolver();
                    contentResolver1.takePersistableUriPermission(rootUri1, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    String rootDocumentId1 = DocumentsContract.getTreeDocumentId(rootUri1);
                    App.Companion.getInstance().getPref().setHiddenUri(rootUri1.toString());
                    App.Companion.getInstance().getPref().setHiddenPermission(true);
                    permissionText.setVisibility(View.GONE);
                    permissionBtn.setVisibility(View.GONE);
                    break;
                case 1001:
                    for (int i = 0; i < duplicate_selected_intruder_data.size(); i++) {
                        Delet_Data delet_data = new Delet_Data();
                        Calendar calendar = Calendar.getInstance();
                        delet_data.setName(duplicate_selected_intruder_data.get(i).getName());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        delet_data.setDate(dateFormat.format(calendar.getTime()));
                        database.addDelete(delet_data);
                        intruder_data.remove(duplicate_selected_intruder_data.get(i));
                    }
                    break;
                case 121:
                    Uri rootUri2 = data.getData();
                    App.Companion.getInstance().getPref().setRecycleUri(rootUri2.toString());
                    App.Companion.getInstance().getPref().setRecyclePermission(true);
                    try {
                        moveToRecycleAboveQ(recycle_source,
                                new File(recycle_destination + File.separator + recycle_name),
                                Uri.parse(recycle_source.getAbsolutePath()),
                                recycle_name);
                        deleteSelectedFile(recycle_name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void initView() {
        intruder_recycler = findViewById(R.id.intruder_recycler);
        no_intruder = findViewById(R.id.no_intruder);
        floating_btn_intruder = findViewById(R.id.floating_btn_intruder);
        permissionBtn = findViewById(R.id.permissionBtn);
        permissionText = findViewById(R.id.permissionText);
        database = new Database(Intruder_Activity.this);
        getImages();
    }

    private void getImages() {
        intruder_data.clear();
        if (SDK_INT > Build.VERSION_CODES.Q) {
            if (!TextUtils.isEmpty(App.Companion.getInstance().getPref().getHiddenUri())) {
                Uri treeUri = Uri.parse(App.Companion.getInstance().getPref().getHiddenUri());
                if (treeUri != null) {
                    this.getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    DocumentFile tree = DocumentFile.fromTreeUri(this, treeUri);
                    if (tree != null && tree.isDirectory()) {
                        for (DocumentFile documentFile : tree.listFiles()) {
                            if (documentFile.isDirectory() && documentFile.getName().equals(Constant.Intruder_Folder)) {
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
//                                    if (Objects.requireNonNull(file.getName()).contains("Intruder_")) {
                                    intruder_data.add(media_data);
//                                    }
                                }
                            }
                        }
                    }
                    Collections.reverse(intruder_data);
                }
                initAdapter();
            }
        } else {
            File folder = new File(Constant.intruder_path);
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
                    intruder_data.add(media_data);
                }
                Collections.reverse(intruder_data);
            }
            initAdapter();
        }
    }

    private void initAdapter() {
        if (intruder_data.size() > 0) {
            intruder_recycler.setVisibility(View.VISIBLE);
            no_intruder.setVisibility(View.GONE);
            intruder_adapter = new Intruder_Adapter(Intruder_Activity.this, intruder_data);
            intruder_recycler.setLayoutManager(new GridLayoutManager(Intruder_Activity.this, 3, RecyclerView.VERTICAL, false));
            intruder_recycler.setAdapter(intruder_adapter);
        } else {
            intruder_recycler.setVisibility(View.GONE);
            no_intruder.setVisibility(View.VISIBLE);
        }

    }

    private void initDialog() {
        intruder_dialog = new Dialog(Intruder_Activity.this, R.style.WideDialog);
        intruder_dialog.setContentView(R.layout.dialog_intruder);
        intruder_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ImageView close = intruder_dialog.findViewById(R.id.close);
        d_inruder_switch = intruder_dialog.findViewById(R.id.d_inruder_switch);
        close.setOnClickListener(v -> {
            intruder_dialog.dismiss();
            finish();
        });
        d_inruder_switch.setOnCheckedChangeListener((view, isChecked) -> {
            camera_per = ContextCompat.checkSelfPermission(Intruder_Activity.this, Manifest.permission.CAMERA);
            if (camera_per == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(Intruder_Activity.this)) {
                    askPermission();
                }
                if (isChecked) {
                    App.Companion.getInstance().getPref().setGetIntruder(true);
                    intruder_dialog.dismiss();
                } else {
                    App.Companion.getInstance().getPref().setGetIntruder(false);
                }
            } else {
                ActivityCompat.requestPermissions(Intruder_Activity.this, new String[]{Manifest.permission.CAMERA}, 110);
            }
        });
        intruder_dialog.show();
        intruder_dialog.setCancelable(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.select:
                if (intruder_adapter != null) {
                    if (!IsSelectAll) {
                        IsSelectAll = true;
                        Intruder_Adapter.IsLongClick = true;
                        floating_btn_intruder.setVisibility(View.VISIBLE);
                        item.setIcon(getResources().getDrawable(R.drawable.iv_select));
                        selected_intruder_data.clear();
                        selected_intruder_data.addAll(intruder_data);
                    } else {
                        IsSelectAll = false;
                        Intruder_Adapter.IsLongClick = false;
                        floating_btn_intruder.setVisibility(View.GONE);
                        item.setIcon(getResources().getDrawable(R.drawable.iv_unselect));
                        selected_intruder_data.clear();
                        selecter.setVisible(false);
                    }
                    intruder_adapter.notifyDataSetChanged();
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

    private void deleteFile(File source, File destination) throws IOException {
        if (!destination.getParentFile().exists())
            destination.getParentFile().mkdirs();

        FileChannel source_channel = null;
        FileChannel destination_channel = null;
        Hide_Data hide_data = new Hide_Data();
        hide_data.setName(source.getName());
        hide_data.setPath(source.getPath());
        database.addHide(hide_data);
        try {
            Boolean isRename = source.renameTo(destination);
            if (isRename) {
                getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "='" + source.getPath() + "'", null);
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
        Intruder_Adapter.IsLongClick = false;
        selected_intruder_data.clear();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 110: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    d_inruder_switch.setChecked(true);
                    App.Companion.getInstance().getPref().setGetIntruder(true);
                    if (SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                        askPermission();
                    }
                    intruder_dialog.dismiss();
                } else {
                    d_inruder_switch.setChecked(false);
                    App.Companion.getInstance().getPref().setGetIntruder(false);
                    Toast.makeText(Intruder_Activity.this, "Please allow this permission...", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getImages();
    }

    @Override
    public void onBackPressed() {
        if (selected_intruder_data.size() != 0) {
            IsSelectAll = false;
            Intruder_Adapter.IsLongClick = false;
            floating_btn_intruder.setVisibility(View.GONE);
            selecter.setIcon(getResources().getDrawable(R.drawable.iv_unselect));
            selected_intruder_data.clear();
            selecter.setVisible(false);
            intruder_adapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }
}
