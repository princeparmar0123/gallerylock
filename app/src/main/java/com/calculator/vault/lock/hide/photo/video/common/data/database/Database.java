package com.calculator.vault.lock.hide.photo.video.common.data.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Bank_Data;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Bookmark_Data;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Card_Data;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Contact_Data;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Delet_Data;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Email_Data;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Hide_Data;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.History_Data;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Note_Data;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Other_Data;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Social_Data;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "note_contact.db";
    private static final String TABLE_NOTE = "Note";
    private static final String COLUMN_NOTE_ID = "note_id";
    private static final String COLUMN_NOTE_TITLE = "note_title";
    private static final String COLUMN_NOTE_DATA = "note_data";
    private static final String COLUMN_NOTE_DATE = "note_date";

    private static final String TABLE_CONTACT = "Contact";
    private static final String COLUMN_CONTACT_ID = "contact_id";
    private static final String COLUMN_CONTACT_NAME = "contact_name";
    private static final String COLUMN_CONTACT_NUMBER = "contact_number";

    private static final String TABLE_BANK = "Bank";
    private static final String COLUMN_BANK_ID = "bank_id";
    private static final String COLUMN_BANK_NAME = "bank_name";
    private static final String COLUMN_BANK_NUMBER = "bank_number";
    private static final String COLUMN_BANK_HOLDER = "bank_holder";
    private static final String COLUMN_BANK_TYPE = "bank_type";
    private static final String COLUMN_BANK_IFSC = "bank_ifsc";
    private static final String COLUMN_BANK_SWIFT = "bank_swift";
    private static final String COLUMN_BANK_EMAIL = "bank_email";
    private static final String COLUMN_BANK_USERID = "bank_userid";
    private static final String COLUMN_BANK_PASS = "bank_pass";
    private static final String COLUMN_BANK_TRAPASS = "bank_trapass";
    private static final String COLUMN_BANK_URL = "bank_url";

    private static final String TABLE_CARD = "Card";
    private static final String COLUMN_CARD_ID = "card_id";
    private static final String COLUMN_CARD_BNAME = "card_name";
    private static final String COLUMN_CARD_TYPE = "card_type";
    private static final String COLUMN_CARD_NUMBER = "card_number";
    private static final String COLUMN_CARD_HOLDER = "card_holder";
    private static final String COLUMN_CARD_EDATE = "card_edate";
    private static final String COLUMN_CARD_PIN = "card_pin";
    private static final String COLUMN_CARD_CVV = "card_cvv";

    private static final String TABLE_EMAIL = "Email";
    private static final String COLUMN_EMAIL_ID = "email_id";
    private static final String COLUMN_EMAIL_NAME = "email_name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_EMAIL_PASS = "email_pass";

    private static final String TABLE_SOCIAL = "Social";
    private static final String COLUMN_SOCIAL_ID = "social_id";
    private static final String COLUMN_SOCIAL_TYPE = "social_type";
    private static final String COLUMN_SOCIAL_NAME = "social_name";
    private static final String COLUMN_SOCIAL_USER = "social_user";
    private static final String COLUMN_SOCIAL_PASS = "social_pass";

    private static final String TABLE_OTHER = "Other";
    private static final String COLUMN_OTHER_ID = "other_id";
    private static final String COLUMN_OTHER_TITLE = "other_title";
    private static final String COLUMN_OTHER1 = "other1";
    private static final String COLUMN_OTHER2 = "other2";
    private static final String COLUMN_OTHER3 = "other3";
    private static final String COLUMN_OTHER4 = "other4";
    private static final String COLUMN_OTHER5 = "other5";
    private static final String COLUMN_OTHER6 = "other6";
    private static final String COLUMN_OTHER7 = "other7";

    private static final String TABLE_HIDE = "Hide";
    private static final String COLUMN_HIDE_NAME = "hide_name";
    private static final String COLUMN_HIDE_PATH = "hide_path";

    private static final String TABLE_DELETE = "Delete_Data";
    private static final String COLUMN_DELETE_NAME = "delete_name";
    private static final String COLUMN_DELETE_DATE = "delete_date";

    private static final String TABLE_BOOKMARK = "Bookmark";
    private static final String COLUMN_BOOKMARK_ID = "bookmark_id";
    private static final String COLUMN_BOOKMARK_NAME = "bookmark_name";
    private static final String COLUMN_BOOKMARK_URL = "bookmark_url";
    private static final String COLUMN_BOOKMARK_IMAGE = "bookmark_image";

    private static final String TABLE_HISTORY = "History";
    private static final String COLUMN_HISTORY_ID = "history_id";
    private static final String COLUMN_HISTORY_NAME = "history_name";
    private static final String COLUMN_HISTORY_URL = "history_url";
    private static final String COLUMN_HISTORY_IMAGE = "history_image";
    private static final String COLUMN_HISTORY_DATE = "history_date";

    private String CREATE_NOTE_TABLE = "CREATE TABLE " + TABLE_NOTE + " ( " + COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NOTE_TITLE + " TEXT, " + COLUMN_NOTE_DATA + " TEXT, " + COLUMN_NOTE_DATE + " TEXT " + " ) ";
    private String CREATE_CONTACT_TABLE = "CREATE TABLE " + TABLE_CONTACT + " ( " + COLUMN_CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CONTACT_NAME + " TEXT, " + COLUMN_CONTACT_NUMBER + " TEXT " + " ) ";
    private String CREATE_BANK_TABLE = "CREATE TABLE " + TABLE_BANK + " ( " + COLUMN_BANK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_BANK_NAME + " TEXT, " + COLUMN_BANK_NUMBER + " TEXT, " + COLUMN_BANK_HOLDER + " TEXT, " + COLUMN_BANK_TYPE + " TEXT, " + COLUMN_BANK_IFSC + " TEXT, " + COLUMN_BANK_SWIFT + " TEXT, " + COLUMN_BANK_EMAIL + " TEXT, " + COLUMN_BANK_USERID + " TEXT, " + COLUMN_BANK_PASS + " TEXT, " + COLUMN_BANK_TRAPASS + " TEXT, " + COLUMN_BANK_URL + " TEXT " + " ) ";
    private String CREATE_CARD_TABLE = "CREATE TABLE " + TABLE_CARD + " ( " + COLUMN_CARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CARD_BNAME + " TEXT, " + COLUMN_CARD_TYPE + " TEXT, " + COLUMN_CARD_NUMBER + " TEXT, " + COLUMN_CARD_HOLDER + " TEXT, " + COLUMN_CARD_EDATE + " TEXT, " + COLUMN_CARD_PIN + " TEXT, " + COLUMN_CARD_CVV + " TEXT " + " ) ";
    private String CREATE_EMAIL_TABLE = "CREATE TABLE " + TABLE_EMAIL + " ( " + COLUMN_EMAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_EMAIL_NAME + " TEXT, " + COLUMN_EMAIL + " TEXT, " + COLUMN_EMAIL_PASS + " TEXT " + " ) ";
    private String CREATE_SOCIAL_TABLE = "CREATE TABLE " + TABLE_SOCIAL + " ( " + COLUMN_SOCIAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_SOCIAL_TYPE + " TEXT, " + COLUMN_SOCIAL_NAME + " TEXT, " + COLUMN_SOCIAL_USER + " TEXT, " + COLUMN_SOCIAL_PASS + " TEXT " + " ) ";
    private String CREATE_OTHER_TABLE = "CREATE TABLE " + TABLE_OTHER + " ( " + COLUMN_OTHER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_OTHER_TITLE + " TEXT, " + COLUMN_OTHER1 + " TEXT, " + COLUMN_OTHER2 + " TEXT, " + COLUMN_OTHER3 + " TEXT, " + COLUMN_OTHER4 + " TEXT, " + COLUMN_OTHER5 + " TEXT, " + COLUMN_OTHER6 + " TEXT, " + COLUMN_OTHER7 + " TEXT " + " ) ";
    private String CREATE_HIDE_TABLE = "CREATE TABLE " + TABLE_HIDE + " ( " + COLUMN_HIDE_NAME + " TEXT PRIMARY KEY, " + COLUMN_HIDE_PATH + " TEXT " + " ) ";
    private String CREATE_DELETE_TABLE = "CREATE TABLE " + TABLE_DELETE + " ( " + COLUMN_DELETE_NAME + " TEXT PRIMARY KEY, " + COLUMN_DELETE_DATE + " TEXT " + " ) ";
    private String CREATE_BOOKMARK_TABLE = "CREATE TABLE " + TABLE_BOOKMARK + " ( " + COLUMN_BOOKMARK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_BOOKMARK_NAME + " TEXT, " + COLUMN_BOOKMARK_URL + " TEXT, " + COLUMN_BOOKMARK_IMAGE + " TEXT " + " ) ";
    private String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_HISTORY + " ( " + COLUMN_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_HISTORY_NAME + " TEXT, " + COLUMN_HISTORY_URL + " TEXT, " + COLUMN_HISTORY_IMAGE + " TEXT, " + COLUMN_HISTORY_DATE + " TEXT " + " ) ";

    private String DROP_NOTE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NOTE;
    private String DROP_CONTACT_TABLE = "DROP TABLE IF EXISTS " + TABLE_CONTACT;
    private String DROP_BANK_TABLE = "DROP TABLE IF EXISTS " + TABLE_BANK;
    private String DROP_CARD_TABLE = "DROP TABLE IF EXISTS " + TABLE_CARD;
    private String DROP_EMAIL_TABLE = "DROP TABLE IF EXISTS " + TABLE_EMAIL;
    private String DROP_SOCIAL_TABLE = "DROP TABLE IF EXISTS " + TABLE_SOCIAL;
    private String DROP_OTHER_TABLE = "DROP TABLE IF EXISTS " + TABLE_OTHER;
    private String DROP_HIDE_TABLE = "DROP TABLE IF EXISTS " + TABLE_HIDE;
    private String DROP_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_DELETE;
    private String DROP_BOOKMARK_TABLE = "DROP TABLE IF EXISTS " + TABLE_BOOKMARK;
    private String DROP_HISTORY_TABLE = "DROP TABLE IF EXISTS " + TABLE_HISTORY;


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTE_TABLE);
        db.execSQL(CREATE_CONTACT_TABLE);
        db.execSQL(CREATE_BANK_TABLE);
        db.execSQL(CREATE_CARD_TABLE);
        db.execSQL(CREATE_EMAIL_TABLE);
        db.execSQL(CREATE_SOCIAL_TABLE);
        db.execSQL(CREATE_OTHER_TABLE);
        db.execSQL(CREATE_HIDE_TABLE);
        db.execSQL(CREATE_DELETE_TABLE);
        db.execSQL(CREATE_BOOKMARK_TABLE);
        db.execSQL(CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_NOTE_TABLE);
        db.execSQL(DROP_CONTACT_TABLE);
        db.execSQL(DROP_BANK_TABLE);
        db.execSQL(DROP_CARD_TABLE);
        db.execSQL(DROP_EMAIL_TABLE);
        db.execSQL(DROP_SOCIAL_TABLE);
        db.execSQL(DROP_OTHER_TABLE);
        db.execSQL(DROP_HIDE_TABLE);
        db.execSQL(DROP_DELETE_TABLE);
        db.execSQL(DROP_BOOKMARK_TABLE);
        db.execSQL(DROP_HISTORY_TABLE);
        onCreate(db);
    }

    public void addNote(Note_Data note_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note_data.getTitle());
        values.put(COLUMN_NOTE_DATA, note_data.getNote());
        values.put(COLUMN_NOTE_DATE, note_data.getDate());
        db.insert(TABLE_NOTE, null, values);
        db.close();
    }

    public void addContact(Contact_Data contact_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACT_NAME, contact_data.getName());
        values.put(COLUMN_CONTACT_NUMBER, contact_data.getNumber());
        db.insert(TABLE_CONTACT, null, values);
        db.close();
    }

    public void addBank(Bank_Data bank_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BANK_NAME, bank_data.getBank_name());
        values.put(COLUMN_BANK_NUMBER, bank_data.getAccount_nummber());
        values.put(COLUMN_BANK_HOLDER, bank_data.getHolder_name());
        values.put(COLUMN_BANK_TYPE, bank_data.getAccount_type());
        values.put(COLUMN_BANK_IFSC, bank_data.getIfsc_code());
        values.put(COLUMN_BANK_SWIFT, bank_data.getSwift_code());
        values.put(COLUMN_BANK_EMAIL, bank_data.getEmail());
        values.put(COLUMN_BANK_USERID, bank_data.getUser_id());
        values.put(COLUMN_BANK_PASS, bank_data.getPass());
        values.put(COLUMN_BANK_TRAPASS, bank_data.getTrapass());
        values.put(COLUMN_BANK_URL, bank_data.getUrl());
        db.insert(TABLE_BANK, null, values);
        db.close();
    }

    public void addCard(Card_Data card_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CARD_BNAME, card_data.getCard_bname());
        values.put(COLUMN_CARD_TYPE, card_data.getCard_type());
        values.put(COLUMN_CARD_NUMBER, card_data.getCard_number());
        values.put(COLUMN_CARD_HOLDER, card_data.getCard_holder());
        values.put(COLUMN_CARD_EDATE, card_data.getCard_expire());
        values.put(COLUMN_CARD_PIN, card_data.getCard_pin());
        values.put(COLUMN_CARD_CVV, card_data.getCard_cvv());
        db.insert(TABLE_CARD, null, values);
        db.close();
    }


    public void addEmail(Email_Data email_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL_NAME, email_data.getName());
        values.put(COLUMN_EMAIL, email_data.getEmail());
        values.put(COLUMN_EMAIL_PASS, email_data.getPassword());
        db.insert(TABLE_EMAIL, null, values);
        db.close();
    }

    public void addBookmark(Bookmark_Data bookmark_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BOOKMARK_NAME, bookmark_data.getName());
        values.put(COLUMN_BOOKMARK_URL, bookmark_data.getUrl());
        values.put(COLUMN_BOOKMARK_IMAGE, bookmark_data.getImage());
        db.insert(TABLE_BOOKMARK, null, values);
        db.close();
    }

    public void addSocial(Social_Data social_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SOCIAL_TYPE, social_data.getSocial_type());
        values.put(COLUMN_SOCIAL_NAME, social_data.getSocial_name());
        values.put(COLUMN_SOCIAL_USER, social_data.getSocial_email());
        values.put(COLUMN_SOCIAL_PASS, social_data.getSocial_pass());
        db.insert(TABLE_SOCIAL, null, values);
        db.close();
    }

    public void addHistory(History_Data history_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HISTORY_NAME, history_data.getName());
        values.put(COLUMN_HISTORY_URL, history_data.getUrl());
        values.put(COLUMN_HISTORY_IMAGE, history_data.getImage());
        values.put(COLUMN_HISTORY_DATE, history_data.getDate());
        db.insert(TABLE_HISTORY, null, values);
        db.close();
    }

    public void addHide(Hide_Data hide_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String temp_s = hide_data.getName().replace("'", "@#");
        String temp_p = hide_data.getPath().replace("'", "@#");
        values.put(COLUMN_HIDE_NAME, temp_s);
        values.put(COLUMN_HIDE_PATH, temp_p);
        db.insert(TABLE_HIDE, null, values);
        db.close();
    }

    public int getID() {
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "SELECT * FROM " + TABLE_HIDE;
        Cursor cursor = db.rawQuery(q, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getCount();
            cursor.close();
            return count;
        }
        return 0;

    }

    public void addDelete(Delet_Data delet_data) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            String temp_s = delet_data.getName().replace("'", "@#");
            values.put(COLUMN_DELETE_NAME, temp_s);
            values.put(COLUMN_DELETE_DATE, delet_data.getDate());
            db.insert(TABLE_DELETE, null, values);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addOther(Other_Data other_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_OTHER_TITLE, other_data.getOther_title());
        values.put(COLUMN_OTHER1, other_data.getOther1());
        values.put(COLUMN_OTHER2, other_data.getOther2());
        values.put(COLUMN_OTHER3, other_data.getOther3());
        values.put(COLUMN_OTHER4, other_data.getOther4());
        values.put(COLUMN_OTHER5, other_data.getOther5());
        values.put(COLUMN_OTHER6, other_data.getOther6());
        values.put(COLUMN_OTHER7, other_data.getOther7());
        db.insert(TABLE_OTHER, null, values);
        db.close();
    }

    @SuppressLint("Range")
    public List<Note_Data> getAllNote() {
        List<Note_Data> note_datas = new ArrayList<Note_Data>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NOTE, null);
        if (cursor.moveToFirst()) {
            do {
                Note_Data note_data = new Note_Data();
                note_data.setId(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_ID)));
                note_data.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE)));
                note_data.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_DATA)));
                note_data.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_DATE)));
                note_datas.add(note_data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return note_datas;
    }

    @SuppressLint("Range")
    public List<Contact_Data> getAllContact() {
        List<Contact_Data> contact_datas = new ArrayList<Contact_Data>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_CONTACT, null);
        if (cursor.moveToFirst()) {
            do {
                Contact_Data contact_data = new Contact_Data();
                contact_data.setId(cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_ID)));
                contact_data.setName(cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_NAME)));
                contact_data.setNumber(cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_NUMBER)));
                contact_datas.add(contact_data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contact_datas;
    }

    @SuppressLint("Range")
    public List<Bank_Data> getAllBank() {
        List<Bank_Data> bank_datas = new ArrayList<Bank_Data>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_BANK, null);
        if (cursor.moveToFirst()) {
            do {
                Bank_Data bank_data = new Bank_Data();
                bank_data.setId(cursor.getString(cursor.getColumnIndex(COLUMN_BANK_ID)));
                bank_data.setBank_name(cursor.getString(cursor.getColumnIndex(COLUMN_BANK_NAME)));
                bank_data.setAccount_nummber(cursor.getString(cursor.getColumnIndex(COLUMN_BANK_NUMBER)));
                bank_data.setHolder_name(cursor.getString(cursor.getColumnIndex(COLUMN_BANK_HOLDER)));
                bank_data.setAccount_type(cursor.getString(cursor.getColumnIndex(COLUMN_BANK_TYPE)));
                bank_data.setIfsc_code(cursor.getString(cursor.getColumnIndex(COLUMN_BANK_IFSC)));
                bank_data.setSwift_code(cursor.getString(cursor.getColumnIndex(COLUMN_BANK_SWIFT)));
                bank_data.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_BANK_EMAIL)));
                bank_data.setUser_id(cursor.getString(cursor.getColumnIndex(COLUMN_BANK_USERID)));
                bank_data.setPass(cursor.getString(cursor.getColumnIndex(COLUMN_BANK_PASS)));
                bank_data.setTrapass(cursor.getString(cursor.getColumnIndex(COLUMN_BANK_TRAPASS)));
                bank_data.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_BANK_URL)));
                bank_datas.add(bank_data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bank_datas;
    }

    @SuppressLint("Range")
    public List<Card_Data> getAllCard() {
        List<Card_Data> card_datas = new ArrayList<Card_Data>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_CARD, null);
        if (cursor.moveToFirst()) {
            do {
                Card_Data card_data = new Card_Data();
                card_data.setId(cursor.getString(cursor.getColumnIndex(COLUMN_CARD_ID)));
                card_data.setCard_bname(cursor.getString(cursor.getColumnIndex(COLUMN_CARD_BNAME)));
                card_data.setCard_type(cursor.getString(cursor.getColumnIndex(COLUMN_CARD_TYPE)));
                card_data.setCard_number(cursor.getString(cursor.getColumnIndex(COLUMN_CARD_NUMBER)));
                card_data.setCard_holder(cursor.getString(cursor.getColumnIndex(COLUMN_CARD_HOLDER)));
                card_data.setCard_expire(cursor.getString(cursor.getColumnIndex(COLUMN_CARD_EDATE)));
                card_data.setCard_pin(cursor.getString(cursor.getColumnIndex(COLUMN_CARD_PIN)));
                card_data.setCard_cvv(cursor.getString(cursor.getColumnIndex(COLUMN_CARD_CVV)));
                card_datas.add(card_data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return card_datas;
    }

    @SuppressLint("Range")
    public List<Email_Data> getAllEmail() {
        List<Email_Data> email_datas = new ArrayList<Email_Data>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_EMAIL, null);
        if (cursor.moveToFirst()) {
            do {
                Email_Data email_data = new Email_Data();
                email_data.setId(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL_ID)));
                email_data.setName(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL_NAME)));
                email_data.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
                email_data.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL_PASS)));
                email_datas.add(email_data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return email_datas;
    }

    @SuppressLint("Range")
    public List<Bookmark_Data> getAllBookmark() {
        List<Bookmark_Data> bookmark_datas = new ArrayList<Bookmark_Data>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_BOOKMARK, null);
        if (cursor.moveToFirst()) {
            do {
                Bookmark_Data bookmark_data = new Bookmark_Data();
                bookmark_data.setId(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKMARK_ID)));
                bookmark_data.setName(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKMARK_NAME)));
                bookmark_data.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKMARK_URL)));
                bookmark_data.setImage(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKMARK_IMAGE)));
                bookmark_datas.add(bookmark_data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bookmark_datas;
    }

    public List<Hide_Data> getAllHide() {
        List<Hide_Data> hide_datas = new ArrayList<Hide_Data>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_HIDE, null);
        if (cursor.moveToFirst()) {
            do {
                Hide_Data hide_data = new Hide_Data();
                @SuppressLint("Range") String temp_s = cursor.getString(cursor.getColumnIndex(COLUMN_HIDE_NAME)).replace("@#", "'");
                @SuppressLint("Range") String temp_p = cursor.getString(cursor.getColumnIndex(COLUMN_HIDE_PATH)).replace("@#", "'");
                hide_data.setName(temp_s);
                hide_data.setPath(temp_p);
                hide_datas.add(hide_data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return hide_datas;
    }

    public Hide_Data getHideData(String name) {
        Hide_Data hide_data = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_HIDE + " where " + COLUMN_HIDE_NAME + "='" + name + "'", null);
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String temp_s = cursor.getString(cursor.getColumnIndex(COLUMN_HIDE_NAME)).replace("@#", "'");
            @SuppressLint("Range") String temp_p = cursor.getString(cursor.getColumnIndex(COLUMN_HIDE_PATH)).replace("@#", "'");
            hide_data = new Hide_Data();
            hide_data.setName(temp_s);
            hide_data.setPath(temp_p);
        }
        cursor.close();
        db.close();
        return hide_data;
    }

    @SuppressLint("Range")
    public List<Delet_Data> getAllDelete() {
        List<Delet_Data> delet_datas = new ArrayList<Delet_Data>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_DELETE, null);
        if (cursor.moveToFirst()) {
            do {
                Delet_Data delet_data = new Delet_Data();
                @SuppressLint("Range") String temp_s = cursor.getString(cursor.getColumnIndex(COLUMN_DELETE_NAME)).replace("@#", "'");
                delet_data.setName(temp_s);
                delet_data.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DELETE_DATE)));
                delet_datas.add(delet_data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return delet_datas;
    }

    @SuppressLint("Range")
    public List<Social_Data> getAllSocial() {
        List<Social_Data> social_datas = new ArrayList<Social_Data>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_SOCIAL, null);
        if (cursor.moveToFirst()) {
            do {
                Social_Data social_data = new Social_Data();
                social_data.setId(cursor.getString(cursor.getColumnIndex(COLUMN_SOCIAL_ID)));
                social_data.setSocial_type(cursor.getString(cursor.getColumnIndex(COLUMN_SOCIAL_TYPE)));
                social_data.setSocial_name(cursor.getString(cursor.getColumnIndex(COLUMN_SOCIAL_NAME)));
                social_data.setSocial_email(cursor.getString(cursor.getColumnIndex(COLUMN_SOCIAL_USER)));
                social_data.setSocial_pass(cursor.getString(cursor.getColumnIndex(COLUMN_SOCIAL_PASS)));
                social_datas.add(social_data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return social_datas;
    }

    @SuppressLint("Range")
    public List<History_Data> getAllHistory() {
        List<History_Data> history_datas = new ArrayList<History_Data>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_HISTORY, null);
        if (cursor.moveToFirst()) {
            do {
                History_Data history_data = new History_Data();
                history_data.setId(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_ID)));
                history_data.setName(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_NAME)));
                history_data.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_URL)));
                history_data.setImage(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_IMAGE)));
                history_data.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DATE)));
                history_datas.add(history_data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return history_datas;
    }

    @SuppressLint("Range")
    public List<Other_Data> getAllOther() {
        List<Other_Data> other_datas = new ArrayList<Other_Data>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_OTHER, null);
        if (cursor.moveToFirst()) {
            do {
                Other_Data other_data = new Other_Data();
                other_data.setId(cursor.getString(cursor.getColumnIndex(COLUMN_OTHER_ID)));
                other_data.setOther_title(cursor.getString(cursor.getColumnIndex(COLUMN_OTHER_TITLE)));
                other_data.setOther1(cursor.getString(cursor.getColumnIndex(COLUMN_OTHER1)));
                other_data.setOther2(cursor.getString(cursor.getColumnIndex(COLUMN_OTHER2)));
                other_data.setOther3(cursor.getString(cursor.getColumnIndex(COLUMN_OTHER3)));
                other_data.setOther4(cursor.getString(cursor.getColumnIndex(COLUMN_OTHER4)));
                other_data.setOther5(cursor.getString(cursor.getColumnIndex(COLUMN_OTHER5)));
                other_data.setOther6(cursor.getString(cursor.getColumnIndex(COLUMN_OTHER6)));
                other_data.setOther7(cursor.getString(cursor.getColumnIndex(COLUMN_OTHER7)));
                other_datas.add(other_data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return other_datas;
    }

    public Integer deleteNote(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NOTE, "note_id = ?", new String[]{id});
    }

    public Integer deleteContact(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CONTACT, "contact_id = ?", new String[]{id});
    }

    public Integer deleteBank(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_BANK, "bank_id = ?", new String[]{id});
    }

    public Integer deleteCard(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CARD, "card_id = ?", new String[]{id});
    }

    public Integer deleteEmail(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EMAIL, "email_id = ?", new String[]{id});
    }

    public Integer deleteBookmark(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_BOOKMARK, "bookmark_id = ?", new String[]{id});
    }

    public Integer deleteHistory(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_HISTORY, "history_id = ?", new String[]{id});
    }

    public Integer deleteSocial(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_SOCIAL, "social_id = ?", new String[]{id});
    }

    public Integer deleteHide(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_HIDE, "hide_name = ?", new String[]{name});
    }

    public Integer deleteData(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_DELETE, "delete_name = ?", new String[]{name});
    }

    public Integer deleteOther(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_OTHER, "other_id = ?", new String[]{id});
    }

    public boolean updateBank(Bank_Data bank_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_BANK_NAME, bank_data.getBank_name());
        contentValues.put(COLUMN_BANK_NUMBER, bank_data.getAccount_nummber());
        contentValues.put(COLUMN_BANK_HOLDER, bank_data.getHolder_name());
        contentValues.put(COLUMN_BANK_TYPE, bank_data.getAccount_type());
        contentValues.put(COLUMN_BANK_IFSC, bank_data.getIfsc_code());
        contentValues.put(COLUMN_BANK_SWIFT, bank_data.getSwift_code());
        contentValues.put(COLUMN_BANK_EMAIL, bank_data.getEmail());
        contentValues.put(COLUMN_BANK_USERID, bank_data.getUser_id());
        contentValues.put(COLUMN_BANK_PASS, bank_data.getPass());
        contentValues.put(COLUMN_BANK_TRAPASS, bank_data.getTrapass());
        contentValues.put(COLUMN_BANK_URL, bank_data.getUrl());
        db.update(TABLE_BANK, contentValues, "bank_id = ?", new String[]{bank_data.getId()});
        return true;
    }

    public boolean updateCard(Card_Data card_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CARD_BNAME, card_data.getCard_bname());
        contentValues.put(COLUMN_CARD_TYPE, card_data.getCard_type());
        contentValues.put(COLUMN_CARD_NUMBER, card_data.getCard_number());
        contentValues.put(COLUMN_CARD_HOLDER, card_data.getCard_holder());
        contentValues.put(COLUMN_CARD_EDATE, card_data.getCard_expire());
        contentValues.put(COLUMN_CARD_PIN, card_data.getCard_pin());
        contentValues.put(COLUMN_CARD_CVV, card_data.getCard_cvv());
        db.update(TABLE_CARD, contentValues, "card_id = ?", new String[]{card_data.getId()});
        return true;
    }

    public boolean updateEmail(Email_Data email_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_EMAIL_NAME, email_data.getName());
        contentValues.put(COLUMN_EMAIL, email_data.getEmail());
        contentValues.put(COLUMN_EMAIL_PASS, email_data.getPassword());
        db.update(TABLE_EMAIL, contentValues, "email_id = ?", new String[]{email_data.getId()});
        return true;
    }

    public boolean updateContact(Contact_Data contact_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CONTACT_NAME, contact_data.getName());
        contentValues.put(COLUMN_CONTACT_NUMBER, contact_data.getNumber());
        db.update(TABLE_CONTACT, contentValues, "contact_id = ?", new String[]{contact_data.getId()});
        return true;
    }

    public boolean updateBookmark(Bookmark_Data bookmark_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_BOOKMARK_NAME, bookmark_data.getName());
        contentValues.put(COLUMN_BOOKMARK_URL, bookmark_data.getUrl());
        contentValues.put(COLUMN_BOOKMARK_IMAGE, bookmark_data.getImage());
        db.update(TABLE_BOOKMARK, contentValues, "bookmark_id = ?", new String[]{bookmark_data.getId()});
        return true;
    }

    public boolean updateNote(Note_Data note_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTE_TITLE, note_data.getTitle());
        contentValues.put(COLUMN_NOTE_DATA, note_data.getNote());
        contentValues.put(COLUMN_NOTE_DATE, note_data.getDate());
        db.update(TABLE_NOTE, contentValues, "note_id = ?", new String[]{note_data.getId()});
        return true;
    }

    public boolean updateSocial(Social_Data social_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SOCIAL_TYPE, social_data.getSocial_type());
        contentValues.put(COLUMN_SOCIAL_NAME, social_data.getSocial_name());
        contentValues.put(COLUMN_SOCIAL_USER, social_data.getSocial_email());
        contentValues.put(COLUMN_SOCIAL_PASS, social_data.getSocial_pass());
        db.update(TABLE_SOCIAL, contentValues, "social_id = ?", new String[]{social_data.getId()});
        return true;
    }

    public boolean updateOther(Other_Data other_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_OTHER_TITLE, other_data.getOther_title());
        contentValues.put(COLUMN_OTHER1, other_data.getOther1());
        contentValues.put(COLUMN_OTHER2, other_data.getOther2());
        contentValues.put(COLUMN_OTHER3, other_data.getOther3());
        contentValues.put(COLUMN_OTHER4, other_data.getOther4());
        contentValues.put(COLUMN_OTHER5, other_data.getOther5());
        contentValues.put(COLUMN_OTHER6, other_data.getOther6());
        contentValues.put(COLUMN_OTHER7, other_data.getOther7());
        db.update(TABLE_OTHER, contentValues, "other_id = ?", new String[]{other_data.getId()});
        return true;
    }
}
