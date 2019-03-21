package com.berberic.android.fishinglog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by berberic on 2/17/2018.
 */

// This is from the Fishlog Details page

public class LocationFragment extends Fragment {
    private static final String ARG_FISHLOG_ID = "fishlog_id";

    private Fishlog mFishlog;
    private ListView listview;
    private ArrayAdapter<String> adapter;
    ArrayList<String> Target;


    public static LocationFragment newInstance(UUID fishlogId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FISHLOG_ID, fishlogId);

        LocationFragment fragment = new LocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        UUID fishlogId = (UUID) getArguments().getSerializable(ARG_FISHLOG_ID);

        mFishlog = FishlogLab.get(getActivity()).getFishlog(fishlogId);

        UUID mUUID = mFishlog.getId();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_main_list, container, false);

        FishlogLab fishlogLab = FishlogLab.get(getActivity());

        String currenttitle = mFishlog.getTitle();

        if (currenttitle.equals(null) || currenttitle.equals("")){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setTitle("No Stream names")
                    .setMessage("You have no Location Descriptions for this Stream Name. Do you have a Stream Name?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = FishlogPagerActivity.newIntent(getActivity(),mFishlog.getId(),"%"+null+"^",null,"&date DESC");
                            startActivity(intent);
                        }

                    })
                    //      .setNegativeButton("No", null)
                    .show();
        }
        List<Fishlog> fishlogs = fishlogLab.getFishlogs(null, null, null);
        int numberoftitles= fishlogs.size();
        ArrayList<String> titles = new ArrayList<>();
        for (int i =0;i<numberoftitles;i++) {

            String alltitles=fishlogs.get(i).getTitle().toString();
            if (currenttitle.equals(alltitles)) {

                titles.add(fishlogs.get(i).getLocDesc());
            }

        }


        listview = (ListView) v.findViewById(R.id.list_view);

        Target = new ArrayList<String>();
        Target.addAll(titles);

        Collections.sort(Target);
        // save list in hashset to remove duplicates
        Set<String> hs = new HashSet<>();
        hs.addAll(Target);

        Target.clear();
        Target.addAll(hs);
        Collections.sort(Target);

     //   Target.add(0,"Current Access points for "+currenttitle);
      //  Target.remove(1);


        adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, R.id.listText, Target);
        listview.setAdapter(adapter);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getActivity(),
                        "Click ListItem Number " + Target.get(position), Toast.LENGTH_LONG)
                        .show();

                mFishlog.setLocDesc(Target.get(position));
                Toast.makeText(getActivity(),
                        "Click ListItem Number " + Target.get(position), Toast.LENGTH_LONG)
                        .show();

            //    String title = mFishlog.getLocDesc();
             //   System.out.print(title);

                //     EditText mTitleField = (EditText) v2.findViewById(R.id.fish_log_title);

                //     mTitleField.setText(title);

                FishlogLab.get(getActivity()).updateFishlog(mFishlog);
                Intent intent = FishlogPagerActivity.newIntent(getActivity(),mFishlog.getId(),"%"+null+"^",null,"&date DESC");
                startActivity(intent);
            }
        });



        return v;

    }
 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
*/

    //here we maintain our products in various departments
    private void addProduct(String product){

        Target.add(product);
        Collections.sort(Target);
        // save list in hashset to remove duplicates
        Set<String> hs = new HashSet<>();
        hs.addAll(Target);

        Target.clear();
        Target.addAll(hs);
        Collections.sort(Target);
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, R.id.listText, Target);
        listview.setAdapter(adapter);
        //     listview.setOnItemClickListener(new LocationActivity.ListClickHandler());

    }
}

