package com.dup.tdup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import static android.app.PendingIntent.getActivity;
import static java.security.AccessController.getContext;

@SuppressLint("Registered")
public class PermissionManager extends AppCompatActivity
{
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1001;
    private final String TAG = "C-PREVIEWMANAGER: "; //log TAG
    private static Activity act = null;
    private static boolean allPermissionsGranted = false;
    private static String[] permissionList = {Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final int REQUEST_CODE = 999;
    public PermissionManager(Activity act){this.act = act;}


    public static boolean checkPermissions()
    {
        for(String perm:permissionList)
        {
            if(ContextCompat.checkSelfPermission(act, perm) != PackageManager.PERMISSION_GRANTED)
            {
                allPermissionsGranted = false;
                return false;
            }
            else{allPermissionsGranted = true;}
        }//end for loop
        return allPermissionsGranted;
    }//end checkPermissions

    public static void checkpermissions()
    {
        ActivityCompat.requestPermissions(act, permissionList,REQUEST_CODE);

        int permssionCheck = ContextCompat.checkSelfPermission(act,Manifest.permission.CAMERA);

        if (ContextCompat.checkSelfPermission(act,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(act,
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(act,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                                  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this,"승인이 허가되어 있습니다.",Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this,"아직 승인받지 않았습니다.",Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }
    
    public void requestPerms()
    {
        if(checkPermissions()) return;
        else
            {ActivityCompat.requestPermissions(act, permissionList,REQUEST_CODE);}
    }//end requestPerms



}//end class
