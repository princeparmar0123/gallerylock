package com.calculator.vault.lock.hide.photo.video.common.data.network.model;

import java.io.Serializable;

public class Contact_Data implements Serializable {
    String id, name, number;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
