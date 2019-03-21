package com.berberic.android.fishinglog;

/**
 * Created by berberic on 5/4/18.
 */

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

//This is from the Navigation - GoTO menu from the Fishlog List page

public class ListTitlesGoToFragment extends Fragment {
    private static final String ARG_FISHLOG_ID = "fishlog_id";

    private Fishlog mFishlog;
    private ListView listview;
    private ArrayAdapter<String> adapter;
    ArrayList<String> Target;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   //     mFishlog = FishlogLab.get(getActivity()).getFishlogTableID();
   //     mFishlog = FishlogLab.get(getActivity()).getFishlogTableID();


    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_main_list, container, false);
        final View v2 = inflater.inflate(R.layout.fragment_fishlog, container, false);



        FishlogLab fishlogLab = FishlogLab.get(getActivity());
        List<Fishlog> fishlogs = fishlogLab.getFishlogs(null, null, null);
        int numberoftitles= fishlogs.size();
        ArrayList<String> titles = new ArrayList<>();
        for (int i =0;i<numberoftitles;i++) {
            titles.add(fishlogs.get(i).getTitle().toString());
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

        if (Target.contains("Home"))
            Target.add(0,"Home");

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, R.id.listText, Target);
        listview.setAdapter(adapter);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String title = Target.get(position);

         //       mFishlog.setTitle(Target.get(position));
                Toast.makeText(getActivity(),
                        "Click ListItem Number " + Target.get(position), Toast.LENGTH_LONG)
                        .show();
        //        String title = mFishlog.getTitle();
                System.out.print(title);

                //     EditText mTitleField = (EditText) v2.findViewById(R.id.fish_log_title);

                //     mTitleField.setText(title);
                String Query="%"+null+"^"+null+"&date DESC";

     //           FishlogLab.get(getActivity()).updateFishlog(mFishlog);
                //   INSERT Code HERE to find all Locations
                startActivity(LocationGoToActivity.newIntent(getActivity(), title));
                //     Intent intent1 = LocationActivity.newIntent(getActivity(),mFishlog.getId());
                //     startActivity(intent1);
                getActivity().finish();

     //           Intent intent = FishlogPagerActivity.newIntent(getActivity(),mFishlog.getId(),"%"+null+"^",null,"&date DESC");
     //           startActivity(intent);

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

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }


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
