package com.berberic.android.fishinglog;

import android.support.v4.app.Fragment;

/**
 * Created by berberic on 10/15/2017.
 */

public class FishlogImportExportActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new FishlogImportExportFragment();
    }
}
