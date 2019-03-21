package com.berberic.android.fishinglog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

/**
 * Created by berberic on 5/4/18.
 */

public class LocationGoToFragment extends Fragment {
    private static final String ARG_FISHLOG_ID = "fishlog_id";

    private Fishlog mFishlog;
    private ListView listview;
    private ArrayAdapter<String> adapter;
    ArrayList<String> Target;
    String mtitle;


    public static LocationGoToFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FISHLOG_ID, title);

        LocationGoToFragment fragment = new LocationGoToFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        mtitle = (String) getArguments().getSerializable(ARG_FISHLOG_ID);

    //    mFishlog = FishlogLab.get(getActivity()).getFishlog(fishlogId);

    //    UUID mUUID = mFishlog.getId();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_main_list, container, false);

        FishlogLab fishlogLab = FishlogLab.get(getActivity());

      //  String currenttitle = mFishlog.getTitle();

        String currenttitle = mtitle;

        if (currenttitle.equals(null) || currenttitle.equals("")){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setTitle("No Stream names")
                    .setMessage("You have no Location Descriptions for this Stream Name")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String Query="%"+null+"^"+null+"&date DESC";
                            try {
                                Intent intent = FishlogPagerActivity.newIntent(getActivity(), mFishlog.getId(), "%" + null + "^", null, "&date DESC");
                                startActivity(intent);
                            }
                            catch(NullPointerException e)
                            {
                                System.out.print("Caught NullPointerException on getID");
                            }
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



        if (Target.size() == 1){
            Toast.makeText(getActivity(),
                    "Go to " + Target.get(0), Toast.LENGTH_LONG)
                    .show();

            //           mFishlog.setLocDesc(Target.get(position));

            String title = Target.get(0);
            System.out.print(title);
            String data = title;

            if (title.contains("LAT:")) {

                String locName = data.substring(data.indexOf("^")+1,data.lastIndexOf("LAT:")-1);
                String locNameLat = data.substring(data.lastIndexOf("LAT:"));
                locNameLat=locNameLat.replaceAll("\n"," ");

                String [] words = locNameLat.split(" ");
                String LATITUTe = words[0].substring(4);
                String LONGITUDE = words[1].substring(4);

                    /*    Need check for correct LAT and LNG data */

                Double lat =Double.parseDouble(LATITUTe);
                Double lng =Double.parseDouble(LONGITUDE);
                // Create a Uri from an intent string. Use the result to create an Intent.
                //     Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+lat.toString()+","+lng.toString()+"");
                //geo:0,0?q=latitude,longitude(label) // this adds a destination marker
                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+lat.toString()+","+lng.toString()+"("+locName+")");


// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
// Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

// Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);
            } else if (!data.contains("LAT")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setTitle("No Stream names Location Access Point")
                            .setMessage("You have no Location Descriptions LAT/LNG for this Stream Name")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getActivity().finish();
                                }

                            })
                            //      .setNegativeButton("No", null)
                            .show();

                return v;
            }


        } else {


            adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, R.id.listText, Target);
            listview.setAdapter(adapter);


            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Toast.makeText(getActivity(),
                            "Go to " + Target.get(position), Toast.LENGTH_LONG)
                            .show();

                    //           mFishlog.setLocDesc(Target.get(position));

                    String title = Target.get(position);
                    System.out.print(title);
                    String data = title;

                    if (title.contains("LAT:")) {

                        String locName = data.substring(data.indexOf("^") + 1, data.lastIndexOf("LAT:") - 1);
                        String locNameLat = data.substring(data.lastIndexOf("LAT:"));
                        locNameLat = locNameLat.replaceAll("\n", " ");

                        String[] words = locNameLat.split(" ");
                        String LATITUTe = words[0].substring(4);
                        String LONGITUDE = words[1].substring(4);
                    
                    /*    Need check for correct LAT and LNG data */
                        boolean checkit = isNumeric(LATITUTe);
                        if (!checkit) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                            builder1.setTitle("Bad data")
                                    .setMessage("Your Location Access LAT data is bad. It is not a number, Please check it and fix")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }

                                    })
                                    //      .setNegativeButton("No", null)
                                    .show();
                            return;
                        }

                        Double lat = Double.parseDouble(LATITUTe);

                        checkit = isNumeric(LONGITUDE);
                        if (!checkit) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                            builder1.setTitle("Bad data")
                                    .setMessage("Your Location Access LNG data is bad. It is not a number, Please check it and fix")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }

                                    })
                                    //      .setNegativeButton("No", null)
                                    .show();
                            return;
                        }


                        Double lng = Double.parseDouble(LONGITUDE);

                        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + lat.toString() + "," + lng.toString() + "(" + locName + ")");


// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
// Make the Intent explicit by setting the Google Maps package
                        mapIntent.setPackage("com.google.android.apps.maps");

// Attempt to start an activity that can handle the Intent
                        startActivity(mapIntent);
                    }
                    else if (!data.contains("LAT")) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                        builder1.setTitle("No Stream names Location Access Point")
                                .setMessage("You have no Location Descriptions LAT/LNG for this Stream Name")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getActivity().finish();
                                    }

                                })
                                //      .setNegativeButton("No", null)
                                .show();
                    }

                }
            });

        }

        return v;

    }
 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
*/

    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
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
