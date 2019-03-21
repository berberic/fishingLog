package com.berberic.android.fishinglog.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.berberic.android.fishinglog.Fishlog;
import com.berberic.android.fishinglog.FishlogID;

import java.util.Date;
import java.util.UUID;

/**
 * Created by berberic on 8/28/2017.
 */

public class FishlogCursorWrapper extends CursorWrapper{

    public FishlogCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public FishlogID getFishlogID() {
        String userid = getString(getColumnIndex(FishlogDbSchema.FishlogIDTable.Cols.USERUUID));
        String useridlock = getString(getColumnIndex(FishlogDbSchema.FishlogIDTable.Cols.LOCK));
        String encriptedID = getString(getColumnIndex(FishlogDbSchema.FishlogIDTable.Cols.ENCRYPTID));


        FishlogID fishlogID = new FishlogID();
        if (fishlogID.getID()==null) {
            fishlogID.setID();

            UUID ID = fishlogID.getID();
            fishlogID.setLock(useridlock);
            fishlogID.setEncryptID("test2");
            fishlogID.setID();
        }

        return fishlogID;
    }

    public Fishlog getFishlog() {
        String uuidString = getString(getColumnIndex(FishlogDbSchema.FishlogTable.Cols.UUID));
        String title = getString(getColumnIndex(FishlogDbSchema.FishlogTable.Cols.TITLE));
        long date = getLong(getColumnIndex(FishlogDbSchema.FishlogTable.Cols.DATEIT));
        //    int isSolved = getInt(getColumnIndex(FishlogDbSchema.FishlogTable.Cols.HIDELOC));
        String partners = getString(getColumnIndex(FishlogDbSchema.FishlogTable.Cols.PARTNERS));
        String locdesc = getString(getColumnIndex(FishlogDbSchema.FishlogTable.Cols.LOCDESC));
        String notes = getString(getColumnIndex(FishlogDbSchema.FishlogTable.Cols.NOTES));
        String qsf = getString(getColumnIndex(FishlogDbSchema.FishlogTable.Cols.QSF));
        String watertemp = getString(getColumnIndex(FishlogDbSchema.FishlogTable.Cols.WATERTEMP));
        String receipient = getString(getColumnIndex(FishlogDbSchema.FishlogTable.Cols.RECEIPIENT));
        int isSelected = getInt(getColumnIndex(FishlogDbSchema.FishlogTable.Cols.HIDELOC));

        //  String log = getString(getColumnIndex(FishlogDbSchema.FishlogTable.NAME));  // does not work


        Fishlog fishlog = new Fishlog(UUID.fromString(uuidString));
        fishlog.setTitle(title);  //Stream name
        fishlog.setDate(new Date(date));
        //    fishlog.setSolved(isSolved != 0);
        //   fishlog.setPartners(uuidString);
        //    fishlog.setPartners(receipient);


        //       if (locdesc.contains("LAT:"))
        //      if (partners.contains("LAT"))
        //          partners="";
        //          partners = locdesc.substring(locdesc.indexOf("LAT:"));


        fishlog.setPartners(partners);
        fishlog.setLocDesc(locdesc);
        fishlog.setQSF(qsf);
        fishlog.setNotes(notes);
        fishlog.setWaterTemp(watertemp);
        //    fishlog.setHideloc(isSolved != 0);
        //    fishlog.setHideloc(isSolved);
        fishlog.setReceipient(receipient);
        fishlog.setSelectVirtual(isSelected !=0);

        return fishlog;
    }
}
