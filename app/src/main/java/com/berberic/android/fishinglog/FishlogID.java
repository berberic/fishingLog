package com.berberic.android.fishinglog;

/**
 * Created by berberic on 1/29/2018.
 */

import java.util.UUID;

public class FishlogID {
    private UUID mID;
    public String mLock;
    public String mEncryptID;

    public FishlogID(){
    }

    public FishlogID(UUID mID, String mLock, String mEncryptID){
        this.mLock=mLock;
        this.mID=mID;
        this.mEncryptID=mEncryptID;
    }

    public String getEncryptID(){
        return mEncryptID;
    }

    public void setEncryptID(String encryptID) {
        mEncryptID = encryptID;
    }

    public UUID getID(){
        return mID;
    }

    public void setID(){
        mID =   UUID.randomUUID();
    }

    public void setLock(String lock) {
        mLock = lock;
    }

    public String getLock() {
        return mLock;
    }
}
