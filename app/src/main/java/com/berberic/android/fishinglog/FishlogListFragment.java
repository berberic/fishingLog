package com.berberic.android.fishinglog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

//import com.google.firebase.analytics.FirebaseAnalytics;


/**
 * * Copyright (C) 2017 Carl Berberich
 * Created by berberic on 8/27/2017.
 */

public class FishlogListFragment extends Fragment {
    private static final String ARG_MESSAGE="order_id";
    private RecyclerView mFishlogRecyclerView;
    private FishlogAdapter mAdapter;
    private boolean mSubtitleVisible;
    private boolean mMapUpdate;
    private String mQuery;
    private String [] mArgs;
    private String arg;
    private String mOrder;
    private String mSeason;
 //   private FirebaseAnalytics mFirebaseAnalytics;
//    private String mOrder;
//    private String mMessage;

    private RadioButton mSummer;
    private RadioButton mSpring;
    private RadioButton mFall;
    private RadioButton mWinter;
    //   private RadioButton mAll;
    private RadioButton mTitle;
    private int fishlogsize;

    private String mLocDescShortName;
    private String mQSFSearch;
    private String mNotesSearch;
    //   private RadioButton mAll;
    private String mYear;
    private View mLayout;
    private Button mAddCrimeButton;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (checkUpdate())
            getActivity().setTitle("UpGrade");
    //    mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

