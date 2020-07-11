package com.swapuniba.crowdpulse.handlers;


import android.app.Activity;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.swapuniba.crowdpulse.BuildConfig;
import com.swapuniba.crowdpulse.business_object.AbstractData;
import com.swapuniba.crowdpulse.business_object.ActivityData;
import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.database.DbManager;
import com.swapuniba.crowdpulse.utility.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

//import io.socket.client.Ack;
//import io.socket.client.Socket;

/**
 *
 * Classe simile ad ActivityHandler_v28 , la differenza tra le due classi è che questa usa lavori
 * per effettuare l'estrazione dei dati sulle attività e ha il ricevitore interno mentre
 * ActivityHandler_28 ha il ricevitore esterno ed estrae i dati senza utilizzare i lavori.
 *
 * Non viene utilizzata per l'estrazione dei dati ma solo per controllare i tempi di estrazione
 * e salvare i dati sul db. La classe effettivamente utilizzata è ActivityHandler_v28
 *
 */
public class ActivityHandler  extends JobIntentService {
    private static final String TAG = "ActivityHandler";
    private List<ActivityTransition> activityTransitionList;

    private final String TRANSITIONS_RECEIVER_ACTION =
            BuildConfig.APPLICATION_ID + ".TRANSITIONS_RECEIVER_ACTION";
    BroadcastReceiver broadcastReceiver = null;
    private PendingIntent mActivityTransitionsPendingIntent;



    private static ActivityData handleDetectedActivities(List<ActivityTransitionEvent> probableActivities) {
        ActivityData activityData = new ActivityData();

        activityData.timestamp = System.currentTimeMillis() + "";

        for( ActivityTransitionEvent activity : probableActivities ) {

            switch( activity.getActivityType() ) {
                case DetectedActivity.IN_VEHICLE: {
                    Utility.printLog("In Vehicle: " + toTransitionType(activity.getTransitionType()) );
                    activityData.inVehicle = toTransitionType(activity.getTransitionType()) + "";
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Utility.printLog("On Bicycle: " + toTransitionType(activity.getTransitionType()) );
                    activityData.onBicycle =toTransitionType(activity.getTransitionType()) + "";
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Utility.printLog("On Foot: " + toTransitionType(activity.getTransitionType()) );
                    activityData.onFoot = toTransitionType(activity.getTransitionType()) + "";
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Utility.printLog("Running: " + toTransitionType(activity.getTransitionType()) );
                    activityData.running = toTransitionType(activity.getTransitionType()) + "";
                    break;
                }
                case DetectedActivity.STILL: {
                    Utility.printLog("Still: " + toTransitionType(activity.getTransitionType()) );
                    activityData.still = toTransitionType(activity.getTransitionType()) + "";
                    break;
                }
                //Deprecato
                case DetectedActivity.TILTING: {
                    Utility.printLog("Tilting: " + toTransitionType(activity.getTransitionType()) );
                    activityData.tilting = toTransitionType(activity.getTransitionType()) + "";
                    break;
                }
                case DetectedActivity.WALKING: {
                    Utility.printLog("Walking: " + toTransitionType(activity.getTransitionType()) );
                    activityData.walking = toTransitionType(activity.getTransitionType()) + "";
                    break;
                }
                default: {
                    Utility.printLog("Unknown: " + toTransitionType(activity.getTransitionType()));
                    activityData.unknown = toTransitionType(activity.getTransitionType()) + "";
                    break;
                }
            }
        }

        activityData.send = false;

        //activityData.print();

        return activityData;

    }

    /**
     * check if is pass the right time between 2 get
     * @param context
     * @return
     */
    public static Boolean checkTimeBetweenRequest(Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return  Long.parseLong(preferences.getString(Constants.last_activity_send, "0")) < System.currentTimeMillis();

    }

