package com.berberic.android.fishinglog;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * * Copyright (C) 2017 Carl Berberich
 * Created by berberic on 8/27/2017.
 */

public class FishlogFragment extends Fragment {

    private Uri uri;
 //   public double mLat;
 //   public double mLng;
    private boolean mHide= false;
    private boolean mPhoto_send = false;
    private static final String ARG_FISHLOG_ID = "fishlog_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_IMAGE = "image";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static String task = "";
    private String pictureFilePath;


    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    LocationManager locationManager;
    double longitudeGPS, latitudeGPS;
    private Location location;
//    TextView longitudeValueGPS, latitudeValueGPS;

    private Fishlog mFishlog;
    private EditText mTitleField;
    private EditText mPartnersField;
    private EditText mQSFField;

    public String FINDLatLng;

    private EditText mLocationDescField;
    private Button mDateButton;
    private CheckBox mHideLocation;
    private CheckBox mSelectVirtualClient;
    private EditText mNotesField;
    private EditText mWaterTemp;
    private Button mReportButton;
    private TextView mSeason;  // from RECIPIENT
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private ImageView mImageView;
    private File mPhotoFile;
    private CheckBox mDbsend;
    private CheckBox mPhotosend;
    private Button mExportDb;
    private Button mGPS1;  // For Location Description Data
    private Button mGPS2;  // For Hatch Notes
    private Button mSave_Photo;
 //   private RadioButton mRadioButtonSat;
  //  private RadioButton mRadioButtonTerr;
 //   private RadioButton mRadioButtonHyb;
    private UUID fishlogId;
    private GoogleApiClient googleApiClient;
//    private GoogleMap mMap;


    public static FishlogFragment newInstance(UUID fishlogId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FISHLOG_ID, fishlogId);

