package com.berberic.android.fishinglog;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.berberic.android.fishinglog.database.FishlogBaseHelper;
import com.berberic.android.fishinglog.database.FishlogCursorWrapper;
import com.berberic.android.fishinglog.database.FishlogDbSchema;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * * Copyright (C) 2017 Carl Berberich
 * Created by berberic on 8/27/2017.
 */

public class FishlogLab {
    private static FishlogLab sFishlogLab;
    //  private List<Fishlog> mFishlogs;
    private Context mContext;
    private SQLiteDatabase mDatabase;


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public static FishlogLab get(Context context){
        if (sFishlogLab == null){
            sFishlogLab = new FishlogLab(context);
        }
        return sFishlogLab;
    }
    private FishlogLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new FishlogBaseHelper(mContext)
                .getWritableDatabase();

    }

    public List<String> getFishlogs_Title(String whereClause, String[] whereArgs,String order){

        List<String> fishlogslocdesc = new ArrayList<>();
        FishlogCursorWrapper cursor = queryFishlogs_locdesc(whereClause, whereArgs, order);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                String data1 =  cursor.getString(cursor.getColumnIndex("locdesc"));
                String data =  cursor.getString(cursor.getColumnIndex("title"));
                if (data1.contains("LAT"))
                    fishlogslocdesc.add(data);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return fishlogslocdesc;
    }

    // private Cursor queryFishlog(String whereClause, String[] whereArgs ) {
    private FishlogCursorWrapper queryFishlogs_locdesc(String whereClause, String[] whereArgs, String order) {

        Cursor cursor = mDatabase.query(
                FishlogDbSchema.FishlogTable.NAME,
                null,
                //       "receipient =?",
                whereClause,
                //         new String[] {"('1')"},
                whereArgs,
                null,
                //         "receipient title",
                //         "receipient title date partners locdesc uuid qsf notes hideloc watertemp ",   // group by
                null,
                order
                //     "date DESC"   // order by
                //     "receipient ASC"
        );

        //        mDatabase.close();
        return new FishlogCursorWrapper(cursor);
    }

    public List<String> getFishlogs_LocDesc(String whereClause, String[] whereArgs,String order){

        List<String> fishlogslocdesc = new ArrayList<>();
        FishlogCursorWrapper cursor = queryFishlogs_locdesc(whereClause, whereArgs, order);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                String data =  cursor.getString(cursor.getColumnIndex("locdesc"));
                if (data.contains("LAT"))
                    fishlogslocdesc.add(data);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return fishlogslocdesc;

    }

    public List<Fishlog> getFishlogs(String whereClause, String[] whereArgs,String order){

        List<Fishlog> fishlogs = new ArrayList<>();
        FishlogCursorWrapper cursor = queryFishlogs(whereClause, whereArgs, order);
        //The Following works to display a filtered subset, but does not show/allow new data input
        //  FishlogCursorWrapper cursor = queryFishlogs("receipient = ?", new String [] {"Winter Season"});

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                fishlogs.add(cursor.getFishlog());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return fishlogs;

    }

    // Get the FishLog
    public Fishlog getFishlog(UUID id){

        FishlogCursorWrapper cursor = queryFishlogs(

                FishlogDbSchema.FishlogTable.Cols.UUID + " = ?",
                new String[] {id.toString()}, null
        );
        try {
            if (cursor.getCount() == 0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getFishlog();
        }finally {
            cursor.close();
        }

    }

    public File getPhotoFile(Fishlog fishlog){
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, fishlog.getPhotoFilename());
    }

    public void addFishlog(Fishlog c){

        //mFishlogs.add(c);
        ContentValues values = getContentValues(c);
        mDatabase.insert(FishlogDbSchema.FishlogTable.NAME, null, values);
//        mDatabase.close();
    }

    private static ContentValues getContentValues (Fishlog fishlog){
        ContentValues values = new ContentValues();
        values.put(FishlogDbSchema.FishlogTable.Cols.UUID, fishlog.getId().toString());
        values.put(FishlogDbSchema.FishlogTable.Cols.TITLE, fishlog.getTitle());
        values.put(FishlogDbSchema.FishlogTable.Cols.DATEIT, fishlog.getDate().getTime());
        //    values.put(FishlogDbSchema.FishlogTable.Cols.HIDELOC,fishlog.isHideloc() ? 1 : 0);
        //   values.put(FishlogDbSchema.FishlogTable.Cols.HIDELOC,fishlog.getHideloc());
        values.put(FishlogDbSchema.FishlogTable.Cols.HIDELOC,fishlog.isSelectVirtual() ? 1 : 0);
        values.put(FishlogDbSchema.FishlogTable.Cols.LOCDESC,fishlog.getLocDesc());
        // Search for Lat/long data and put in PARTNERs column if found
        //       String data = fishlog.getLocDesc().toString();
        //       if (data.contains("LAT:")) {
        //           data = data.substring(data.indexOf("LAT:"));
        //           values.put(FishlogDbSchema.FishlogTable.Cols.PARTNERS,data);
        //       }
        //      else
        values.put(FishlogDbSchema.FishlogTable.Cols.PARTNERS,fishlog.getPartners());
        values.put(FishlogDbSchema.FishlogTable.Cols.NOTES,fishlog.getNotes());
        values.put(FishlogDbSchema.FishlogTable.Cols.QSF,fishlog.getQSF());
        values.put(FishlogDbSchema.FishlogTable.Cols.WATERTEMP,fishlog.getWaterTemp());
        values.put(FishlogDbSchema.FishlogTable.Cols.RECEIPIENT,fishlog.getReceipient());
        return values;
    }


    public void updateFishlog(Fishlog fishlog) {
        String uuidString = fishlog.getId().toString();
        ContentValues values = getContentValues(fishlog);

        mDatabase.update(FishlogDbSchema.FishlogTable.NAME, values, FishlogDbSchema.FishlogTable.Cols.UUID + " = ?",
                new String[]{uuidString});
        //        mDatabase.close();
    }

    public String getLocDes(Fishlog fishlog){
        String locDes = fishlog.getLocDesc();
        return locDes;

    }

    public void deleteFishlog(Fishlog fishlog)
    {
        String uuidString = fishlog.getId().toString();
        ContentValues values = getContentValues(fishlog);
        mDatabase.delete(FishlogDbSchema.FishlogTable.NAME, FishlogDbSchema.FishlogTable.Cols.UUID + " = ?",
                new String[]{uuidString});
        //    mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + " = ?", new String[] {uuidString});
        //        mDatabase.close();
    }

    // private Cursor queryFishlog(String whereClause, String[] whereArgs ) {
    private FishlogCursorWrapper queryFishlogs(String whereClause, String[] whereArgs, String order) {

        String  filterSeason="ALL";
        //       filterSeason="Winter Season";

        //This has 7 fields query(String table, String[] columns, String selection, String[]
        // selectionArgs, String groupBy, String having, String orderBy
        //   new String[] {"('title1','title2','title3')"}
        Cursor cursor = mDatabase.query(
                FishlogDbSchema.FishlogTable.NAME,
                null,
                //       "receipient =?",
                whereClause,
                //         new String[] {"('1')"},
                whereArgs,
                null,
                //         "receipient title",
                //         "receipient title date partners locdesc uuid qsf notes hideloc watertemp ",   // group by
                null,
                order
                //     "date DESC"   // order by
                //     "receipient ASC"
        );

        //        mDatabase.close();
        return new FishlogCursorWrapper(cursor);
    }

    public boolean importDatabase(boolean OverOrAppend,String filenameDir, String Key) {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return false;
        } else {

            //We use the Download directory for saving our .csv file.
            File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            StringBuilder text = new StringBuilder();
            //     BufferedReader br;
            BufferedReader br = null;
            try {

                //      File file = new File(filenameDir);

                br = new BufferedReader(new FileReader(filenameDir));
                String encryptedline;
                String line="";
                br.readLine(); // Skip the first line
                // Overwrite the database
                if (!OverOrAppend)
                    mDatabase.delete(FishlogDbSchema.FishlogTable.NAME,null,null);
                while ((encryptedline = br.readLine()) != null) {
                    //   while ((line = br.readLine()) != null) {
                    if ((Key == null) || (Key.equals("")))
                        line=encryptedline;
                    else
                        line =decryption( encryptedline,Key);

                    text.append(line);
                    Log.i("Test", "text : " + text + " : end");
                    text.append('\n');
                    //          Cursor curCSV = mDatabase.rawQuery("insert uuid,title,date,qsf,partners,locdesc," +
                    //                  "notes,watertemp,receipient,hideloc from fishlogs", null);

                    String[] str =null;
                    if (line.contains(",")) {
                        Pattern pattern = Pattern.compile(",");
                        Matcher matcher = pattern.matcher(line);
                        int count = 0;
                        while(matcher.find())
                            count++;
                        if (count != 9)
                            return false;
                        str = line.split(",", 10);  // defining 3 columns with null or blank field //values acceptance
                    } else
                        return false;

                    String uuid = str[0].toString();
                    String title = str[1].toString();
                    String date1 = str[2].toString();
                    Date parseDate = null;

                    if (date1.contains("/")){
                        String year =   "20" + date1.substring(date1.lastIndexOf("/")+1);
                        String month= date1.substring(0,date1.indexOf("/"));
                        String day  = date1.substring(date1.indexOf("/")+1,date1.lastIndexOf("/"));

                        String newDate = year+"-"+month+"-"+day;
                        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

                        try {
                            parseDate = f.parse(newDate);
                            //parseDate = df2.parse(date1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            parseDate = df2.parse(date1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    long milliseconds = parseDate.getTime();


                    String qsf =  str[3].toString();
                    String partners = str[4].toString(); //seasons
                    String locdesc = str[5].toString();
                    String notes = str[6].toString();
                    //          String qsf =  str[7].toString();
                    String watertemp = str[7].toString();
                    String receipient = str[8].toString();//season

                    ContentValues values = new ContentValues();
                    //Append
                    if (OverOrAppend) {
                        UUID mId = UUID.randomUUID();
                        values.put(FishlogDbSchema.FishlogTable.Cols.UUID, mId.toString());
                    } else
                        values.put(FishlogDbSchema.FishlogTable.Cols.UUID, str[0].toString());

                    values.put(FishlogDbSchema.FishlogTable.Cols.TITLE, str[1].toString());
                    values.put(FishlogDbSchema.FishlogTable.Cols.DATEIT,milliseconds );
                    // The following works and give current date/time
                    //        values.put(FishlogDbSchema.FishlogTable.Cols.DATEIT, new Date().getTime());//This works
                    values.put(FishlogDbSchema.FishlogTable.Cols.QSF,qsf);

                    values.put(FishlogDbSchema.FishlogTable.Cols.PARTNERS,partners);//seasons
                    values.put(FishlogDbSchema.FishlogTable.Cols.LOCDESC,locdesc);
                    values.put(FishlogDbSchema.FishlogTable.Cols.NOTES,notes);

                    values.put(FishlogDbSchema.FishlogTable.Cols.WATERTEMP,watertemp);
                    values.put(FishlogDbSchema.FishlogTable.Cols.RECEIPIENT,receipient);
                    values.put(FishlogDbSchema.FishlogTable.Cols.HIDELOC,0);

                    // Drop all data from table

                    mDatabase.insert(FishlogDbSchema.FishlogTable.NAME, null, values);

                }

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {

                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            return true;
        }
    }

    public boolean exportSelectionDatabase(String Key, String filenameDir) {

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

     //   SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String curdate = df2.format(new Date());

        Calendar cal1 = Calendar.getInstance();
        cal1.add(Calendar.DATE, 10);
        //   cal1.add(Calendar.DATE, 1);
//        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(cal1.getTime());
// Output "Wed Sep 26 14:23:28 EST 2012"

    //    String expireddate = format2.format(cal1.getTime());
     //   System.out.println(expireddate);
// Output "2012-09-26"

        // System.out.println(format2.parse(formatted));


        /**First of all we check if the external storage of the device is available for writing.
         * Remember that the external storage is not necessarily the sd card. Very often it is
         * the device storage.
         */

        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return false;
        }
        else {
            //We use the Download directory for saving our .csv file.
            File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            File file;
            PrintWriter printWriter = null;

            try {

                //     file = new File(exportDir, "MyFlyFishingLog.csv");
                file = new File(filenameDir);

                printWriter = new PrintWriter(new FileWriter(file));

                /**Let's read the first table of the database.
                 * getFirstTable() is a method in our DBCOurDatabaseConnector class which retrieves a Cursor
                 * containing all records of the table (all fields).
                 * The code of this class is omitted for brevity.
                 */
                //   Cursor curCSV = mDatabase.rawQuery("select * from fishlogs order by date", null);
                Cursor curCSV = mDatabase.rawQuery("select uuid,title,date,qsf,partners,locdesc," +
                        "notes,watertemp,receipient,hideloc " +
                        "from fishlogs order by date desc", null);

                //Write the name of the table and the name of the columns (comma separated values) in the .csv file.
                printWriter.println("UUID,TITLE,DATE,QSF,SEASON,LOCDESC,NOTES,WATERTEMP,MONTH,SELECTION");
                while (curCSV.moveToNext()) {
                    String uuid = curCSV.getString(curCSV.getColumnIndex("uuid"));
                    String title = curCSV.getString(curCSV.getColumnIndex("title"));
                    Long date = curCSV.getLong(curCSV.getColumnIndex("date"));
                    String qsf = curCSV.getString(curCSV.getColumnIndex("qsf"));
                    String partners = curCSV.getString(curCSV.getColumnIndex("partners"));
                    String locdesc = curCSV.getString(curCSV.getColumnIndex("locdesc"));
                    String notes = curCSV.getString(curCSV.getColumnIndex("notes"));
                    String watertemp = curCSV.getString(curCSV.getColumnIndex("watertemp"));
                    String season = curCSV.getString(curCSV.getColumnIndex("receipient"));
                    int selection = curCSV.getInt(curCSV.getColumnIndex("hideloc"));

                    //     Long expiredate = curCSV.getLong(curCSV.getColumnIndex("expiredate"));


                    /**Create the line to write in the .csv file.
                     * We need a String where values are comma separated.
                     * The field date (Long) is formatted in a readable text. The amount field
                     * is converted into String.
                     */
                    // cover bad data
                    if (partners == null)
                        partners="";
                    if (uuid == null)
                        uuid="";
                    if (qsf == null)
                        qsf="";
                    if (locdesc == null)
                        locdesc="";
                    if (notes == null)
                        notes = "";
                    if (watertemp == null)
                        watertemp = "";
                    if (season == null)
                        season = "";

                    if (selection > 0) {

                        String record = uuid + "," + title.replaceAll("\n", ":").replaceAll(",", " ") + "," +
                                df2.format(new Date(date)) + "," +
                                qsf.replaceAll("\n", ":").replaceAll(",", " ") + "," +
                                partners.replaceAll("\n", ":").replaceAll(",", " ") + "," +
                                locdesc.replaceAll("\n", " ").replaceAll(",", " ") + "," +
                                notes.replaceAll("\n", " ").replaceAll(",", " - ") + "," +
                                watertemp.replaceAll("\n", " ").replaceAll(",", " ") + "," +
                                season + "," + selection ;

                        String encryptionBytes = null;
                        if ((Key == null) || (Key.equals("")))
                            encryptionBytes = record;
                        else
                            encryptionBytes = encryption(record, Key);

                        printWriter.println(encryptionBytes); //write the record in the .csv file
                    }
                }

                curCSV.close();
                mDatabase.close();
            } catch (Exception exc) {
                //if there are any exceptions, return false
                return false;
            } finally {
                if (printWriter != null) printWriter.close();

            }
        }

        //If there are no errors, return true.
        return true;
    }

    public boolean exportDatabase(String Key, String filenameDir) {

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        /**First of all we check if the external storage of the device is available for writing.
         * Remember that the external storage is not necessarily the sd card. Very often it is
         * the device storage.
         */

        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return false;
        }
        else {
            //We use the Download directory for saving our .csv file.
            File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            File file;
            PrintWriter printWriter = null;

            try {

                //     file = new File(exportDir, "MyFlyFishingLog.csv");
                file = new File(filenameDir);

                printWriter = new PrintWriter(new FileWriter(file));

                /**Let's read the first table of the database.
                 * getFirstTable() is a method in our DBCOurDatabaseConnector class which retrieves a Cursor
                 * containing all records of the table (all fields).
                 * The code of this class is omitted for brevity.
                 */
                //   Cursor curCSV = mDatabase.rawQuery("select * from fishlogs order by date", null);
                Cursor curCSV = mDatabase.rawQuery("select uuid,title,date,qsf,partners,locdesc," +
                        "notes,watertemp,receipient,hideloc from fishlogs order by date desc", null);

                //Write the name of the table and the name of the columns (comma separated values) in the .csv file.
                printWriter.println("UUID,TITLE,DATE,QSF,SEASON,LOCDESC,NOTES,WATERTEMP,MONTH,HIDELOC");
                while (curCSV.moveToNext()) {
                    String uuid = curCSV.getString(curCSV.getColumnIndex("uuid"));
                    String title = curCSV.getString(curCSV.getColumnIndex("title"));
                    Long date = curCSV.getLong(curCSV.getColumnIndex("date"));
                    String qsf = curCSV.getString(curCSV.getColumnIndex("qsf"));
                    String partners = curCSV.getString(curCSV.getColumnIndex("partners"));
                    String locdesc = curCSV.getString(curCSV.getColumnIndex("locdesc"));
                    String notes = curCSV.getString(curCSV.getColumnIndex("notes"));
                    String watertemp = curCSV.getString(curCSV.getColumnIndex("watertemp"));
                    String season = curCSV.getString(curCSV.getColumnIndex("receipient"));
                    int hideloc = curCSV.getInt(curCSV.getColumnIndex("hideloc"));


                    /**Create the line to write in the .csv file.
                     * We need a String where values are comma separated.
                     * The field date (Long) is formatted in a readable text. The amount field
                     * is converted into String.
                     */
                    // cover bad data
                    if (partners == null)
                        partners="";
                    if (uuid == null)
                        uuid="";
                    if (qsf == null)
                        qsf="";
                    if (locdesc == null)
                        locdesc="";
                    if (notes == null)
                        notes = "";
                    if (watertemp == null)
                        watertemp = "";
                    if (season == null)
                        season = "";

                    String record = uuid + "," + title.replaceAll("\n", ":").replaceAll(","," ")  + "," +
                            df2.format(new Date(date))+ "," +
                            qsf.replaceAll("\n", ":").replaceAll(","," ")  + "," +
                            partners.replaceAll("\n", ":").replaceAll(","," ") + "," +
                            locdesc.replaceAll("\n", " ").replaceAll(","," ") + "," +
                            notes.replaceAll("\n", " ").replaceAll(","," - ") + "," +
                            watertemp.replaceAll("\n"," ").replaceAll(","," ")+ "," +
                            season + "," +hideloc;

                    String  encryptionBytes = null;
                    if ((Key == null) || (Key.equals("")))
                        encryptionBytes=record;
                    else
                        encryptionBytes = encryption(record, Key);

                    printWriter.println(encryptionBytes); //write the record in the .csv file
                }

                curCSV.close();
                mDatabase.close();
            } catch (Exception exc) {
                //if there are any exceptions, return false
                return false;
            } finally {
                if (printWriter != null) printWriter.close();

            }
        }

        //If there are no errors, return true.
        return true;
    }

    public boolean deleteDatabaseFile(String filenameDir) {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return false;
        } else {

            //We use the Download directory for saving our .csv file.
            File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            File file = new File(filenameDir);
            file.delete();


            return true;
        }
    }

    public static String encrypt(String seed, String cleartext) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] result = encrypt(rawKey, cleartext.getBytes());
        return toHex(result);
    }

    public static String decrypt(String seed, String encrypted) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(rawKey, enc);
        return new String(result);
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG",new CryptoProvider());
        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }
    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2*buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }
    private final static String HEX = "0123456789ABCDEF";
    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
    }
    public String encryption(String strNormalText, String seedValue){
        //    String seedValue = "YourSecKey";
        String normalTextEnc="";
        try {
            normalTextEnc =encrypt(seedValue, strNormalText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return normalTextEnc;
    }
    public String decryption(String strEncryptedText,String seedValue){
        //   String seedValue = "YourSecKey";
        String strDecryptedText="";
        try {
            strDecryptedText =decrypt(seedValue, strEncryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDecryptedText;
    }

    public void createTag(FishlogID fishlogid) {



        fishlogid.setID();
        UUID id = fishlogid.getID();
        String uniqueID = id.toString().substring(id.toString().length()-8);
        ContentValues values = new ContentValues();
        //   values.put("encryptid", fishlogid.getEncryptID());
        values.put("encryptid", "");
        //   values.put("lockid",fishlogid.getLock());
        values.put("lockid", "");
        values.put("useruuid", uniqueID);
        // insert row
        long tag_id = mDatabase.insert(FishlogDbSchema.FishlogIDTable.NAME, null, values);
        //         }
        //  }
    }

    /**
     * getting all tags
     * */
    public List<FishlogID> getAllTags() {
        List<FishlogID> tags = new ArrayList<FishlogID>();
        String selectQuery = "SELECT  * FROM " + FishlogDbSchema.FishlogIDTable.NAME;

        //    Log.e(LOG, selectQuery);


        Cursor c = mDatabase.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                //    String data =  c.getString(c.getColumnIndex("locdesc"));
                FishlogID t = new FishlogID();
                //       t.setId(c.getInt((c.getColumnIndex("useruuid"))));
                t.setID();
                t.setEncryptID(c.getString(c.getColumnIndex("encryptid")));
                //      t.setTagName(c.getString(c.getColumnIndex(KEY_TAG_NAME)));
                t.setLock(c.getString(c.getColumnIndex("lockid")));

                // adding to tags list
                tags.add(t);
            } while (c.moveToNext());
        }
        return tags;
    }

    public String getID() {
        List<FishlogID> tags = new ArrayList<FishlogID>();
        String selectQuery = "SELECT  * FROM " + FishlogDbSchema.FishlogIDTable.NAME;

        Cursor c = mDatabase.rawQuery(selectQuery, null);
        String data="";
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                data =  c.getString(c.getColumnIndex("useruuid"));

            } while (c.moveToNext());
        }
        return data;
    }

    public String getlockID() {
        List<FishlogID> tags = new ArrayList<FishlogID>();
        String selectQuery = "SELECT  * FROM " + FishlogDbSchema.FishlogIDTable.NAME;

        Cursor c = mDatabase.rawQuery(selectQuery, null);
        String data="";
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                data =  c.getString(c.getColumnIndex("lockid"));

            } while (c.moveToNext());
        }
        return data;
    }

    public String getencryptID() {
        List<FishlogID> tags = new ArrayList<FishlogID>();
        String selectQuery = "SELECT  * FROM " + FishlogDbSchema.FishlogIDTable.NAME;

        Cursor c = mDatabase.rawQuery(selectQuery, null);
        String data="";
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                data =  c.getString(c.getColumnIndex("encryptid"));

            } while (c.moveToNext());
        }
        return data;
    }

    /*
     * Updating a tag
     */
    public int updateTag(FishlogID fishlogid) {


        ContentValues values = new ContentValues();
        //     values.put(KEY_TAG_NAME, tag.getTagName());
        values.put("encryptid", fishlogid.getEncryptID());
        values.put("lockid",fishlogid.getLock());
        //    values.put("useruuid", fishlogid.getID().toString());

        // updating row
        return mDatabase.update(FishlogDbSchema.FishlogIDTable.NAME, values, "useruuid = ?",
                new String[] { String.valueOf(fishlogid.getID()) });
    }

    public void upgrade (String userid, String lockid){
        ContentValues values = new ContentValues();

        values.put("encryptid","");
        values.put("lockid",lockid);
        mDatabase.update(FishlogDbSchema.FishlogIDTable.NAME, values, "useruuid = ?",
                new String[] { userid });
    }
    /*
     * Deleting a tag
     */
    public void deleteTag(FishlogID tag) {
        // now delete the tag
        mDatabase.delete(FishlogDbSchema.FishlogIDTable.NAME, "useruuid = ?",
                new String[] { String.valueOf(tag.getID()) });
    }

    public List<FishlogID> getFishlogsID(String whereClause, String[] whereArgs,String order){

        List<FishlogID> fishlogsID = new ArrayList<>();
        //    List<Fishlog> fishlogs1 = new ArrayList<>();
        FishlogCursorWrapper cursor = queryFishlogsTableID(whereClause, whereArgs, order);
        //The Following works to display a filtered subset, but does not show/allow new data input
        //  FishlogCursorWrapper cursor = queryFishlogs("receipient = ?", new String [] {"Winter Season"});

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                fishlogsID.add(cursor.getFishlogID());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        //  fishlogs1.addAll(fishlogs);
        return fishlogsID;
    }

    private FishlogCursorWrapper queryFishlogsTableID(String whereClause, String[] whereArgs, String order) {

        Cursor cursor = mDatabase.query(
                FishlogDbSchema.FishlogIDTable.NAME,
                null,
                //       "receipient =?",
                whereClause,
                //         new String[] {"('1')"},
                whereArgs,
                null,
                //         "receipient title",
                //         "receipient title date partners locdesc uuid qsf notes hideloc watertemp ",   // group by
                null,
                order
                //     "date DESC"   // order by
                //     "receipient ASC"
        );

        //        mDatabase.close();
        return new FishlogCursorWrapper(cursor);
    }
}