    /**
     * check the next time to register the data
     * @param context
     * @return
     */
    public static void setNetxTime(Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        Long netxTime = Long.parseLong(preferences.getString(Constants.setting_time_read_activity, "0")) + System.currentTimeMillis();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.last_activity_send, netxTime+ "");
        editor.apply();

    }

    public static Boolean saveActivity(ActivityData activity, Context context){

        Boolean done = false;
        DbManager db = new DbManager(context);

        ActivityData db_activity = db.getActivity(activity.timestamp);

        if(db_activity == null){
            done = db.saveActivity(activity);
        }
        else {
            done = db.updateActivity(activity);
        }

        return done;

    }

    public static ArrayList<? extends AbstractData> getNotSendActivity(Context context){
        DbManager dbManager = new DbManager(context);
        return dbManager.getNotSendActivity();
    }

    public static void enqueueWork(Context context , Intent intent){
        enqueueWork(context , ActivityHandler.class , Constants.JOB_ACT_HANDLER_ID , intent);
    }

    private void setActivityTransitionList(){
        activityTransitionList = new ArrayList<>();

        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());

        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {



        //i tipi di attività da gestire
        setActivityTransitionList();
        Intent intentActivity = new Intent(TRANSITIONS_RECEIVER_ACTION);
        mActivityTransitionsPendingIntent = PendingIntent.getBroadcast(getApplicationContext() , 123 , intentActivity , PendingIntent.FLAG_UPDATE_CURRENT);

        if(broadcastReceiver==null){
            broadcastReceiver = new ReceiverActivity();
            registerReceiver(
                    broadcastReceiver,
                    new IntentFilter(TRANSITIONS_RECEIVER_ACTION)
            );
        }else{
            Utility.printLog("TAG-M-Activity","Broadcast già registrato");
        }

        ActivityTransitionRequest request = new ActivityTransitionRequest(activityTransitionList);

        Task<Void> task = ActivityRecognition.getClient(getApplicationContext())
                .requestActivityTransitionUpdates(request, mActivityTransitionsPendingIntent);


        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "onSuccess: Transaction Api was successfully registered");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: Transitions Api could NOT be registered: ");
                Log.e(TAG, "Transitions Api could NOT be registered: " + e);

            }
        });


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //use in receiver
    private static  String toTransitionType(int transitionType) {
        switch (transitionType) {
            case ActivityTransition.ACTIVITY_TRANSITION_ENTER:
                return "100";
            case ActivityTransition.ACTIVITY_TRANSITION_EXIT:
                return "0";
            default:
                return "0";
        }
    }

    private  String toActivityString(int activity) {
        switch (activity) {
            case DetectedActivity.STILL:
                return "Sei fermo";
            case DetectedActivity.WALKING:
                return "Stai camminando";
            case DetectedActivity.IN_VEHICLE:
                return "Sei in un veicolo";
            case DetectedActivity.RUNNING:
                return "Stai correndo";
            case DetectedActivity.ON_BICYCLE:
                return "Sei in bici";
            default:
                return "Stai facendo qualcosa ma non so cosa";
        }
    }

    class ReceiverActivity extends BroadcastReceiver {
        private static final String TAG = "ReceiverActivity";
        @Override
        public void onReceive(Context context, Intent intent) {

            ActivityData activityData = null;

            Utility.printLog("TAG-M-"+TAG,"onReceive Attivato");





            // TODO: Extract activity transition information from listener.
            if(ActivityTransitionResult.hasResult(intent)){
                ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);


                if(result.getTransitionEvents()!=null){
                    if(result.getTransitionEvents().size()!=0){

                        Utility.printLog("TAG-M-"+TAG,"Attività in corso");

                        activityData = handleDetectedActivities(result.getTransitionEvents());

                        saveActivity(activityData , getApplicationContext());

                    }

                }

            }

        }
    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(broadcastReceiver);
        }catch (Exception e){
            Log.i(TAG, "onDestroy: Problema con ricevitore , cancellazione della registrazione non avvenuta");
            e.printStackTrace();
        }
        Log.i(TAG, " in onDestroy");

        super.onDestroy();

    }

    public static void deleteCache(Context context){

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        List<JobInfo> jobInfoList = scheduler.getAllPendingJobs();
        for(JobInfo jobInfo : jobInfoList){
            if(jobInfo.getId() == Constants.JOB_ACT_HANDLER_ID){
                scheduler.cancel(Constants.JOB_ACT_HANDLER_ID);
                break;
            }
        }


    }



}



