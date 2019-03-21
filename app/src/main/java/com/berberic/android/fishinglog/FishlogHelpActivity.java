package com.berberic.android.fishinglog;

/**
 * * Copyright (C) 2017 Carl Berberich
 * Created by berberic on 9/13/2017.
 */

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class FishlogHelpActivity extends AppCompatActivity {
    private Button mFishlogTable;
    private SQLiteDatabase mDatabase;
    private Fishlog mFishlog;
    private TextView mTextView;
    private ScrollView mScrollView;
    private Button mExportButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_help_container);

        if (fragment == null){
            fragment = new FishlogHelpFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_help_container,fragment)
                    .commit();
        }

    }

}