        FishlogFragment fragment = new FishlogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        StrictMode.VmPolicy.Builder builder1 = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder1.build());
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //mFishlog = new Fishlog();
        //    UUID fishlogId = (UUID) getActivity().getIntent()
        //            .getSerializableExtra(FishlogActivity.EXTRA_FISHLOG_ID);

        fishlogId = (UUID) getArguments().getSerializable(ARG_FISHLOG_ID);

        mFishlog = FishlogLab.get(getActivity()).getFishlog(fishlogId);
        mPhotoFile = FishlogLab.get(getActivity()).getPhotoFile(mFishlog);

        locationManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);

     /*   getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
            }
        });
*/
        if (googleApiClient == null) {

            googleApiClient = new GoogleApiClient.Builder(getActivity()) .addApi(LocationServices.API) .build();
            googleApiClient.connect();


            //         googleApiClient = new GoogleApiClient.Builder(getActivity())
            //                .addApi(LocationServices.API).addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) getActivity()).build();
            //        googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(getActivity(), 1000);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fishlog, container, false);

    //    mRadioButtonSat = (RadioButton) v.findViewById(R.id.radioButtonSat);
    //    mRadioButtonTerr= (RadioButton) v.findViewById(R.id.radioButtonTerr);
    //    mRadioButtonHyb = (RadioButton) v.findViewById(R.id.radioButtonHybr);

        mSeason = (TextView) v.findViewById(R.id.fishlog_season);
        String season = updateSeason();
        mSeason.setText(season);

    /*    mSeason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFishlog.setReceipient(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
*/
        mTitleField = (EditText) v.findViewById(R.id.fish_log_title);
        if (mFishlog.getTitle() == null)
            mFishlog.setTitle("");
        mTitleField.setText(mFishlog.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFishlog.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        mDateButton = (Button) v.findViewById(R.id.fishlog_date);
        updateDate();

        //mDateButton.setEnabled(false);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                //    DatePickerFragment dialog = new DatePickerFragment();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mFishlog.getDate());
                dialog.setTargetFragment(FishlogFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

     /*   mPartnersField = (EditText) v.findViewById(R.id.partners_title);
        if (mFishlog.getPartners() == null)
            mFishlog.setPartners("");
        mPartnersField.setText(mFishlog.getPartners());
        mPartnersField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //         mFishlog.setTitle(s.toString());
                mFishlog.setPartners(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
*/
        mQSFField = (EditText) v.findViewById(R.id.qsf_title);
        if (mFishlog.getQSF() == null)
            mFishlog.setQSF("");
        mQSFField.setText(mFishlog.getQSF());
        mQSFField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFishlog.setQSF(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mPhotosend = (CheckBox) v.findViewById(R.id.fishlog_send_photo);
        mPhotosend.setChecked(false);   //Checkbox
        mPhoto_send=false;
        //     mHideLocation.setChecked(mFishlog.isHideloc());
        mPhotosend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //              mFishlog.setHideloc(isChecked);
                mPhoto_send=true;
            }
        });


        mHideLocation = (CheckBox) v.findViewById(R.id.fishlog_send);
        //    mFishlog.setHideloc(false);
        mHideLocation.setChecked(false);   //Checkbox
        mHide=false;
        //     mHideLocation.setChecked(mFishlog.isHideloc());
        mHideLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //              mFishlog.setHideloc(isChecked);
                mHide=true;
            }
        });

        mSelectVirtualClient = (CheckBox)v.findViewById(R.id.fishlog_virtual_client);
        mSelectVirtualClient.setChecked(mFishlog.isSelectVirtual());
        mSelectVirtualClient.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFishlog.setSelectVirtual(isChecked);
            }
        });

        mLocationDescField = (EditText) v.findViewById(R.id.location_desc);
        if (mFishlog.getLocDesc() == null)
            mFishlog.setLocDesc("");
        mLocationDescField.setText(mFishlog.getLocDesc());
        mLocationDescField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //     mFishlog.setTitle(s.toString());
                mFishlog.setLocDesc(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mNotesField = (EditText) v.findViewById(R.id.notes_title);
        if (mFishlog.getNotes() == null)
            mFishlog.setNotes("");
        mNotesField.setText(mFishlog.getNotes());
        mNotesField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //          mFishlog.setTitle(s.toString());
                mFishlog.setNotes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mWaterTemp = (EditText) v.findViewById(R.id.water_temp_title);
        if (mFishlog.getWaterTemp() == null)
            mFishlog.setWaterTemp("");
        mWaterTemp.setText(mFishlog.getWaterTemp());
        mWaterTemp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFishlog.setWaterTemp(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Access Location GPS button
        mGPS1 = (Button) v.findViewById(R.id.locationControllerGPS);
        mGPS1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){

                boolean connectivity = checkConnectivityLoc(); //Sets LAT/LAN if no connectivity

                if (connectivity) {
                    startActivity(MapsFind1Activity.newIntent(getActivity(), mFishlog.getId()));
                    getActivity().finish();
     //               getGPSdata(locationListenerGPS);
                    //           startActivity(MapsFind1Activity.newIntent(getActivity(), mFishlog.getId()));
                    //          getActivity().finish();
                }

            /*    String gpsdata = mLocationDescField.getText().toString();
                if ((!gpsdata.equals("")) ) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("GPS")
                        //    .setMultiChoiceItems(true,)
                            .setMessage("Are you sure you want to add GPS data?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getGPSdata(locationListenerGPS,"locationListenerGPS");
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else {
                    getGPSdata(locationListenerGPS,"locationListenerGPS");
                }
         */   };

        });

        mGPS2 = (Button) v.findViewById(R.id.locationControllerGPS2);
        mGPS2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                String gpsdata = mNotesField.getText().toString();
                boolean connectivity = checkConnectivityNotes(); //Sets LAT/LAN if no connectivity
                if (connectivity) {
                    startActivity(MapsFindNotesActivity.newIntent(getActivity(), mFishlog.getId()));
                    getActivity().finish();
                }
            };

        });

        mReportButton = (Button) v.findViewById(R.id.fishlog_report);
        mReportButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v ){

                int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE
                    );
                }
                Intent i = new Intent(Intent.ACTION_SEND);
                if (mPhotoFile.exists() && mPhoto_send){

                    try {


                        File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        if (!exportDir.exists()) {
                            exportDir.mkdirs();
                        }
                        File file = new File(exportDir, "Photo.jpg");
                        Process p = Runtime.getRuntime().exec("cp -pf "+mPhotoFile+" "+file.getAbsolutePath());
                        i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_TEXT, getFishlogReport(mHide));
                    i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.fishlog_report_subject));
                    i = Intent.createChooser(i,getString(R.string.send_report));


                    startActivity(i);
                        try {
                            Thread.sleep(20000);

                             } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // Kill currently running Activity process
                        // Kill the email process after sending mail
                   //     android.os.Process.killProcess(android.os.Process.myPid());
                        //         boolean db1 = fishlogLab.deleteDatabaseFile(file.toString());

                     //   System.exit(1);
                } else {
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_TEXT, getFishlogReport(mHide));
                    i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.fishlog_report_subject));
                    i = Intent.createChooser(i,getString(R.string.send_report));

                    startActivity(i);

                    //   getDistance();

                    // Kill the email procees after sending mail
               //     android.os.Process.killProcess(android.os.Process.myPid());
               //     System.exit(1);
                }




                //   getDistance();

                // Kill the email procees after sending mail
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);

            }
        });


        mPhotoButton = (ImageButton) v.findViewById(R.id.fishlog_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(captureImage,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mPhotoButton.setEnabled(false);
        }


        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                uri = FileProvider.getUriForFile(getActivity(),
                        "com.berberic.android.fishinglog.fileprovider",
                        mPhotoFile);


                //         i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));



                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);

                Intent galleryIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                galleryIntent.setData(uri);
                getActivity().sendBroadcast(galleryIntent);

            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.fishlog_photo);
        updatePhotoView();

        mPhotoView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                FragmentManager fm = getActivity().getSupportFragmentManager();
                String path = FishlogLab.get(getActivity()).getPhotoFile(mFishlog).getAbsolutePath();
                ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);

            }
        });

        mSave_Photo = (Button) v.findViewById(R.id.save_photo);
        mSave_Photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE
                    );
                }

                if (mPhotoFile.exists()){

                    try {

                        File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);


                        if (!exportDir.exists()) {
                            exportDir.mkdirs();
                        }
                        File file = new File(exportDir, "FLY_"+mFishlog.getId().toString()+".jpg");



                        if(file.exists()) {
                            if (file.delete())
                                Toast.makeText(getActivity(),"Deleting file:"+
                                        "FLY_"+ mFishlog.getId().toString()+".jpg",Toast.LENGTH_LONG).show();
                            else
                                System.out.println("File was not deleted");
                            try {
                                Thread.sleep(2000);
                                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(
                                        new File(exportDir, "FLY_"+mFishlog.getId().toString()+".jpg"))));

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        String copyphoto_cmd = "cp -pf " + mPhotoFile + " " + file.getAbsolutePath();
                        Process p = Runtime.getRuntime().exec(copyphoto_cmd);

                        //         i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    try {
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);

                }



            }
        });


        return v;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mFishlog.setDate(date);
            updateDate();
            updateSeason();

        } else if (requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.berberic.android.fishinglog.fileprovider",
                    mPhotoFile);

            try {
                File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                //     File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }
                File file = new File(exportDir, mFishlog.getId().toString()+".jpg");

                Process p = Runtime.getRuntime().exec("cp -pf " + mPhotoFile + " " + file.getAbsolutePath());


                //         i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));

            } catch (IOException e) {e.printStackTrace();}

            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    //        addPhotoToGallery(uri);
            updatePhotoView();
        }

    }



    private String updateSeason(){

        // Find the Date & determine the Season
        Date fishDate = mFishlog.getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fishDate);

        int month =calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        month++;
        String season= null;
        if ((month == 1) || (month == 2))
            season = "Winter Season";
        else if (( month == 3) && (day < 20))
            season = "Winter Season";
        else if (month == 3)
            season = "Spring Season";
        else if ((month == 4) || (month == 5))
            season = "Spring Season";
        else if (( month == 6) && (day < 21))
            season = "Spring Season";
        else if ( month == 6)
            season = "Summer Season";
        else if ((month == 7) || (month == 8))
            season = "Summer Season";
        else if (( month == 9) && (day < 22))
            season = "Summer Season";
        else if ( month == 9)
            season = "Fall Season";
        else if ((month == 10) || (month == 11))
            season = "Fall Season";

        else if (( month == 12) && (day < 21))
            season = "Fall Season";
        else if ( month == 12)
            season = "Winter Season";
        else
            season="What planet are you from?"+month+" "+day;

        if (season.startsWith("Spring"))
            mFishlog.setPartners("1");
        if (season.startsWith("Summer"))
            mFishlog.setPartners("2");
        if (season.startsWith("Fall"))
            mFishlog.setPartners("3");
        if (season.startsWith("Winter"))
            mFishlog.setPartners("4");

        String MONTH = Integer.toString(month);
        String DAY = Integer.toString(day);
        if (MONTH.length() == 1){
            MONTH="0"+MONTH;
        }
        if (DAY.length()==1)
            DAY = "0"+DAY;

        mFishlog.setReceipient("2017-"+MONTH+"-"+DAY);

     //   mFishlog.setReceipient(season);
     //   String newSeason = mFishlog.getReceipient();
     //   mSeason.setText(newSeason);
        return season;
    }

    private void updateDate() {
        // Shortened version of date
      //  String formattedDate = new SimpleDateFormat("EEEE, MMM d, yyyy").format(mFishlog.getDate());
      //  mDateButton.setText(formattedDate.toString());
        mDateButton.setText(mFishlog.getDate().toString());
    }

    private String getFishlogReport(boolean mHide){

        FishlogLab.get(getActivity()).updateFishlog(mFishlog);
//        String gps = mFishlog.getPartners().replaceAll("\n",":");
   //     String hiddenString = null;
        String location = null;
        String notes = mFishlog.getNotes();
        if (notes == null){
            notes="";
        }

   //     double dist = getDistance();

        if (!mHide){
  //          hiddenString =getString(R.string.fishlog_report_hidden);
            location = "";
            notes="";
   //         gps ="";
        } else {
   //         hiddenString = getString(R.string.fishlog_report_not_hidden);



            location = "\nLocated at:\""+mFishlog.getLocDesc()+"\"\n";
            notes = "\nHatch Notes:\""+ notes+"\"";
       /*     if ((gps == null) || (gps == "")){
                gps ="";
            } else {
                gps = "\n" + gps;
            }
            */
        }
        String dateFormat = "EEE, MMM dd yyyy";
        String dateString = DateFormat.format(dateFormat,mFishlog.getDate()).toString();


        String fishcaught = mFishlog.getQSF().replaceAll("\n",":");


        if (fishcaught == null){
            fishcaught ="0";
        }

        String report1 = location +"Stream Temperature:"+mFishlog.getWaterTemp() +
                "\nQuantity/Fish Species/Fly:\n \"" +fishcaught +"\""+ notes;
        String report = getString(R.string.fishlog_report, mFishlog.getTitle(),
                dateString,report1,"\n The report came from the fishing log of:\n");

        return report;
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        FishlogLab.get(getActivity()).updateFishlog(mFishlog);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

       // inflater.getMenuInflater().inflate(R.menu.fragment_fishlog, menu);
        inflater.inflate(R.menu.fragment_fishlog, menu);
   //     inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        String notes = mNotesField.getText().toString();
        String data =mLocationDescField.getText().toString();
        switch(item.getItemId())
        {
            case R.id.title_list: //Show Selectable List of current Titles (stream Names)
                startActivity(ListTitlesActivity.newIntent(getActivity(), mFishlog.getId()));
                getActivity().finish();
                return true;
            case R.id.loc_list: // Show selectable list of Locations
                startActivity(LocationActivity.newIntent(getActivity(), mFishlog.getId()));
           //     Intent intent1 = LocationActivity.newIntent(getActivity(),mFishlog.getId());
           //     startActivity(intent1);
                getActivity().finish();
                return true;
 /*           case R.id.mapit: // Location Description:Add Map

                boolean connectivity = checkConnectivity();  //Sets LAT/LAN if no connectivity

                if (connectivity) {
                    startActivity(MapsFind1Activity.newIntent(getActivity(), mFishlog.getId()));
                    getActivity().finish();
       //         } else {
             //       getGPSdata(locationListenerGPS);
         //           startActivity(MapsFind1Activity.newIntent(getActivity(), mFishlog.getId()));
          //          getActivity().finish();
                }

                return true;
   */         case R.id.delete_fishlog_action:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Delete Fishing Log Entry")
                    .setMessage("Are you sure you want to delete this activity?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UUID fishlogId = (UUID) getArguments().getSerializable(ARG_FISHLOG_ID);
                            FishlogLab fishlogLab = FishlogLab.get(getActivity());
                            mFishlog = fishlogLab.getFishlog(fishlogId);
                            fishlogLab.deleteFishlog(mFishlog);
                            Intent intent = FishlogListActivity.newIntent(getActivity(),"");
                            getActivity().finish();
                            startActivity(intent);
                    //
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();

                return true;

      /*      case R.id.offline_view:
                if (data.contains("LAT:")) {  //if there is Access Location data

                    String locName = data.substring(data.indexOf("^")+1,data.lastIndexOf("LAT:")-1);
                    String locNameLat = data.substring(data.lastIndexOf("LAT:"));
                    locNameLat=locNameLat.replaceAll("\n"," ");

                    String [] words = locNameLat.split(" ");
                    String LATITUTe = words[0].substring(4);
                    String LONGITUDE = words[1].substring(4);
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
                }

                return true;
  */          case R.id.compass_fishlog_action:  // View MAP

                if (data.contains("LAT:")) {
                    if (notes.contains("LAT")){
                        data = notes+"^"+data;
                    }
                    // do I still need NORMAL?
                data = data+" &*NORMAL";

                startActivity(MapsActivity.newIntent(getActivity(), data));
                }
                else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setTitle("No Lat/Lng data")
                            .setMessage("You have no Location data. Please enter Lat/Lng data first! " +
                                    "You can use the LOC LAT/LNG Button")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }

                            })
                            //      .setNegativeButton("No", null)
                            .show();

            //        startActivity(MapsFind1Activity.newIntent(getActivity(), mFishlog.getId()));

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getDistance(){

        double totaldist=0;
        String distance=null;
        String mUUID=null;

        String notes = mNotesField.getText().toString();
        String data =mLocationDescField.getText().toString();
        if (data.contains("LAT:")) {
            if (notes.contains("LAT")) {
                data = notes + "^" + data;
            }
        }
        double lat, lng, latA,lngA;
        String locDesc,locName,locNameLat;
        //       List<LatLng> points = new ArrayList<>();
        ArrayList<LatLng> locations = null;
        ArrayList<String> marks = null;
        mUUID=data;
        if (mUUID.contains("LAT")) {
            //       locName = mUUID.substring(0, mUUID.lastIndexOf("LAT:") - 1);
            locNameLat = mUUID.substring(mUUID.lastIndexOf("LAT:"));
            lat = Double.parseDouble(locNameLat.substring(4, locNameLat.indexOf("LNG") - 1));
            lng = Double.parseDouble(locNameLat.substring(locNameLat.indexOf("LNG") + 4));
            latA = lat;
            lngA = lng;
            if (mUUID.contains("^")) {
                locations = new ArrayList();
                marks = new ArrayList<>();
                notes = mUUID.substring(0, mUUID.indexOf("^") - 1); //
                //        locName = mUUID.substring(mUUID.indexOf("^") + 1, mUUID.lastIndexOf("LAT:") - 1);
                // count how many LATs are in notes

                notes = notes.replaceAll("\n", " ");

                String[] words = notes.split(" ");
                String markName = "";
                int counter = 0;
                for (int t = 0; t < words.length; t++) {
                    String text = words[t];
                    if (!words[t].contains("LNG") && words[t] != " " && !words[t].contains("LAT") &&
                            text.length() != 0) {
                        if (words[t].contains("(")) {
                            if (words[t].contains(")"))
                                continue;
                            else {
                                while (!words[t].contains(")") && (t < words.length - 1)) {
                                    t++;
                                }
                            }
                        } else
                            markName = markName + words[t] + " ";
                    }
                    if (words[t].startsWith("\nLAT")) {
                        counter++;
                        String LATITUTe = words[t].substring(5);
                        String LONGITUDE = words[t + 1].substring(4);
                        locations.add(new LatLng(Double.parseDouble(LATITUTe), Double.parseDouble(LONGITUDE)));
                        //      location.distanceBetween(lat,lng,Double.parseDouble(LATITUTe),Double.parseDouble(LONGITUDE));
                        t++;

                    } else if (words[t].startsWith("LAT")) { // need this for imported data
                        counter++;
                        String LATITUTe = words[t].substring(4);
                        String LONGITUDE = words[t + 1].substring(4);
                        locations.add(new LatLng(Double.parseDouble(LATITUTe), Double.parseDouble(LONGITUDE)));
                        t++;
                    }
                }
            }


            // Add a marker to center the map
            LatLng choice = new LatLng(lat, lng);

            //     location.distanceBetween(lat,lng,);

            //Add additional markers (red)

            if (locations != null) {
                for (int x = 0; x < locations.size(); x++) {
                    locations.get(x);
                    String latlng = locations.get(x).toString();
                    String lat1 = latlng.substring(latlng.indexOf("(") + 1, latlng.indexOf(","));
                    String lng1 = latlng.substring(latlng.indexOf(",") + 1, latlng.length() - 1);
                    System.out.print(latlng);

                    Location locationA = new Location("point A");

                    if (x == 0) {
                        locationA.setLatitude(lat);
                        locationA.setLongitude(lng);
                    } else {
                        String latlng2 = locations.get(x - 1).toString();
                        int start = latlng2.indexOf("(") + 1;
                        int stop = latlng2.indexOf(",");

                        String lat2 = latlng2.substring(start, stop);
                        String lng2 = latlng2.substring(stop + 1, latlng2.length() - 1);

                        locationA.setLatitude(Double.parseDouble(lat2));
                        locationA.setLongitude(Double.parseDouble(lng2));
                    }

                    Location locationB = new Location("point B");

                    locationB.setLatitude(Double.parseDouble(lat1));
                    locationB.setLongitude(Double.parseDouble(lng1));

                    double distance1 = locationA.distanceTo(locationB);
                    totaldist = totaldist + distance1;

                }
            }

            //   totaldist= totaldist * 0.00062137;
            totaldist = totaldist / 0.3048;
            int convert_total = (int) (totaldist);
            if (convert_total < 1000) {
                distance = String.valueOf(convert_total);
                return distance + " feet";
            } else {
                int distannceI = (int) convert_total * 100 / 5280;
                distance = String.valueOf(distannceI);
                String distance1 = distance.substring(0, distance.length() - 2);
                distance1 = distance1 + "." + distance.substring(distance.length() - 2);
                return distance1 + " miles";
            }
        }

        return "";

    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final android.support.v7.app.AlertDialog.Builder dialog =
                new android.support.v7.app.AlertDialog.Builder(getActivity());
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

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean checkConnectivityLoc(){
        boolean found_service = false;
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setTitle("Mobile data")
                    .setMessage("You have No Service or WIFI, your Google maps might not Show data. \n" +
                            "Do you want to use your current location or use Mapping? Activating GPS will take 1 - 2 minutes")
                    .setPositiveButton("Current", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getGPSdata(locationListenerGPS);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        }
                    })
                    .setNeutralButton("Mapping", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            startActivity(MapsFind1Activity.newIntent(getActivity(), mFishlog.getId()));
                            getActivity().finish();
                        }

                    });
            builder1.show();
        }

        return found_service;
    }

    private boolean checkConnectivityNotes(){
        boolean found_service = false;
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setTitle("Mobile data")
                    .setMessage("You have No Service or WIFI, your Google maps might not Show data. \n" +
                            "Do you want to use your current location or use Mapping? Activating GPS will take 1 - 2 minutes")
                    .setPositiveButton("Current", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getGPSdata(locationListenerGPSNotes);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        }
                    })
                    .setNeutralButton("Mapping", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            startActivity(MapsFindNotesActivity.newIntent(getActivity(), mFishlog.getId()));
                            getActivity().finish();
                        }

                    });
            builder1.show();
        }

        return found_service;
    }


    // For Location Description
    private void getGPSdata(LocationListener GPS) {
        if (!checkLocation())
            return;

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            int permission = ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        getActivity(),
                        PERMISSIONS_LOCATION,
                        REQUEST_ACCESS_FINE_LOCATION
                );
            };

            ActivityCompat.requestPermissions( getActivity(), new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION  },
                    REQUEST_ACCESS_FINE_LOCATION
            );

            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                        1);
            }
            // locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,locationListenerGPS,null);
            return;
        }
    //    locationManager.re
    //    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
     //           3000, 0, GPS);

        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, GPS, null);
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longitudeGPS=0.0;
            latitudeGPS=0.0;

            try {
                latitudeGPS = location.getLatitude();
                longitudeGPS = location.getLongitude();
            }
            catch(NullPointerException e)
            {
                System.out.print("Caught NullPointerException on GetLatitude");
            }


      //      mLat=latitudeGPS;
     //       mLng=longitudeGPS;

            String total1 = String.valueOf(latitudeGPS);
            String total2 = String.valueOf(longitudeGPS);

            String data =mLocationDescField.getText().toString();
            if (data.contains("LAT:")) {
                data = data.substring(0,data.indexOf("LAT:")-1);
                data = data.replaceAll("\n"," ");
                if (data.equals(" "))
                    data="Current Location";

                mLocationDescField.setText(data + " " + "\nLAT:" + total1 + " LNG:" + total2);
            } else
                mLocationDescField.setText("Current Location " + "\nLAT:" + total1 + " LNG:" + total2);

    //        FishlogLab.get(getActivity()).updateFishlog(mFishlog);
    //        mPartnersField.setText("LAT:"+total1 + " LNG:"+ total2);


            //Toast.makeText(getActivity(),total1 + " "+ total2,Toast.LENGTH_SHORT);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Toast.makeText(getActivity(),
                    "Status Changed:GPS ", Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onProviderEnabled(String s) {
            Toast.makeText(getActivity(),
                    "Provider enabled:GPS ", Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onProviderDisabled(String s) {
            Toast.makeText(getActivity(),
                    "Provider disabled:GPS " , Toast.LENGTH_SHORT)
                    .show();

        }
    };

    private final LocationListener locationListenerGPSNotes = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();
            String data2="";

            String total1 = String.valueOf(latitudeGPS);
            String total2 = String.valueOf(longitudeGPS);

            String data =mNotesField.getText().toString();

            mNotesField.setText(data +"\nCurrent Location" +" \nLAT:"+total1 + " LNG:"+ total2 );

            data =mNotesField.getText().toString();

   //         String dist = getDistance();
   /*         if (!dist.equals("")) {
                String totdist = " (Total Distance:" + dist + ")";
                String olddata = "(Total Distance:";
                if (data.contains(olddata)) {
                    if (data.contains("feet")) {
                        data2 = data.replace(data.substring(data.indexOf(olddata), data.indexOf(" feet") + 7), "");
                        mNotesField.setText(data2 + " " + totdist);
                    } else {
                        data2 = data.replace(data.substring(data.indexOf(olddata), data.indexOf(" miles") + 7), "");
                        mNotesField.setText(data2 + " " + totdist);
                    }
                } else {
                    mNotesField.setText(data + " " + totdist);
                }
            }
*/
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Toast.makeText(getActivity(),
                    "Status Changed:GPS ", Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onProviderEnabled(String s) {
            Toast.makeText(getActivity(),
                    "Provider enabled:GPS ", Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onProviderDisabled(String s) {
            Toast.makeText(getActivity(),
                    "Provider disabled:GPS " , Toast.LENGTH_SHORT)
                    .show();

        }
    };

  //  public double getLat(){
  //      return mLat;
  //  }
   // public double getLng(){
    //    return mLng;
   // }

}
