package com.swapuniba.crowdpulse.main;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.swapuniba.crowdpulse.R;
import com.swapuniba.crowdpulse.config.SettingFile;
import com.swapuniba.crowdpulse.utility.NotificationUtility;
import com.swapuniba.crowdpulse.utility.Utility;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 * In questa activity vengono gestiti i permessi necessari ad estrarre alcuni dati dello smartphone.
 * I permessi richiesti sono definiti nella variabile PERMISSIONS e PERMISSIONS_v29 è vengono chiesti
 * in base alla versione.
 * L'activity viene riproposta fino a quando non vengono concessi tutti i permessi.
 *
 */
public class Intro extends Activity {

    static boolean check_ACCESS_SETTINGS = false;
    static boolean first = true;

    /** code to post/handler request for permission */
    public final static int REQUEST_CODE = -1010101;

    private static final int REQUEST_MANAGER = 0x11;

    //IMPORTANT!!!!
    //modify the permission here and in the manifest
    private static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            //Manifest.permission.PACKAGE_USAGE_STATS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_CONTACTS ,
        };
    private static String[] PERMISSIONS_v29 = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACTIVITY_RECOGNITION,
            //Manifest.permission.PACKAGE_USAGE_STATS,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_CONTACTS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.intro);

        SettingFile.inizialize(getApplicationContext());

      //  HashMap<String, String> a = SettingFile.getSettings(getApplicationContext());

        //check permission

            /** check if we already  have permission to draw over other apps */
            if (isAccessGranted()) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

                    requestPermissions(PERMISSIONS_v29 ,REQUEST_MANAGER );


                }else{
                    requestPermissions(PERMISSIONS, REQUEST_MANAGER);

                }
            }
            else{
                check_ACCESS_SETTINGS = true;
                //startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), REQUEST_CODE);
            }



    }

    @Override
    protected void onResume() {
        super.onResume();

        if (first){
            first = false;
        }
        else{
            if (check_ACCESS_SETTINGS) {
                check_ACCESS_SETTINGS = false;
                reload();
            }
        }

    }

    /**
     * Per richiedere di consentire l'applicazione a gestire le statistiche di utilizzo
     * @return
     */
    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    private void initialOperations(){
        openLoginActivity();
    }


    private void openLoginActivity(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), SetIP.class);//Login.class);//Main.class);
                startActivity(intent);
            }
        }, 3000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        /** check if received result code
         is equal our requested code for draw permission  */
        if (requestCode == REQUEST_CODE) {
            //reload();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult: ");
        if (requestCode == REQUEST_MANAGER) {

            Log.i(TAG, "onRequestPermissionsResult 2 : ");
            boolean all_accepted = true;
            for (int i = 0; i < grantResults.length; i++ ){

                Log.i(TAG, "onRequestPermissionsResult: 3 ");
                //Utility.printLog("permissions: " + permissions[i] + "grant: " + grantResults[i]);

                if(permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION)){
                    if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                        requestGPSEnable();
                    }
                }

                if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    all_accepted = false;
                }


            }

            if (all_accepted && gpsEnabled()) {

                initialOperations();
            }
            else{
                reload();
            }

        }
    }

    /**
     * Richiede di abilitare il GPS
     */
    private void requestGPSEnable(){

        if (!gpsEnabled()) {
            Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(viewIntent);
        }

    }

    private boolean gpsEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(locationManager != null){
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        }else{
            return true;
        }
    }

    private void reload(){

        onCreate(getIntent().getExtras());

    }


}



