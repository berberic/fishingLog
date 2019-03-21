package com.berberic.android.fishinglog;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

/**
 * Created by berberic on 2/16/2018.
 */

public class ListTitlesActivity extends SingleFragmentActivity {

    private static final int REQUEST_ERROR = 0;
    private static final String EXTRA_FISHLOG_ID =
            "com.berberic.android.fishinglog.fishlog_id";

    public static Intent newIntent(Context packageContext, UUID fishlogId){
        Intent intent = new Intent(packageContext, ListTitlesActivity.class);
        intent.putExtra(EXTRA_FISHLOG_ID, fishlogId);
        return intent;
    }


    @Override
    protected Fragment createFragment(){
        //  return new FishlogFragment();
        UUID fishlogId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_FISHLOG_ID);
        return ListTitlesFragment.newInstance(fishlogId);
    }
}


