package com.example.projectscandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.morphingbutton.MorphingButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.snackbar.Snackbar;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;

import static java.util.Collections.singletonList;

public class QrScanFragment extends Fragment {
    Activity activity;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;
    MorphingButton scanqr;
    String path,filename,path1;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_qrscan, container, false);
        ButterKnife.bind( this, root);

        scanqr = (MorphingButton) root.findViewById(R.id.scan_qrcode);

        scanqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanQrCode();
            }
        });
        // Get runtime permissions if build version >= Android M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
            }
        }

        return root;
    }

    private void ScanQrCode() {
       /* Fragment fragment =new Qrcodecapture();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
    */
     //   Intent intent=new Intent(activity, Qrcodecapture.class);
       // startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra("barcode");
                    String scan_value = barcode.displayValue;
                    showalertdialog(scan_value);
                    Toast.makeText(getContext(), scan_value, Toast.LENGTH_SHORT).show();
                } else {
                    showalertdialog("No Barcode Captured");
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showalertdialog(final String string) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Scanned Value");
        builder.setMessage(string);
        if (string.equals("No Barcode Captured")) {
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(activity, "Cancel Clicked", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        } else {
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(activity, "Save Clicked", Toast.LENGTH_SHORT).show();

                    //Array of File Type
                    int filetype = R.array.selectfiletype;
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(activity);
                    builder2.setTitle("Type of File to be saved");
                    builder2.setItems(filetype, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == 0) {
                                createtxt(string);
                                Toast.makeText(activity, "Text", Toast.LENGTH_SHORT).show();

                            } else if (i == 1) {
                               // createfile(string, i);
                                Toast.makeText(activity, "Doc", Toast.LENGTH_SHORT).show();

                            } else {
                               // createfile(string, i);
                                Toast.makeText(activity, "PDF", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                    builder2.show();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(activity, "Cancel Clicked", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        }
    }

    private void createtxt(final String string) {
        new MaterialDialog.Builder(activity)
                .title(R.string.creating_txt)
                .content(R.string.enter_file_name)
                .input(getString(R.string.example), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input == null || input.toString().trim().equals("")) {
                            Toast.makeText(activity, R.string.toast_name_not_blank, Toast.LENGTH_LONG).show();
                        } else {
                            filename = input.toString();
                             path1= String.valueOf(new Creatingtxt().execute());
                            File folder=new File(path1);
                            File myfile = new File(folder, path1);

                            FileOutputStream fileOutputStream = null;
                            try {
                                fileOutputStream = new FileOutputStream(myfile);
                                try {
                                    fileOutputStream.write(string.getBytes());
                                    Toast.makeText(activity, "Done " + myfile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                })
                .show();





    }

    /**
     * An async task that converts selected images to Pdf
     */
    public class Creatingtxt extends AsyncTask<String, String, String> {

        // Progress dialog
        MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                .title(R.string.please_wait)
                .content(R.string.populating_list)
                .cancelable(false)
                .progress(true, 0);
        MaterialDialog dialog = builder.build();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    activity.getString(R.string.pdf_dir);

            File folder = new File(path);
            if (!folder.exists()) {
                boolean success = folder.mkdir();
                if (!success) {
                    Toast.makeText(activity, "Error on creating application folder", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }
            path = path + filename + activity.getString(R.string.txt_ext);
            return (path);
        }
    }
}
