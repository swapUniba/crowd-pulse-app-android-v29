package com.swapuniba.crowdpulse.handlers;


import android.Manifest;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.swapuniba.crowdpulse.business_object.AbstractData;
import com.swapuniba.crowdpulse.business_object.GPS;
import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.database.DbManager;
import com.swapuniba.crowdpulse.utility.Utility;

import java.util.ArrayList;
import java.util.List;

//import io.socket.client.Ack;
//import io.socket.client.Socket;

/**
 *I dati sul gps vengono estratti utilizzando le API di google service.
 */
public class GpsHandler {



    private static final String TAG = "GpsHandler";

    private static final long UPDATE_INTERVAL = 60000; // Every 60 seconds.

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value, but they may be less frequent.
     */
    private static final long FASTEST_UPDATE_INTERVAL = 30000; // Every 30 seconds

    /**
     * The max time before batched results are delivered by location services. Results may be
     * delivered sooner than this interval.
     */
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 6; // Every 5 minutes.

    private static final long EXPIRATION_TIME = UPDATE_INTERVAL * 8 ;



    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private static FusedLocationProviderClient mFusedLocationClient;

    private static Location location = null;

    //Per ottenere le coordinate gps
    public static GPS readGPS(Location location , Context context) {

        GPS gps = new GPS();


        if (location != null) {

            gps.timestamp = String.valueOf(location.getTime());
            gps.latitude = String.valueOf(location.getLatitude());
            gps.longitude = String.valueOf(location.getLongitude());
            gps.speed = String.valueOf(location.getSpeed());
            gps.accuracy = String.valueOf(location.getAccuracy());
            gps.send = false;



                if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Utility.printLog("No GPS permission!");
                    return null;
                }

                if(Utility.vGreaterThan28()){
                    if(context.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION ) != PackageManager.PERMISSION_GRANTED){
                        Utility.printLog("No GPS permission , v29!");
                        return  null;
                    }
                }





        }
        else{
            Utility.printLog("Error: Gps timestamp is null");
            gps = null;
        }

        return gps;

    }

    /**
     * check if is pass the right time between 2 get
     * @param context
     * @return
     */
    public static Boolean checkTimeBetweenRequest(Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Utility.printLog("TAG-M-GPS","last_gps_send: " + preferences.getString(Constants.last_gps_send, "0"));
       // Utility.printLog("TAG-M-GPS","currentTimeMillis: " + System.currentTimeMillis());

       /* long last_app_send =  Long.parseLong(preferences.getString(Constants.last_gps_send, "0"));
        long currentTimeMillis = System.currentTimeMillis();
        long result = last_app_send - currentTimeMillis;
        Utility.printLog("TAG-M-GPS" , "last_gps_send - currentTimeMillis = " + result  ); */

        return  Long.parseLong(preferences.getString(Constants.last_gps_send, "0")) < System.currentTimeMillis();

    }

    /**
     * check the next time to register the data
     * @param context
     * @return
     */
    public static void setNetxTime(Context context){



        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        Long netxTime = Long.parseLong(preferences.getString(Constants.setting_time_read_gps, "0")) + System.currentTimeMillis();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.last_gps_send, netxTime+ "");
        editor.apply();

    }

    public void createRequest(Context context){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        createLocationRequest();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, getPendingIntent(context));


    }

    private PendingIntent getPendingIntent(Context context) {
        // Note: for apps targeting API level 25 ("Nougat") or lower, either
        // PendingIntent.getService() or PendingIntent.getBroadcast() may be used when requesting
        // location updates. For apps targeting API level O, only
        // PendingIntent.getBroadcast() should be used. This is due to the limits placed on services
        // started in the background in "O".

        // TODO(developer): uncomment to use PendingIntent.getService().
//        Intent intent = new Intent(this, LocationUpdatesIntentService.class);
//        intent.setAction(LocationUpdatesIntentService.ACTION_PROCESS_UPDATES);
//        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(context, GpsReceiver.class);
        intent.setAction(GpsReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(context, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        // Note: apps running on "O" devices (regardless of targetSdkVersion) may receive updates
        // less frequently than this interval when the app is no longer in the foreground.
        mLocationRequest.setInterval(UPDATE_INTERVAL);



        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        // Sets the maximum time when batched location updates are delivered. Updates may be
        // delivered sooner than this interval.
        mLocationRequest.setMaxWaitTime(180000);
    }

    public static Boolean saveGPS(GPS gps, Context context){

        Boolean done = false;
        DbManager db = new DbManager(context);

        GPS db_gps = db.getGPS(gps.timestamp);

        if(db_gps == null){
            done = db.saveGps(gps);
        }
        else {
            done = db.updateGPS(gps);
        }

        return done;

    }

    public static ArrayList<? extends AbstractData> getNotSendGPS(Context context){
        DbManager dbManager = new DbManager(context);
        return dbManager.getNotSendGPS();
    }

}



