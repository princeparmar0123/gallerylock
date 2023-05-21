package com.calculator.vault.lock.hide.photo.video.common.data.network.model;

import java.io.Serializable;

public class Card_Data implements Serializable {
    String id;
    String card_bname;
    String card_type;
    String card_number;
    String card_holder;
    String card_expire;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCard_bname() {
        return card_bname;
    }

    public void setCard_bname(String card_bname) {
        this.card_bname = card_bname;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getCard_holder() {
        return card_holder;
    }

    public void setCard_holder(String card_holder) {
        this.card_holder = card_holder;
    }

    public String getCard_expire() {
        return card_expire;
    }

    public void setCard_expire(String card_expire) {
        this.card_expire = card_expire;
    }

    public String getCard_pin() {
        return card_pin;
    }

    public void setCard_pin(String card_pin) {
        this.card_pin = card_pin;
    }

    public String getCard_cvv() {
        return card_cvv;
    }

    public void setCard_cvv(String card_cvv) {
        this.card_cvv = card_cvv;
    }

    String card_pin;
    String card_cvv;
}
