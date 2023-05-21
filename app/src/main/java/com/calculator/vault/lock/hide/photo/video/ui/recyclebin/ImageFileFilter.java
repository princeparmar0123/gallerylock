package com.calculator.vault.lock.hide.photo.video.ui.recyclebin;

import java.io.File;
import java.io.FileFilter;

public class ImageFileFilter implements FileFilter {

    private final String[] okFileExtensions = new String[] {
            "jpg",
            "png",
            "gif",
            "jpeg"
    };


    public boolean accept(File file) {
        for (String extension: okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

}
