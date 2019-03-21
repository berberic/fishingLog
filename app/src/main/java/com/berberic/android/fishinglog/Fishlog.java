package com.berberic.android.fishinglog;

/**
 * Created by berberic on 8/27/2017.
 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * * Copyright (C) 2017 Carl Berberich
 * Created by berberic on 8/26/2017.
 */

public class Fishlog {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private int mHideloc;
    private String mPartners;
    private String mLocDesc;
    private String mNotes;
    private String mQSF;
    private String mWaterTemp;
    private String mReceipient;
    private String mTable;
    private boolean mSelectVirtual;


    public UUID getId() {
        return mId;
    }

    public Fishlog(){
    //    mId = UUID.randomUUID();
    //    mDate = new Date();

        this(UUID.randomUUID());
    }

    public Fishlog(UUID id){
        mId = id;
        mDate = new Date();
    }


    public String getWaterTemp() {
        return mWaterTemp;
    }

    public void setWaterTemp(String waterTemp) {
        mWaterTemp = waterTemp;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSelectVirtual() {
        return mSelectVirtual;
    }

    public void setSelectVirtual(boolean selectVirtual) {
        mSelectVirtual = selectVirtual;
    }

    public int getHideloc() {
        return mHideloc;
    }

    //  public void setHideloc(boolean hideloc) {
    //     mHideloc = hideloc;
    //  }

    public void setHideloc(int hideloc) {
        mHideloc = hideloc;
    }

    public String getPartners() {
        return mPartners;
    }

    public void setPartners(String partners) {
        mPartners = partners;
    }

    public String getLocDesc() {
        return mLocDesc;
    }

    public void setLocDesc(String locDesc) {
        mLocDesc = locDesc;
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        mNotes = notes;
    }

    public String getQSF() {
        return mQSF;
    }

    public void setQSF(String QSF) {
        mQSF = QSF;
    }

    public String getReceipient(){
        return mReceipient;
    }

    public void setReceipient(String receipient){
        mReceipient = receipient;
    }

    public String getPhotoFilename() {
        return "FLY_"+getId().toString() + ".jpg";
    }
}
