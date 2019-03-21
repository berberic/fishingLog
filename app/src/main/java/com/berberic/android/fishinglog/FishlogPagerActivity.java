package com.berberic.android.fishinglog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 * * Copyright (C) 2017 Carl Berberich
 * Created by berberic on 8/27/2017.
 */

public class FishlogPagerActivity extends AppCompatActivity {
    private static final String EXTRA_FISHLOG_ID =
            "com.berberic.android.fishinglog.fishlog_id";

    private ViewPager mViewPager;
    private List<Fishlog> mFishlogs;



  //  public static Intent newIntent(Context packageContext, UUID fishlogId){
    public static Intent newIntent(Context packageContext, UUID fishlogId, String clause,String args, String order){
        Intent intent = new Intent(packageContext, FishlogPagerActivity.class);
        intent.putExtra(EXTRA_FISHLOG_ID, fishlogId+clause+args+order);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fishlog_pager);

       String  fishlogId1 =  getIntent().getStringExtra(EXTRA_FISHLOG_ID);
       UUID fishlogId = UUID.fromString(fishlogId1.substring(0,fishlogId1.indexOf("%")));


        String WhereClause = fishlogId1.substring(fishlogId1.indexOf("%")+1,fishlogId1.indexOf("^"));
        String Arg = fishlogId1.substring(fishlogId1.indexOf("^")+1,fishlogId1.indexOf("&"));

            if (!Arg.equals("null"))
                Arg = Arg.substring(1,Arg.length()-1);

        String Order =fishlogId1.substring(fishlogId1.indexOf("&")+1);

        mViewPager = (ViewPager) findViewById(R.id.fishlog_view_pager);
        if (Arg.equals("null"))
            mFishlogs = FishlogLab.get(this).getFishlogs(null,null,Order);
        else
            mFishlogs = FishlogLab.get(this).getFishlogs(WhereClause,new String [] {Arg},Order);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Fishlog fishlog = mFishlogs.get(position);
                return FishlogFragment.newInstance(fishlog.getId());
            }

            @Override
            public int getCount() {
                return mFishlogs.size();
            }
        });

        for (int i = 0; i < mFishlogs.size();i++){
            if (mFishlogs.get(i).getId().equals(fishlogId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
