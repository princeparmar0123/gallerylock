package com.calculator.vault.lock.hide.photo.video.common.data.network.model;

import java.io.Serializable;

public class Media_Data implements Serializable {
    String name;
    String path;
    String folder;
    String length;
    String addeddate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getAddeddate() {
        return addeddate;
    }

    public void setAddeddate(String addeddate) {
        this.addeddate = addeddate;
    }

    public String getModifieddate() {
        return modifieddate;
    }

    public void setModifieddate(String modifieddate) {
        this.modifieddate = modifieddate;
    }

    String modifieddate;
}
