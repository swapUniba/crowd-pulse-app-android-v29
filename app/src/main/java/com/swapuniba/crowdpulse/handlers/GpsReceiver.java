package com.swapuniba.crowdpulse.handlers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.swapuniba.crowdpulse.R;
import com.swapuniba.crowdpulse.business_object.GPS;
import com.swapuniba.crowdpulse.utility.NotificationUtility;

import java.util.List;

/**
 * Riceve l'azione ACTION_PROCESS_UPDATE per ottenere i dati sul gps.
 * E' possibile che venga ricevuto più di un dato gps (latitudine e longitudine)
 */
public class GpsReceiver extends BroadcastReceiver {

    private static final String TAG = "GpsReceiver";
    private FusedLocationProviderClient mFusedLocationClient;
    private Context c;
    static final String ACTION_PROCESS_UPDATES =
            "com.swapuniba.crowdpulse.action.PROCESS_UPDATES";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");
        c = context;
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {

                    List<Location> locations = result.getLocations();

                   /* NotificationUtility.notification(context , "GPS" , "Lat: " + locations.get(0).getLatitude() + " Long: "
                            + locations.get(0).getLongitude() , R.drawable.logo); */

                    for(Location loc : locations){

                        GPS gps =  GpsHandler.readGPS(loc , context);
                        GpsHandler.saveGPS(gps,context);

                    }






                    removeLocationUpdates();



                }
            }
        }
    }


    private PendingIntent getPendingIntent() {
        // Note: for apps targeting API level 25 ("Nougat") or lower, either
        // PendingIntent.getService() or PendingIntent.getBroadcast() may be used when requesting
        // location updates. For apps targeting API level O, only
        // PendingIntent.getBroadcast() should be used. This is due to the limits placed on services
        // started in the background in "O".

        Intent intent = new Intent(c, GpsReceiver.class);
        intent.setAction(GpsReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(c, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public void removeLocationUpdates() {


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(c);

        Log.i(TAG, "Removing location updates");

        PendingIntent pendingIntent = getPendingIntent();



        mFusedLocationClient.removeLocationUpdates(pendingIntent);
    }
}
