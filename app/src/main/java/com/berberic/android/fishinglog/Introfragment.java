package com.berberic.android.fishinglog;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by berberic on 10/25/2017.
 */

public class Introfragment extends Fragment {

    private String simpledb = "fishlogBase";
    private String tablename = "fishlogs";
    private SQLiteDatabase mDatabase;
    private Context mContext;
    private Button mStartButton;
    private TextView mUserID;
  //  private Button mHelpButton;
  //  private Button mExportDb;
  //  private RadioButton mChrono;
  //  private RadioButton mMonth;
  //  private RadioButton mSeason;
    private static final String SAMPLE_DB_NAME = "fishlogBase";
    private static final String SAMPLE_TABLE_NAME = "fishlogs";
    private static String uniqueID = null;
    //private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_intro,container,false);



        FishlogLab fishlogLab = FishlogLab.get(getActivity());
   //

     //   int fishlogCount = fishlogLab.getFishlogsID(null, null, null).size();
        FishlogID tag1 = new FishlogID();

        int siz=fishlogLab.getAllTags().size();
        if (siz < 1)
            fishlogLab.createTag(tag1);  // Sets a three fields

 //
        // returns Unique ID
        String getID=fishlogLab.getID();
        String getLock=fishlogLab.getlockID();
        String getEncryptID= fishlogLab.getencryptID();
       // List<FishlogID> db.getAllTags();
    //    List list =fishlogLab.getAllTags();
   //     String id = db.getID();

  //      if (checkUpdate())
 //           getActivity().setTitle("UpGrade");

        // This was commented out of the XML GUI
  //      mUserID = (TextView) v.findViewById(R.id.userID);
  //      mUserID.setText("UserID:"+getID.trim());

        mStartButton = (Button) v.findViewById(R.id.start_button);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            String message = "";
            @Override
            public void onClick(View v) {


        /*        if (mChrono.isChecked()){
                    message = "chrono";
                } else if (mMonth.isChecked()){
                    message = "month";
                } else {
                    message = "season";
                }

                Bundle bundle = new Bundle();
                bundle.putString("message", message );
                         Intent intent = new Intent(getActivity(), FishlogListActivity.class);
           */     Intent intent = FishlogListActivity.newIntent(getActivity(),message);
                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_intro, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help_action:
                Intent intent = new Intent(getActivity(), FishlogHelpActivity.class);
                startActivity(intent);
                break;
            case R.id.import_export_action:
                //     Intent intent = FishlogImportExportActivity.this,FishlogListActivity.class);
                //     Intent intent = FishlogImportExportActivity.newIntent(getActivity(),null);
                Intent intent1 = new Intent(getActivity(),FishlogImportExportActivity.class);
                startActivity(intent1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkUpdate(){
        boolean mMapUpdate=false;
        FishlogLab fishlogLab = FishlogLab.get(getActivity());
        String checklock=fishlogLab.getlockID();
        mMapUpdate=true;

        if (checklock.equals(""))
            mMapUpdate=false;

        return mMapUpdate;
    }
    public void getFileDelete(String chosenFile){

        int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        FishlogLab fishlogLab = FishlogLab.get(getActivity());

        boolean db = fishlogLab.deleteDatabaseFile(chosenFile);

    }

}
