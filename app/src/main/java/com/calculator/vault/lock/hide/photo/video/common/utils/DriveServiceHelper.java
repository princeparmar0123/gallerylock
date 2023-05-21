package com.calculator.vault.lock.hide.photo.video.common.utils;

import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;


import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.calculator.vault.lock.hide.photo.video.App;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.GoogleDriveFileHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import timber.log.Timber;

public class DriveServiceHelper {
    private final Executor executor = Executors.newSingleThreadExecutor();
    private Drive drive;

    public DriveServiceHelper(Drive driveService) {
        drive = driveService;
    }

    public Task<String> createFolder(String name) {
        return Tasks.call(executor, () -> {
            File fileMetadata = new File();
            fileMetadata.setName(name);
            fileMetadata.setMimeType("application/vnd.google-apps.folder");
            File file = null;
            try {
                file = drive.files().create(fileMetadata)
                        .setFields("id")
                        .execute();
                System.out.println("Folder ID: " + file.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file.getId();
        });
    }

    public Task<String> createSubFolder(String name, String folderId) {
        return Tasks.call(executor, () -> {
            File fileMetadata = new File();
            fileMetadata.setName(name);
            fileMetadata.setParents(Collections.singletonList(folderId));
            fileMetadata.setMimeType("application/vnd.google-apps.folder");
            File file = null;
            try {
                file = drive.files().create(fileMetadata)
                        .setFields("id")
                        .execute();
                System.out.println("Folder ID: " + file.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file.getId();
        });
    }

    public Task<String> insertImageFile(String name, String path, String folderId) {
        return Tasks.call(executor, () -> {
            File fileMetadata = new File();
            fileMetadata.setName(name);
            fileMetadata.setParents(Collections.singletonList(folderId));
            java.io.File filePath = new java.io.File(path);
            FileContent mediaContent = new FileContent("image/*", filePath);
            File file = null;
            try {
                file = drive.files().create(fileMetadata, mediaContent)
                        .setFields("id, parents")
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("File ID: " + file.getId());
            return file.getId();
        });
    }

    public Task<String> insertVideoFile(String name, String path, String folderId) {
        return Tasks.call(executor, () -> {
            File fileMetadata = new File();
            fileMetadata.setName(name);
            fileMetadata.setParents(Collections.singletonList(folderId));
            java.io.File filePath = new java.io.File(path);
            FileContent mediaContent = new FileContent("video/*", filePath);
            File file = null;
            try {
                file = drive.files().create(fileMetadata, mediaContent)
                        .setFields("id, parents")
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("File ID: " + file.getId());
            return file.getId();
        });
    }

    public Task<Boolean> checkFolder(String name) {
        return Tasks.call(executor, () -> {
            String pageToken = null;
            do {
                FileList result = null;
                try {
                    result = drive.files().list()
                            .setQ("mimeType='application/vnd.google-apps.folder'")
                            .setSpaces("drive")
                            .setFields("nextPageToken, files(id, name)")
                            .setPageToken(pageToken)
                            .execute();
                    for (File file : result.getFiles()) {
                        if (file.getName().equals(name)) {
                            App.Companion.getInstance().getPref().setFolderId(file.getId());
                            //Prefrancemanager.putFolderId(file.getId());
                            return true;
                        }
                    }
                    pageToken = result.getNextPageToken();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (pageToken != null);
            return false;
        });
    }

    public Task<Boolean> checkSubFolder(String id) {
        return Tasks.call(executor, () -> {
            String pageToken = null;
            do {
                FileList result = null;
                try {
                    result = drive.files().list()
                            .setQ("mimeType='application/vnd.google-apps.folder'")
                            .setSpaces("drive")
                            .setFields("nextPageToken, files(id, name)")
                            .setPageToken(pageToken)
                            .execute();
                    for (File file : result.getFiles()) {
                        if (file.getName().equals(id)) {
                            if (id.equals("Image")) {
                                App.Companion.getInstance().getPref().setImageFolder(file.getId());
                               // Prefrancemanager.putImageFolder(file.getId());
                            } else if (id.equals("Video")) {
                                App.Companion.getInstance().getPref().setVideoFolder(file.getId());
                               // Prefrancemanager.putVideoFolder(file.getId());
                            }
                            return true;
                        }
                    }
                    pageToken = result.getNextPageToken();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (pageToken != null);
            return false;
        });
    }

    public Task<Boolean> checkImage(String name) {
        return Tasks.call(executor, () -> {
            String pageToken = null;
            do {
                FileList result = null;
                try {
                    result = drive.files().list()
                            .setSpaces("drive")
                            .setFields("nextPageToken, files(id, name)")
                            .setPageToken(pageToken)
                            .execute();
                    for (File file : result.getFiles()) {
                        if (file.getName().equals(name)) {
                            return true;
                        }
                    }
                    pageToken = result.getNextPageToken();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (pageToken != null);
            return false;
        });
    }

    public Task<Boolean> checkVideo(String name) {
        return Tasks.call(executor, () -> {
            String pageToken = null;
            do {
                FileList result = null;
                try {
                    result = drive.files().list()
                            .setQ("mimeType='video/*'")
                            .setSpaces("drive")
                            .setFields("nextPageToken, files(id, name)")
                            .setPageToken(pageToken)
                            .execute();
                    for (File file : result.getFiles()) {
                        if (file.getName().equals(name)) {
                            return true;
                        }
                    }
                    pageToken = result.getNextPageToken();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (pageToken != null);
            return false;
        });
    }

    public Task<List<GoogleDriveFileHolder>> queryFiles(@Nullable final String folderId) {
        return Tasks.call(executor, new Callable<List<GoogleDriveFileHolder>>() {
                    @Override
                    public List<GoogleDriveFileHolder> call() throws Exception {
                        List<GoogleDriveFileHolder> googleDriveFileHolderList = new ArrayList<>();
                        String parent = "root";
                        if (folderId != null) {
                            parent = folderId;
                        }
                        FileList result = drive.files().list().setQ("'" + parent + "' in parents").setFields("files(id, name,size,createdTime,modifiedTime,starred,mimeType)").setSpaces("drive").execute();
                        for (int i = 0; i < result.getFiles().size(); i++) {
                            GoogleDriveFileHolder googleDriveFileHolder = new GoogleDriveFileHolder();
                            googleDriveFileHolder.setId(result.getFiles().get(i).getId());
                            googleDriveFileHolder.setName(result.getFiles().get(i).getName());
                            if (result.getFiles().get(i).getSize() != null) {
                                googleDriveFileHolder.setSize(result.getFiles().get(i).getSize());
                            }
                            if (result.getFiles().get(i).getModifiedTime() != null) {
                                googleDriveFileHolder.setModifiedTime(result.getFiles().get(i).getModifiedTime());
                            }
                            if (result.getFiles().get(i).getCreatedTime() != null) {
                                googleDriveFileHolder.setCreatedTime(result.getFiles().get(i).getCreatedTime());
                            }
                            if (result.getFiles().get(i).getStarred() != null) {
                                googleDriveFileHolder.setStarred(result.getFiles().get(i).getStarred());
                            }
                            if (result.getFiles().get(i).getMimeType() != null) {
                                googleDriveFileHolder.setMimeType(result.getFiles().get(i).getMimeType());
                            }
                            googleDriveFileHolderList.add(googleDriveFileHolder);
                        }
                        return googleDriveFileHolderList;
                    }
                }
        );
    }

    public Task<Pair<String, String>> readFile(final String fileId) {
        return Tasks.call(executor, new Callable<Pair<String, String>>() {
            @Override
            public Pair<String, String> call() throws Exception {
                File metadata = drive.files().get(fileId).execute();
                String name = metadata.getName();
                try (InputStream is = drive.files().get(fileId).executeMediaAsInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    String contents = stringBuilder.toString();
                    return Pair.create(name, contents);
                }
            }
        });
    }

    public Task<Void> deleteFile(final String fileId) {
        return Tasks.call(executor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Void file = drive.files().delete(fileId).execute();
                return file;
            }
        });
    }
}

