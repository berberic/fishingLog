package com.berberic.android.fishinglog;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by berberic on 5/4/18.
 */

public class LocationGoToActivity extends SingleFragmentActivity {
    private static final int REQUEST_ERROR = 0;
    private static final String EXTRA_FISHLOG_ID =
            "com.berberic.android.fishinglog.fishlog_id";

    public static Intent newIntent(Context packageContext, String title){
        Intent intent = new Intent(packageContext, LocationGoToActivity.class);
        intent.putExtra(EXTRA_FISHLOG_ID, title);
        return intent;
    }


    @Override
    protected Fragment createFragment(){
        //  return new FishlogFragment();
   //     UUID fishlogId = (UUID) getIntent()
   //             .getSerializableExtra(EXTRA_FISHLOG_ID);
   //     return LocationFragment.newInstance(fishlogId);
        String title = (String) getIntent().getCharSequenceExtra(EXTRA_FISHLOG_ID);
        return  LocationGoToFragment.newInstance(title);
        //return new FishlogListFragment();

    }

}
