package com.berberic.android.fishinglog;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by berberic on 9/21/2017.
 */

public class FishlogHelpFragment extends Fragment {

    TextView mTopTextView;
    TextView mBottomTextView;
    TextView mHelpTextView;

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
        View v = inflater.inflate(R.layout.fragment_help_3,container,false);

        mTopTextView = (TextView) v.findViewById(R.id.textView1);
        mBottomTextView = (TextView) v.findViewById(R.id.textView2);
        mHelpTextView = (TextView) v.findViewById(R.id.textView);
        mHelpTextView.setText(R.string.help_intro);
        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_help_2, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.intro:
                mTopTextView.setText("");
                mTopTextView.setText(R.string.intro);
                mBottomTextView.setText("");
                mBottomTextView.setText(R.string.intro2);
                return true;
            case R.id.imp_exp:
                mTopTextView.setText("");
                mTopTextView.setText(R.string.import_export);
                mBottomTextView.setText("");
                mBottomTextView.setText(R.string.importexport);
                return true;
            case R.id.list:
                mTopTextView.setText("");
                mTopTextView.setText(R.string.list);
                mBottomTextView.setText("");
                mBottomTextView.setText(R.string.list2);
                return true;
            case R.id.map_view:
                mTopTextView.setText("");
                mTopTextView.setText(R.string.map_view);
                mBottomTextView.setText("");
                mBottomTextView.setText(R.string.map_view2);
                return true;
            case R.id.navigation_map:
                mTopTextView.setText("");
                mTopTextView.setText(R.string.navigation);
                mBottomTextView.setText("");
                mBottomTextView.setText(R.string.navigation2);
                return true;
            case R.id.details:
                mTopTextView.setText("");
                mTopTextView.setText(R.string.details);
                mBottomTextView.setText("");
                mBottomTextView.setText(R.string.details2);
                return true;
            case R.id.view_detail_map:
                mTopTextView.setText("");
                mTopTextView.setText(R.string.view);
                mBottomTextView.setText("");
                mBottomTextView.setText(R.string.view2);
                return true;
            case R.id.loc_desc_map:
             //   mTopTextView.setText("");
                mTopTextView.setText(R.string.loc_desc);
             //   mBottomTextView.setText("");
                mBottomTextView.setText(R.string.loc_desc2);
                return true;
            case R.id.notes_map:
                mTopTextView.setText("");
                mTopTextView.setText(R.string.notes);
                mBottomTextView.setText("");
                mBottomTextView.setText(R.string.notes2);
                return true;
            case R.id.map_points:
                mTopTextView.setText("");
                mTopTextView.setText(R.string.map_points);
                mBottomTextView.setText("");
                mBottomTextView.setText(R.string.map_points2);
                return true;
            case R.id.connectivity:
                mTopTextView.setText("");
                mTopTextView.setText(R.string.connectivity);
                mBottomTextView.setText("");
                mBottomTextView.setText(R.string.connectivity2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
