package com.berberic.android.fishinglog;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * * Copyright (C) 2017 Carl Berberich
 * Created by berberic on 8/27/2017.
 */

public class FishlogListActivity extends SingleFragmentActivity {
    private static final String EXTRA_MESSAGE=
            "com.berberic.android.fishinglog.order_id";

    public static Intent newIntent(Context packageContext, String orderId){
        Intent intent= new Intent(packageContext, FishlogListActivity.class);
        intent.putExtra(EXTRA_MESSAGE, orderId);
        return intent;
    }

    @Override
    protected Fragment createFragment(){

        //return new FishlogListFragment();
        String orderId = (String) getIntent().getCharSequenceExtra(EXTRA_MESSAGE);
        return  FishlogListFragment.newInstance(orderId);
    }
}

