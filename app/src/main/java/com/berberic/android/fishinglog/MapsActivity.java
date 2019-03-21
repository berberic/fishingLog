package com.berberic.android.fishinglog;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String EXTRA_FISHLOG_ID =
            "com.berberic.android.fishinglog.fishlog_id";

    public static Intent newIntent(Context packageContext, String Lat_Lng){
        Intent intent = new Intent(packageContext, MapsActivity.class);
        intent.putExtra(EXTRA_FISHLOG_ID, Lat_Lng);
        return intent;
    }

   // private UUID mUUID;
    private String mUUID;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mUUID = getIntent().getStringExtra(EXTRA_FISHLOG_ID);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_maps, menu);
        super.onCreateOptionsMenu(menu);
   //     super.onCreateOptionsMenu(menu, inflater);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        // mUUID is the data passed to this Activity
        double lat, lng;
        String locDesc,notes,locName,locNameLat;
        //       List<LatLng> points = new ArrayList<>();
        ArrayList<LatLng> locations = null;
        ArrayList<String> marks = null;
        mUUID=mUUID.substring(0, mUUID.indexOf("&*"));  //After &* is NORMAL
        if (mUUID.contains("LAT")){
            locName = mUUID.substring(0,mUUID.lastIndexOf("LAT:")-1);
            locNameLat = mUUID.substring(mUUID.lastIndexOf("LAT:"));
            if (mUUID.contains("^")) {
                locations = new ArrayList();
                marks = new ArrayList<>();
                notes = mUUID.substring(0, mUUID.indexOf("^") - 1); //
                //   notes = mUUID.substring(0, mUUID.indexOf("^"));
                locName = mUUID.substring(mUUID.indexOf("^")+1,mUUID.lastIndexOf("LAT:")-1);
                // count how many LATs are in notes

                notes=notes.replaceAll("\n"," ");

                String [] words = notes.split(" ");
                String markName= "";
                int counter=0;
                for (int t = 0;t <words.length;t++) {
                    String text = words[t];
                    if (!words[t].contains("LNG") && words[t]!=" " && !words[t].contains("LAT") &&
                            text.length()!=0 ) {
                        if (words[t].contains("(")){
                            if (words[t].contains(")"))
                                continue;
                            else {
                                while (!words[t].contains(")") && (t <words.length -1)) {
                                    t++;
                                }
                            }
                        }
                        else
                            markName = markName + words[t] + " ";
                    }
                    if (words [t].startsWith("\nLAT")){
                        counter++;
                        String LATITUTe = words[t].substring(5);
                        boolean checkit = isNumeric(LATITUTe);
                        if (!checkit) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                            builder1.setTitle("Bad data")
                                    .setMessage("Your Notes LAT data is bad. It is not a number, Please check it and fix")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }

                                    })
                                    //      .setNegativeButton("No", null)
                                    .show();
                            return;
                        }
                        String LONGITUDE = words[t+1].substring(4);
                        checkit = isNumeric(LONGITUDE);
                        if (!checkit){
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                            builder1.setTitle("Bad data")
                                    .setMessage("Your Notes LNG data is bad. It is not a number, Please check it and fix")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }

                                    })
                                    //      .setNegativeButton("No", null)
                                    .show();
                            return;
                        }
                        locations.add(new LatLng(Double.parseDouble(LATITUTe), Double.parseDouble(LONGITUDE)));
                        if (markName=="")
                            markName="Marker "+counter;
                        marks.add(markName);
                        markName="";
                        t++;
                    } else if (words [t].startsWith("LAT")){ // need this for imported data
                        counter++;
                        String LATITUTe = words[t].substring(4);
                        boolean checkit = isNumeric(LATITUTe);
                        if (!checkit) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                            builder1.setTitle("Bad data")
                                    .setMessage("Your Notes LAT data is bad. It is not a number, Please check it and fix")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }

                                    })
                                    //      .setNegativeButton("No", null)
                                    .show();
                            return;
                        }
                        String LONGITUDE = words[t+1].substring(4);
                        checkit = isNumeric(LONGITUDE);
                        if (!checkit){
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                            builder1.setTitle("Bad data")
                                    .setMessage("Your Notes LNG data is bad. It is not a number, please check it and fix")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }

                                    })
                                    //      .setNegativeButton("No", null)
                                    .show();
                            return;
                        }
                        locations.add(new LatLng(Double.parseDouble(LATITUTe), Double.parseDouble(LONGITUDE)));
                        if (markName=="")
                            markName="Marker "+counter;
                        marks.add(markName);
                        markName="";
                        t++;
                    }
                }
            }

            boolean found_service = false;
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            String networkType = "";
            if (info != null) {
                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    networkType = "WIFI";
                    found_service = true;
                } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {

                    networkType = "mobile";
                    found_service = true;
                }
            }

            if (!found_service) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("Mobile data")
                        .setMessage("If you have No Service or WIFI, your Google maps might not Show data")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }

                        })
                        //      .setNegativeButton("No", null)
                        .show();
            }

            boolean checkit = isNumeric(locNameLat.substring(4,locNameLat.indexOf("LNG")-1));
            if (checkit)
                lat = Double.parseDouble(locNameLat.substring(4,locNameLat.indexOf("LNG")-1));
            else {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
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
            checkit = isNumeric(locNameLat.substring(locNameLat.indexOf("LNG")+4));
            if (checkit)
                lng = Double.parseDouble(locNameLat.substring(locNameLat.indexOf("LNG")+4));
            else {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
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

            UiSettings settings = mMap.getUiSettings();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);

            //    PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            //     options.add(new LatLng(lat, lng));
            //    Polyline polyline = mMap.addPolyline(options);

            mMap.addPolyline(new PolylineOptions().width(5)
                    .geodesic(true).add(new LatLng(lat,lng)));


            //    mMap.addCircle(new CircleOptions()
            //           .center(new LatLng(lat, lng))
            //           .radius(50));



            //    mMap.addTileOverlay();
            settings.setMapToolbarEnabled(true);  // what does this do?
            settings.setZoomControlsEnabled(true);
            settings.setCompassEnabled( true );
            settings.setMyLocationButtonEnabled( true );
            settings.setRotateGesturesEnabled( true );
            settings.setScrollGesturesEnabled( true );
            settings.setTiltGesturesEnabled( true );
            settings.setZoomGesturesEnabled( true );

            // Add a marker to center the map
            LatLng choice = new LatLng(lat, lng);

            mMap.addMarker(new MarkerOptions().position(choice)
                    .title(locName));
            //Add additional markers (red)
            if (locations != null){
                for (int x=0;x<locations.size();x++){
                    mMap.addMarker(new MarkerOptions().position(locations.get(x))
                            .title(marks.get(x)).icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }
            }
            float zoomLevel = 16.0f; //This goes up to 21
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(choice, zoomLevel));

            //     mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            //      mMap.moveCamera(CameraUpdateFactory.);
        }  else {

            return;
        }
    }

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

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
