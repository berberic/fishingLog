package com.berberic.android.fishinglog;

import android.support.v4.app.Fragment;

/**
 * Created by berberic on 5/4/18.
 */

public class ListTitlesGoToActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new ListTitlesGoToFragment();
    }
}
