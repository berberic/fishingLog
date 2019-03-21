package com.berberic.android.fishinglog;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;

/**
 * Created by berberic on 10/15/2017.
 */

public class FishlogImportExportFragment extends Fragment {
    private Button mExportButton;
    private Button mExportButtonSelection;
    private Button mImportButton;
  //  private Button mDeleteButton;
    private RadioButton mAppend;
    private RadioButton mOverwrite;
    private EditText mKey;


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public void onCreate(Bundle savedInstanceState){
        StrictMode.VmPolicy.Builder builder1 = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder1.build());
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_import_export,container,false);

   /*     mDeleteButton = (Button) v.findViewById(R.id.delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            String m_chosen;
            @Override
            public void onClick(View v) {
                /////////////////////////////////////////////////////////////////////////////////////////////////
                //Create FileOpenDialog and register a callback
                /////////////////////////////////////////////////////////////////////////////////////////////////
                SimpleFileDialog FileOpenDialog = new SimpleFileDialog(getActivity(), "FileOpen",
                        new SimpleFileDialog.SimpleFileDialogListener() {
                            @Override
                            public void onChosenDir(String chosenDir) {
                                // The code in this function will be executed when the dialog OK button is pushed
                                m_chosen = chosenDir;
                                Toast.makeText(getActivity(), "Chosen FileOpenDialog File: " +
                                        m_chosen, Toast.LENGTH_LONG).show();
                                getFileDelete(m_chosen);
                            }
                        });

                //You can change the default filename using the public variable "Default_File_Name"
                FileOpenDialog.Default_File_Name = "";
                FileOpenDialog.chooseFile_or_Dir();

                /////////////////////////////////////////////////////////////////////////////////////////////////
            }
        });
*/
        mKey = (EditText) v.findViewById(R.id.key);
        mAppend = (RadioButton) v.findViewById(R.id.append);
        mOverwrite = (RadioButton) v.findViewById(R.id.overwrite);

        mImportButton = (Button) v.findViewById(R.id.importDb);
        mImportButton.setOnClickListener(new View.OnClickListener() {
            String m_chosen;
            @Override
            public void onClick(View v) {
                // Check if we have write permission
                int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE
                    );
                }

                String key=mKey.getText().toString();
                if (key == null || key.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Missing Key")
                            .setMessage("There is no Encryption Key. The File will not be decrypted " +
                                    "without one. " +
                                    "Answer NO to enter a Key to Encrypt/Decrypt this file. " +
                                    "Otherwise Yes for an un-encrypted file")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    /////////////////////////////////////////////////////////////////////////////////////////////////
                                    //Create FileOpenDialog and register a callback
                                    /////////////////////////////////////////////////////////////////////////////////////////////////
                                    SimpleFileDialog FileOpenDialog = new SimpleFileDialog(getActivity(), "FileOpen",
                                            new SimpleFileDialog.SimpleFileDialogListener() {
                                                @Override
                                                public void onChosenDir(String chosenDir) {
                                                    // The code in this function will be executed when the dialog OK button is pushed
                                                    m_chosen = chosenDir;
                                                    Toast.makeText(getActivity(), "Chosen FileOpenDialog File: " +
                                                            m_chosen, Toast.LENGTH_LONG).show();
                                                    getFile(m_chosen);
                                                }
                                            });

                                    //You can change the default filename using the public variable "Default_File_Name"
                                    FileOpenDialog.Default_File_Name = "";
                                    FileOpenDialog.chooseFile_or_Dir();

                                    /////////////////////////////////////////////////////////////////////////////////////////////////

                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                }
                else {
                    /////////////////////////////////////////////////////////////////////////////////////////////////
                    //Create FileOpenDialog and register a callback
                    /////////////////////////////////////////////////////////////////////////////////////////////////
                    SimpleFileDialog FileOpenDialog = new SimpleFileDialog(getActivity(), "FileOpen",
                            new SimpleFileDialog.SimpleFileDialogListener() {
                                @Override
                                public void onChosenDir(String chosenDir) {
                                    // The code in this function will be executed when the dialog OK button is pushed
                                    m_chosen = chosenDir;
                                    Toast.makeText(getActivity(), "Chosen FileOpenDialog File: " +
                                            m_chosen, Toast.LENGTH_LONG).show();
                                    getFile(m_chosen);
                                }
                            });

                    //You can change the default filename using the public variable "Default_File_Name"
                    FileOpenDialog.Default_File_Name = "";
                    FileOpenDialog.chooseFile_or_Dir();

                    /////////////////////////////////////////////////////////////////////////////////////////////////

                }

            }
        });

        mExportButtonSelection = (Button) v.findViewById(R.id.exportDb1);
        mExportButtonSelection.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                // Check if we have write permission
                int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE
                    );
                }

                String key=mKey.getText().toString();
                if (key == null || key.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Missing Key")
                            .setMessage("There is no Encryption Key. The File will not be encrypted " +
                                    "without one. The file will be readable text! " +
                                    "Answer NO to enter a key to Encrypt/Decrypt this file. "+
                                    "Otherwise Yes, to continue.")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FishlogLab fishlogLab = FishlogLab.get(getActivity());
                                    File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                    File file = new File(exportDir, "Fly-FishLogSelection.csv");

                                    boolean db = fishlogLab.exportSelectionDatabase(null,file.toString());

                                    if (db) {


                                        if (file.exists()) {
                                            Intent i = new Intent(Intent.ACTION_SEND);
                                            i.setType("text/plain");
                                            i.putExtra(Intent.EXTRA_TEXT, R.string.fishlog_Db_report);
                                            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.fishlog_database_subject));
                                            i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));

                                            i = Intent.createChooser(i, getString(R.string.send_report));
                                            startActivity(i);
                                            try {
                                                Thread.sleep(20000);

                                                getFileDelete(file.toString());
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            android.os.Process.killProcess(android.os.Process.myPid());
                                            System.exit(1);
                                        }
                                    }
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                } else {

                    FishlogLab fishlogLab = FishlogLab.get(getActivity());
                    File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File file = new File(exportDir, "Fly-FishLogSelection.csv");
                    boolean db = fishlogLab.exportSelectionDatabase(key,file.toString());

                    if (db) {



                        if (file.exists()) {
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("text/plain");
                            i.putExtra(Intent.EXTRA_TEXT, R.string.fishlog_Db_report);
                            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.fishlog_database_subject));
                            i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));

                            i = Intent.createChooser(i, getString(R.string.send_report));
                            startActivity(i);
                            try {
                                Thread.sleep(20000);

                                getFileDelete(file.toString());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            // Kill currently running Activity process
                            // Kill the email process after sending mail
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);

                        }
                    }
                }


            }

        });

        mExportButton = (Button) v.findViewById(R.id.exportDb);
        mExportButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                // Check if we have write permission
                int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE
                    );
                }

                String key=mKey.getText().toString();
                if (key == null || key.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Missing Key")
                            .setMessage("There is no Encryption Key. The File will not be encrypted " +
                                    "without one. The file will be readable text! " +
                                    "Answer NO to enter a key to Encrypt/Decrypt this file. "+
                                    "Otherwise Yes, to continue and wait for transaction to complete.")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FishlogLab fishlogLab = FishlogLab.get(getActivity());
                                    File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                    File file = new File(exportDir, "Fly-Fishlog.csv");
                                    if (file.exists()) getFileDelete(file.toString());

                                    boolean db = fishlogLab.exportDatabase(null,file.toString());

                                    if (db) {


                                        if (file.exists()) {
                                            Intent i = new Intent(Intent.ACTION_SEND);
                                            i.setType("text/plain");
                                            i.putExtra(Intent.EXTRA_TEXT, R.string.fishlog_Db_report);
                                            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.fishlog_database_subject));
                                            i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));

                                            i = Intent.createChooser(i, getString(R.string.send_report));
                                            startActivity(i);

                                            // Kill currently running Activity process
                                            // Kill the email process after sending mail

                       //                     try {
                       //                         Thread.sleep(20000);

                       //                         getFileDelete(file.toString());
                       //                     } catch (InterruptedException e) {
                        //                        e.printStackTrace();
                        //                    }
                                            android.os.Process.killProcess(android.os.Process.myPid());
                                            System.exit(1);
                                        }
                                    }
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                } else {

                    FishlogLab fishlogLab = FishlogLab.get(getActivity());
                    File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File file = new File(exportDir, "Fly-Fishlog.csv");
                    if (file.exists())
                     getFileDelete(file.toString());

                    // include encryption key
                    boolean db = fishlogLab.exportDatabase(key,file.toString());

                    if (db) {

                        if (file.exists()) {
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("text/plain");
                            i.putExtra(Intent.EXTRA_TEXT, R.string.fishlog_Db_report);
                            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.fishlog_database_subject));
                            i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));

                            i = Intent.createChooser(i, getString(R.string.send_report));
                            startActivity(i);
                    //        try {
                   //             Thread.sleep(20000);

                    //            getFileDelete(file.toString());
                     //       } catch (InterruptedException e) {
                    //            e.printStackTrace();
                    //        }
                            // Kill currently running Activity process
                            // Kill the email process after sending mail
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    }
                }

            }

        });

        return v;
    }

    public void completeUpload(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Upload Complete")
                .setMessage("The Upload is complete.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                })
                .show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        // inflater.getMenuInflater().inflate(R.menu.fragment_fishlog, menu);
        inflater.inflate(R.menu.fragment_import_export, menu);
        //     inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId())
        {
            case R.id.delete:
                /////////////////////////////////////////////////////////////////////////////////////////////////
                //Create FileOpenDialog and register a callback
                /////////////////////////////////////////////////////////////////////////////////////////////////


                SimpleFileDialog FileOpenDialog = new SimpleFileDialog(getActivity(), "FileOpen",
                        new SimpleFileDialog.SimpleFileDialogListener() {
                            String m_chosen;
                            @Override
                            public void onChosenDir(String chosenDir) {
                                // The code in this function will be executed when the dialog OK button is pushed
                                m_chosen = chosenDir;
                                Toast.makeText(getActivity(), "Chosen FileOpenDialog File: " +
                                        m_chosen, Toast.LENGTH_LONG).show();
                                getFileDelete(m_chosen);
                            }
                        });

                //You can change the default filename using the public variable "Default_File_Name"
                FileOpenDialog.Default_File_Name = "";
                FileOpenDialog.chooseFile_or_Dir();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void completeUploadNOT(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Upload NOT Complete")
                .setMessage("The Upload did NOT complete. There is probably an error in your data." +
                        "Check to see how much did complete.  After that point is where the error " +
                        "could be.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                })
                .show();
    }

    public void getFile(String chosenFile){

        if (chosenFile.contains(".csv")) {
            final String choose = chosenFile;

            int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        getActivity(),
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }

            if (mAppend.isChecked()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Append Data to fishinglog")
                        .setMessage("Are you sure you want to Append this data")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FishlogLab fishlogLab = FishlogLab.get(getActivity());
                                //Temporarily do not do
                                String key = mKey.getText().toString();
                                //               boolean del = fishlogLab.deleteDatabaseFile(choose);
                                boolean db = fishlogLab.importDatabase(true, choose, key);
                                if (db)
                                    completeUpload();
                                else
                                    completeUploadNOT();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                return;


            } else if (mOverwrite.isChecked()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Overwrite Data to fishinglog")
                        .setMessage("Are you sure you want to Overwrite with this data. All " +
                                "previous records will be deleted! This is primarily use to " +
                                "restore a backup.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FishlogLab fishlogLab = FishlogLab.get(getActivity());
                                //Temporarily do not do
                                String key = mKey.getText().toString();
                                boolean db = fishlogLab.importDatabase(false, choose, key);
                                if (db)
                                    completeUpload();
                                else
                                    completeUploadNOT();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                return;

            } else {
                // this is wrong
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Append Data to fishinglog")
                        .setMessage("You did not select Overwrite or Append. You must select " +
                                "one or the other.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }

                        })
                        .show();
                return;
            }
        } else{
            Toast.makeText(getActivity(), "There was no file found to import.", Toast.LENGTH_LONG).show();

        }
    }

    public void getFileDelete(String chosenFile){

        int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        FishlogLab fishlogLab = FishlogLab.get(getActivity());

        boolean db = fishlogLab.deleteDatabaseFile(chosenFile);

    }
}

