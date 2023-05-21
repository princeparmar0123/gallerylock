package com.calculator.vault.lock.hide.photo.video.common.data.network.model;

import java.io.Serializable;

public class Bank_Data implements Serializable {
    String id;
    String bank_name;
    String account_nummber;
    String holder_name;
    String account_type;
    String ifsc_code;
    String swift_code;
    String email;
    String user_id;
    String pass;
    String trapass;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getAccount_nummber() {
        return account_nummber;
    }

    public void setAccount_nummber(String account_nummber) {
        this.account_nummber = account_nummber;
    }

    public String getHolder_name() {
        return holder_name;
    }

    public void setHolder_name(String holder_name) {
        this.holder_name = holder_name;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public String getIfsc_code() {
        return ifsc_code;
    }

    public void setIfsc_code(String ifsc_code) {
        this.ifsc_code = ifsc_code;
    }

    public String getSwift_code() {
        return swift_code;
    }

    public void setSwift_code(String swift_code) {
        this.swift_code = swift_code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getTrapass() {
        return trapass;
    }

    public void setTrapass(String trapass) {
        this.trapass = trapass;
    }
}
