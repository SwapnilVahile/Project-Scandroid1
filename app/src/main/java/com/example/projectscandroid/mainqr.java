package com.example.projectscandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class mainqr extends AppCompatActivity {

    Calendar calendar;
    String timesysa;             //TimeSystemAllocated
    SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrmainactivity);
        isCameraPermissionGranted();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isCameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(this, "PERMISSION RE_GRANTED", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
                return false;
            }
        } else { //Permission is automatically granted on sdk<23 upon install
            Toast.makeText(this, "OS is not supported", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public void ScanBarcode(View view) {
        Intent in = new Intent(this, BarcodeCaptureActivity.class);
        startActivityForResult(in, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra("barcode");
                    String scan_value = barcode.displayValue;
                    showalertdialog(scan_value);
                    Toast.makeText(this, scan_value, Toast.LENGTH_SHORT).show();
                } else {
                    showalertdialog("No Barcode Captured");
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showalertdialog(final String string) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mainqr.this);
        builder.setTitle("Scanned Value");
        builder.setMessage(string);
        if (string.equals("No Barcode Captured")) {
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(mainqr.this, "Cancel Clicked", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        } else {
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(mainqr.this, "OK Clicked", Toast.LENGTH_SHORT).show();

                    //Array of File Type
                    int filetype = R.array.selectfiletype;
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(mainqr.this);
                    builder2.setTitle("Type of File to be saved");
                    builder2.setItems(filetype, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == 0) {
                                createfile(string, i);
                            } else if (i == 1) {
                                createfile(string, i);
                            } else {
                                createfile(string, i);
                            }
                        }
                    });
                    builder2.show();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(mainqr.this, "Cancel Clicked", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        }
    }

    private void createfile(String string, int i) {
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        timesysa = simpleDateFormat.format(calendar.getTime());
        if (i == 0) {
            timesysa = "Scan Data" + timesysa + ".txt";
        } else if (i == 1) {
            timesysa = "Scan Data" + timesysa + ".doc";
        } else {
            timesysa = "Scan Data" + timesysa + ".pdf";
        }
        ActivityCompat.requestPermissions(mainqr.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); //Folder where file will be saved
        File myfile = new File(folder, timesysa);
        writeData(myfile, string);
    }

    private void writeData(File myfile, String string) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(myfile);
            try {
                fileOutputStream.write(string.getBytes());
                Toast.makeText(this, "Done " + myfile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