  //      String message = (String) getArguments().getCharSequence(ARG_MESSAGE);
  //      mMessage = message;

/*        String id ="CarlsFlyFishingLog";
        String name = "FishlogListFragment";

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
*/

    }

    public static FishlogListFragment newInstance(String orderId) {
        Bundle args = new Bundle();
        args.putCharSequence(ARG_MESSAGE, orderId);

        FishlogListFragment fragment = new FishlogListFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fishlog_list, container,false);
        mLocDescShortName="";
        mQSFSearch="";
        mNotesSearch="";

        mSpring = (RadioButton) view.findViewById(R.id.spring);
        mSpring.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mLocDescShortName ="";
                mQSFSearch="";
                mNotesSearch="";
                String whereClause = " partners=?";
                String[] whereArgs = new String[] { "1"};
                updateUI(whereClause, whereArgs,"receipient ASC, title asc");
            }
        });

        mSummer = (RadioButton) view.findViewById(R.id.summer);
        mSummer.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mLocDescShortName ="";
                mQSFSearch="";
                mNotesSearch="";
                String order = "receipient ASC, title asc";

                String whereClause = "partners=?";
                String[] whereArgs = new String[] {"2"};
                updateUI(whereClause, whereArgs,"receipient ASC, title asc");
            }
        });

        mFall = (RadioButton) view.findViewById(R.id.fall);
        mFall.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mLocDescShortName ="";
                mQSFSearch="";
                mNotesSearch="";
                String order = "receipient ASC, title asc";

                String whereClause = "partners=?";
                String[] whereArgs = new String[] {"3"};
                updateUI(whereClause, whereArgs,order);
            }
        });

        mWinter = (RadioButton) view.findViewById(R.id.winter);
        mWinter.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mLocDescShortName ="";
                mQSFSearch="";
                mNotesSearch="";
                String order = "receipient ASC, title asc";

                String whereClause = "partners=?";
                String[] whereArgs = new String[] {"4"};
                updateUI(whereClause, whereArgs,order);
            }
        });

        //Loc selection
        mTitle = (RadioButton) view.findViewById(R.id.title_loc);
        mTitle.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mLocDescShortName ="";
                mQSFSearch="";
                mNotesSearch="";
                String whereClause = null;
                String[] whereArgs = null;
                updateUI(whereClause, whereArgs,"title ASC,date DESC");
            }
        });

        mFishlogRecyclerView = (RecyclerView) view.findViewById(R.id.fishlog_recycler_view);
        mFishlogRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // The following was added to support a button which is only viewed and active when there is no data in the Log
        mLayout = view.findViewById(R.id.add_crime_view);
        mAddCrimeButton = view.findViewById(R.id.add_crime);
        mAddCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fishlog fishlog = new Fishlog();
                FishlogLab.get(getActivity()).addFishlog(fishlog);
                Intent intent = FishlogPagerActivity.newIntent(getActivity(),fishlog.getId(),"%"+null+"^",null,"&date DESC");
                startActivity(intent); //starting an instance of CrimePageActivity to edit new Crime

            }
        });

        if (savedInstanceState != null){
  //          mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        return view;
    }



    private class FishlogHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSelectExp;
        private Fishlog mFishlog;

        public FishlogHolder(LayoutInflater inflater, ViewGroup parent){
            super((inflater.inflate(R.layout.list_item_fishlog,parent,false)));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.fishlog_title);
            mDateTextView =  (TextView) itemView.findViewById(R.id.fishlog_date);
            mSelectExp = (CheckBox) itemView.findViewById(R.id.select_export);
        }

        public void bind(Fishlog fishlog){
            mFishlog = fishlog;
            mTitleTextView.setText(mFishlog.getTitle());

        // Shortened version of date
        //    String formattedDate = new SimpleDateFormat("EEEE, MMM d, yyyy").format(mFishlog.getDate());
        //    mDateTextView.setText(formattedDate.toString());

            mDateTextView.setText(mFishlog.getDate().toString());

            boolean isChecked = mFishlog.isSelectVirtual();
            if (isChecked)
                mSelectExp.setChecked(true);
            else
                mSelectExp.setChecked(false);
        }

        @Override
        public void onClick(View view){
            //Toast.makeText(getActivity(),mFishlog.getTitle() + " clicked!", Toast.LENGTH_SHORT).show();

            //  Intent intent = new Intent(getActivity(),FishlogActivity.class);
           //   Intent intent = FishlogActivity.newIntent(getActivity(),mFishlog.getId());
            Intent intent = FishlogPagerActivity.newIntent(getActivity(),mFishlog.getId(),mQuery,arg,mOrder);
            startActivity(intent);

        }
    }

    private class FishlogAdapter extends RecyclerView.Adapter<FishlogHolder>{
        private List<Fishlog> mFishlogs;
        public FishlogAdapter(List<Fishlog> fishlogs){
            mFishlogs = fishlogs;
        }

        @Override
        public FishlogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new FishlogHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(FishlogHolder holder, int position) {
            Fishlog fishlog = mFishlogs.get(position);
            holder.bind(fishlog);
        }

        @Override
        public int getItemCount() {
            return mFishlogs.size();
        }

        public void setFishlogs(List<Fishlog> fishlogs) {
            mFishlogs =fishlogs;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String order="date DESC";
        String whereClause = null;
        String[] whereArgs = null;

        if (mTitle.isChecked()) //Loc selection
            order="title ASC,date DESC";
        else if (mSpring.isChecked()){
            whereClause = " partners=?";
            whereArgs = new String[] { "1"};
            order="receipient ASC, title ASC";
        }
        else if (mSummer.isChecked()){
            whereClause = " partners=?";
            whereArgs = new String[] { "2"};
            order="receipient ASC, title ASC";
        }
        else if (mFall.isChecked()){
            whereClause = " partners=?";
            whereArgs = new String[] { "3"};
            order="receipient ASC, title ASC";
        }
        else if (mWinter.isChecked()){
            whereClause = " partners=?";
            whereArgs = new String[] { "4"};
            order="receipient ASC, title ASC";
        } else if (!mLocDescShortName.equals("")){
            whereClause = " title like?";
            whereArgs = new String[] { mLocDescShortName+"%"};
            order="title ASC,date DESC";
            mYear="0";
        }
        else if (!mQSFSearch.equals("")){
            whereClause = " QSF like?";
            whereArgs = new String[] { "%"+mQSFSearch+"%"};
            order="title ASC,date DESC";
        } else if (!mNotesSearch.equals("")){
            whereClause = " Notes like?";
            whereArgs = new String[] { "%"+mNotesSearch+"%"};
            order="title ASC,date DESC";
        }
        //mAll.setChecked(true);
        updateUI(whereClause,whereArgs,order);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_fishlog_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.new_fishlog:
                Fishlog fishlog = new Fishlog();
                FishlogLab.get(getActivity()).addFishlog(fishlog);
                Intent intent = FishlogPagerActivity.newIntent(getActivity(),fishlog.getId(),"%"+null+"^",null,"&date DESC");
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle(null,null,null);
                return true;
            case R.id.show_view:            // View MAP of List
           //     mSubtitleVisible = !mSubtitleVisible;
           //     getActivity().invalidateOptionsMenu();
                String Season="1";
                if (mSpring.isChecked())
                    Season="1";
                else if (mSummer.isChecked())
                    Season="2";
                else if (mFall.isChecked())
                    Season="3";
                else if (mWinter.isChecked())
                    Season="4";
                else Season="All";

      //          if (checkUpdate()) {
            //        getActivity().setTitle("UpGrade FlyLog");
                boolean connectivity = checkConnectivity(Season);


      //          }
       //         else
         //           Toast.makeText(getActivity(),"For UpGraded Systems only",Toast.LENGTH_LONG).show();
          //      Intent intent1 = new Intent(getActivity(), MapsListActivity.class);
            //    startActivity(intent1);

                return true;

            case R.id.navigation:

                Intent intent1 = new Intent(getActivity(), ListTitlesGoToActivity.class);
                startActivity(intent1);

                return true;
            case R.id.sort_search: // Select the Sort item and then search for it
                mYear="0";
                searchfunction("stream");
                return true;
            case R.id.sort_search_qsf: // Select the Sort item and then search for it
                mYear="0";
                searchfunction("qsf");
                return true;
            case R.id.sort_search_notes: // Select the Sort item and then search for it
                mYear="0";
                searchfunction("notes");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doitStream(){
        String whereClause = " title like?";
        String [] whereArgs = new String[] { mLocDescShortName+"%"};
        String order="title ASC,date DESC";


        updateUI(whereClause,whereArgs,order);

    }
    private void doitQSF(){
        String whereClause = " QSF like?";
        String [] whereArgs = new String[] { "%"+mQSFSearch+"%"};
        String order="title ASC,date DESC";


        updateUI(whereClause,whereArgs,order);

    }

    private void doitNotes(){
        String whereClause = " Notes like?";
        String [] whereArgs = new String[] { "%"+mNotesSearch+"%"};
        String order="title ASC,date DESC";


        updateUI(whereClause,whereArgs,order);

    }

    private void searchfunction(String searchtype) {
        //String searchitem="";
        String order="date DESC";
        String whereClause = null;
        String[] whereArgs = null;
        mSpring.setChecked(false);
        mSummer.setChecked(false);
        mFall.setChecked(false);
        mWinter.setChecked(false);
        mTitle.setChecked(false);
        updateUI(whereClause,whereArgs,order);

        final EditText taskEditText = new EditText(getActivity());
        taskEditText.setInputType(InputType.TYPE_CLASS_TEXT );

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        edt.setInputType(InputType.TYPE_CLASS_TEXT );

        if (searchtype == "stream") {
            dialogBuilder.setTitle("Search by Stream Name starting with");
            dialogBuilder.setMessage("Enter text below, ");
            dialogBuilder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //do something with edt.getText().toString();
                    String task1 = String.valueOf(edt.getText());
                    mLocDescShortName = task1;
                    doitStream();
                }
            });
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //pass
                }
            });
            AlertDialog b = dialogBuilder.create();
            b.show();
        } else if (searchtype == "qsf") {
            dialogBuilder.setTitle("Search for QSF info starting with");
            dialogBuilder.setMessage("Enter text below, ");
            dialogBuilder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //do something with edt.getText().toString();
                    String task2 = String.valueOf(edt.getText());
                    mQSFSearch = task2;
                    doitQSF();
                }
            });
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //pass
                }
            });
            AlertDialog b = dialogBuilder.create();
            b.show();
        } else if (searchtype == "notes") {
            dialogBuilder.setTitle("Search for Notes starting with");
            dialogBuilder.setMessage("Enter text below, ");
            dialogBuilder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //do something with edt.getText().toString();
                    String task3 = String.valueOf(edt.getText());
                    mNotesSearch = task3;
                    doitNotes();
                }
            });
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //pass
                }
            });
            AlertDialog b = dialogBuilder.create();
            b.show();
        }

    }

    private boolean checkConnectivity(String Season){
        mSeason=Season;
        boolean found_service = false;
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        String networkType = "";
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                networkType = "WIFI";
                found_service = true;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {

                networkType = "mobile";
                found_service = true;
            }
        }

        if (!found_service) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setTitle("Mobile data")
                    .setMessage("You have No Service or WIFI, your Google maps might not Show data. \n" +
                            "Activating GPS will take 1 - 2 minutes")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(MapsListActivity.newIntent(getActivity(), mSeason));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        }
                    });
            builder1.show();
        } else
            startActivity(MapsListActivity.newIntent(getActivity(), mSeason));

        return found_service;
    }

    private void updateSubtitle(String whereClause,String[] whereArgs, String order){
        FishlogLab fishlogLab = FishlogLab.get(getActivity());
        int fishlogCount = fishlogLab.getFishlogs(whereClause, whereArgs, order).size();

        //    String subtitle = getString(R.string.subtitle_format, fishlogCount);
        String subtitle = getString(R.string.subtitle_format, fishlogsize);

        if (!mSubtitleVisible){
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI(String whereClause,String[] whereArgs, String order){
        FishlogLab fishlogLab = FishlogLab.get(getActivity());

        List<Fishlog> fishlogs = fishlogLab.getFishlogs(whereClause, whereArgs, order);
        fishlogsize = fishlogs.size();
        mQuery="%"+whereClause+"^";
        mArgs=whereArgs;
        arg = Arrays.toString(mArgs);
        mOrder="&"+order;
        if (mAdapter == null) {
            mAdapter = new FishlogAdapter(fishlogs);
            mFishlogRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setFishlogs(fishlogs);   //Filter
            mAdapter.notifyDataSetChanged();
        }

        // the following line is used by the Add New Log view when there is no Logs in the database
        mLayout.setVisibility((fishlogs.size() > 0? View.GONE : View.VISIBLE));

        updateSubtitle(null, null, null);  // Count all entries
    }

    private boolean checkUpdate(){
        FishlogLab fishlogLab = FishlogLab.get(getActivity());
        String checklock=fishlogLab.getlockID();
        mMapUpdate=true;

        if (checklock.equals(""))
                mMapUpdate=false;

        return mMapUpdate;
    }
}
