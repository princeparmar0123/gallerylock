package com.calculator.vault.lock.hide.photo.video.common.data.network.model;

import java.io.Serializable;

public class Social_Data implements Serializable {
    String id, social_type, social_name, social_email, social_pass;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSocial_type() {
        return social_type;
    }

    public void setSocial_type(String social_type) {
        this.social_type = social_type;
    }

    public String getSocial_name() {
        return social_name;
    }

    public void setSocial_name(String social_name) {
        this.social_name = social_name;
    }

    public String getSocial_email() {
        return social_email;
    }

    public void setSocial_email(String social_email) {
        this.social_email = social_email;
    }

    public String getSocial_pass() {
        return social_pass;
    }

    public void setSocial_pass(String social_pass) {
        this.social_pass = social_pass;
    }
}
