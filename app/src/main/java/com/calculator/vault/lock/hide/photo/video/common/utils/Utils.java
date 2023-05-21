package com.calculator.vault.lock.hide.photo.video.common.utils;


import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Utils {

    public static boolean isImage(String type) {
        if (type != null
                && (type.equals("jpg") || type.equals("gif")
                || type.equals("png") || type.equals("jpeg")
                || type.equals("bmp") || type.equals("wbmp")
                || type.equals("ico") || type.equals("jpe"))) {
            return true;
        }
        return false;
    }

    public static boolean isVideo(String type) {
        if (type != null
                && (type.equals("mp4") || type.equals("mov")
                || type.equals("wmv") || type.equals("avi")
                || type.equals("avchd") || type.equals("mkv")
                || type.equals("webm") || type.equals("flv"))) {
            return true;
        }
        return false;
    }

    public static String getMimeType(String name) {

        String extension = getExtension(name);

        if (extension.length() > 0)
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        return "application/octet-stream";
    }

    public static String getExtension(String path) {
        String extension = "";
        if (path != null) {
            int i = path.lastIndexOf('.');
            if (i > 0) {
                extension = path.substring(i + 1);
            }
        }
        return extension;
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

    public static void moveToRecycleAboveQ(Context context,File source, File destination, Uri muri, String newName) throws
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
                moveFile2(context,source, destination);
                source.delete();
            }
            MediaScannerConnection.scanFile(context, new String[]{source.getPath()}, null, (path, uri) -> Log.i("ExternalStorage", "Scanned " + path + ":" + uri));
            MediaScannerConnection.scanFile(context, new String[]{destination.getPath()}, null, (path, uri) -> Log.i("ExternalStorage", "Scanned " + path + ":" + uri));
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

    private static void moveFile2(Context context,File source, File destination) throws IOException {
        if (!destination.getParentFile().exists())
            destination.getParentFile().mkdirs();
        FileChannel source_channel = null;
        FileChannel destination_channel = null;
        try {
            Boolean isRename = source.renameTo(destination);
            if (isRename) {
                if (!source.getPath().contains("'")) {
                    context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "='" + source.getPath() + "'", null);
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
            MediaScannerConnection.scanFile(context, new String[]{source.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":" + uri);
                }
            });
            MediaScannerConnection.scanFile(context, new String[]{destination.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
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

}
