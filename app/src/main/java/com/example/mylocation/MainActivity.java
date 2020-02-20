package com.example.mylocation;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";
    private LocationTrackService mBoundService;
    //public emp


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       /* Intent intent = new Intent(this,LocationTrackService.class);
        bindService(intent,mServerConn, Context.BIND_AUTO_CREATE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
        /*startService(new Intent(this , LocationTrackService.class));*/




        Dexter.withActivity(this)
                .withPermissions(Arrays.asList(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ))
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        startService(new Intent(getApplicationContext(),LocationTrackService.class));



                        /*mBoundService.requestLocationUpdates();*/
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();





    }
/*
    protected ServiceConnection mServerConn = new ServiceConnection() {
        private static final String LOG_TAG = "LocationServices";

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(LOG_TAG, "onServiceConnected");

            LocationTrackService.LocalBinder localBinder= (LocationTrackService.LocalBinder) binder;
            mBoundService= localBinder.getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(LOG_TAG, "onServiceDisconnected");
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        //bind


    }*/
}


