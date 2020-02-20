package com.example.mylocation;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.mylocation.app.CHANNEL_ID;

public class LocationTrackService extends Service {


    private final String Tag ="LocationClassActivity";
    private LocationRequest locationRequest;
    private static long ShortesInterval=10000;
    private static long FastestInterval= ShortesInterval/2;
    private FusedLocationProviderClient fusedLocationProviderClient;
   /* private final IBinder mBinder= new LocalBinder();*/
    private Double longitude;
    private Double latitude;
    private int phoneNumber = 1234567890;




    @Override
    public void onCreate() {

       fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        locationRequest= new LocationRequest();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        startForeground();
        requestLocationUpdates();
        return START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void startForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        startForeground(1, new NotificationCompat.Builder(this,
                CHANNEL_ID) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.avatar)
                .setContentTitle("HARAKA BUSINESS ENTERPRISES")
                .setContentText("Device Management Info")
                .setContentIntent(pendingIntent)
                .build());



    }
    private  void requestLocationUpdates(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)==
                PermissionChecker.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==
                        PermissionChecker.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==
                PermissionChecker.PERMISSION_GRANTED){
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(ShortesInterval);
            locationRequest.setFastestInterval(FastestInterval);

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);

                    longitude= locationResult.getLastLocation().getLongitude();
                    latitude=locationResult.getLastLocation().getLatitude();
                    Log.e(Tag,"lat  "+ locationResult.getLastLocation().getLatitude() +  " long " +locationResult.getLastLocation().getLongitude());

                    insertDB();


                }
            }, getMainLooper());

        }


    }

    public void insertDB(){

        RequestQueue requestQueue = Volley.newRequestQueue(LocationTrackService.this);
        String insertUrl="http://192.168.43.51:9090/insert.php";
        JSONObject jsonBody = new JSONObject();
        final String requestBody = jsonBody.toString();


        StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("Haraka Location App", " connected");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error Response", error.getMessage());

            }
        }){
            Double lat= -34.397;
            Double lng=150.644;


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parameters = new HashMap<String,String>();
                parameters.put("Latitude",lat.toString());
                parameters.put("Longitude",lng.toString());


                return parameters;

            }
            @Override
            public String getBodyContentType()
            {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("Content-Type", "application/json");
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }


            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }



        };

        requestQueue.add(request);

    }
}