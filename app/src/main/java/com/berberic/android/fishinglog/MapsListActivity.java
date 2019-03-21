package com.berberic.android.fishinglog;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


//import static android.app.PendingIntent.getActivity;

/**
 * Created by berberic on 12/20/2017.
 */

public class MapsListActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String EXTRA_FISHLOG_ID =
            "com.berberic.android.fishinglog.fishlog_id";

    public static Intent newIntent(Context packageContext, String Lat_Lng){
        Intent intent = new Intent(packageContext, MapsListActivity.class);
        intent.putExtra(EXTRA_FISHLOG_ID, Lat_Lng);
        return intent;
    }

    // private UUID mUUID;
    private String mSeason;

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        boolean permission= false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission=checkLocationPermission();
        }

        if (permission) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            mSeason = getIntent().getStringExtra(EXTRA_FISHLOG_ID);
            System.out.print("");
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }
    private void showAlert() {
        final android.support.v7.app.AlertDialog.Builder dialog =
                new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    @Override
    public void onConnected(Bundle bundle) {

        if (!checkLocation())
            return;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

// Get all the Location LAt and Lng data
//   FishlogLab fishlogLab = FishlogLab.get(getActivity());
        FishlogLab fishlogLab = FishlogLab.get(this);
        String whereClause = "partners=?";
        String[] whereArgs = new String[] {mSeason};

        if (mSeason.equals("All")) {
            whereClause = null;
            whereArgs = null;
        }

        // Get the Access Location info
        List<String> fishlogslocdesc = fishlogLab.getFishlogs_LocDesc(whereClause, whereArgs, null);
      // fishlogLab.getFishlogs();

        // Get the Title info
        List<String> fishlogstitle = fishlogLab.getFishlogs_Title(whereClause, whereArgs, null);

        double lat, lng;
        String locDesc,notes,locName,locNameLat;
        //       List<LatLng> points = new ArrayList<>();
        ArrayList<LatLng> locations = null;
        ArrayList<String> marks = null;

        locations = new ArrayList();
        marks = new ArrayList<>();

        if (fishlogslocdesc.size() > 0){

        for ( int j=0; j<fishlogslocdesc .size(); j++ ) {
            String mark = fishlogslocdesc.get(j);
            System.out.println("element " + j + ": " + fishlogslocdesc.get(j));
            mark = mark.replaceAll("\n", " ");
            String[] words = mark.split(" ");
            String markName = "";
            int counter = 0;
            for (int t = 0; t < words.length; t++) {
                String text = words[t];
                if ((!words[t].contains("LNG") && (words[t] != " ") && (!words[t].contains("LAT"))
                        && text.length() != 0)) {
                    if (words[t].contains("(")) {
                        if (words[t].contains(")"))
                            continue;
                        else {
                            while (!words[t].contains(")") && (t < words.length - 1)) {
                                t++;
                            }
                        }
                    } else {
                        // Show Access location words
                //        markName = markName + words[t] + " ";
                        // Show Title
                        markName = fishlogstitle.get(j);
                    }
                }

                if (words[t].startsWith("\nLAT")) {
                    counter++;
                    String LATITUTe = words[t].substring(5);
                    String LONGITUDE = words[t + 1].substring(4);
                    locations.add(new LatLng(Double.parseDouble(LATITUTe), Double.parseDouble(LONGITUDE)));
                    if (markName == "")
                        markName = "Marker " + counter;
                    marks.add(markName);
                    markName = "";
                    t++;
                } else if (words[t].startsWith("LAT")) { // need this for imported data
                    counter++;
                    String LATITUTe = words[t].substring(4);
                    String LONGITUDE = words[t + 1].substring(4);
                    locations.add(new LatLng(Double.parseDouble(LATITUTe), Double.parseDouble(LONGITUDE)));
                    if (markName == "")
                        markName = "Marker " + counter;
                    marks.add(markName);
                    markName = "";
                    t++;
                }
            }
        }
        }
        locNameLat="";
     //   lat = Double.parseDouble(locNameLat.substring(4,locNameLat.indexOf("LNG")-1));
     //   lng = Double.parseDouble(locNameLat.substring(locNameLat.indexOf("LNG")+4));

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
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(location.getLatitude(), location.getLongitude()))
                .radius(100));
        settings.setMapToolbarEnabled(true);  // what does this do?
        

        settings.setZoomControlsEnabled(true);
        settings.setCompassEnabled( true );
        settings.setMyLocationButtonEnabled( true );
        settings.setRotateGesturesEnabled( true );
        settings.setScrollGesturesEnabled( true );
        settings.setTiltGesturesEnabled( true );
        settings.setZoomGesturesEnabled( true );

        if (locations != null){
            for (int x=0;x<locations.size();x++){
                mMap.addMarker(new MarkerOptions().position(locations.get(x))
                        .title(marks.get(x)).icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            }
        }

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(8));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
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
}
