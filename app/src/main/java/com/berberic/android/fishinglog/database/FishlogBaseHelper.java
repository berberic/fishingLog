package com.berberic.android.fishinglog.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.berberic.android.fishinglog.database.FishlogDbSchema.FishlogIDTable;
import com.berberic.android.fishinglog.database.FishlogDbSchema.FishlogTable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//import com.berberic.android.privatelog.FishlogID;


/**
 * Created by berberic on 8/28/2017.
 */

public class FishlogBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    public static String DB_FILEPATH = "/data/data/com.berberic.android.fishinglog.database/databases/fishlogBase.db";
    private static final String DATABASE_NAME = "fishlogBase.db";
    private static final String TAG = FishlogBaseHelper.class.getSimpleName();
 //   private static final String TABLE_TAG = "tags";


    public FishlogBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + FishlogTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                FishlogTable.Cols.UUID + ", " +
                FishlogTable.Cols.TITLE + ", " +
                FishlogTable.Cols.DATEIT + ", " +
                FishlogTable.Cols.PARTNERS + ", " +
                FishlogTable.Cols.LOCDESC + ", " +
                FishlogTable.Cols.HIDELOC + ", " +
                FishlogTable.Cols.QSF + ", " +
                FishlogTable.Cols.WATERTEMP + ", " +
                FishlogTable.Cols.RECEIPIENT + ", " +
                FishlogTable.Cols.NOTES + ")");

        db.execSQL("create table " + FishlogIDTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                FishlogIDTable.Cols.USERUUID + ", " +
                FishlogIDTable.Cols.ENCRYPTID + ", " +
                FishlogIDTable.Cols.LOCK+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FishlogTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FishlogIDTable.NAME);


        // create new tables
        onCreate(db);
    }


    //Code copy database from SD

    public void sdCardDatabase() {
        String databasePath = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/fishlogBase.db";

        try {
            File f1 = new File("path of your database");
            if (f1.exists()) {
                File f2 = new File(databasePath);
                f2.createNewFile();
                InputStream in = new FileInputStream(f1);
                OutputStream out = new FileOutputStream(f2);
                byte[] bufer = new byte[1024];
                int len;
                while ((len = in.read(bufer)) > 0) {
                    out.write(bufer, 0, len);
                }
                in.close();
                out.close();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());

        } catch (IOException e) {

        }
    }

    /*
	 * Creating tag
	 */
}
