package com.berberic.android.fishinglog;

import android.support.v4.app.Fragment;

// * Copyright (C) 2017 Carl Berberich

public class IntroActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new Introfragment();
    }
}
