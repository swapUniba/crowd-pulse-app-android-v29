package com.swapuniba.crowdpulse.handlers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.swapuniba.crowdpulse.BuildConfig;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Si occupa dell'estrazione dei dati relativi alle attivitò. Vedere ActivityHandler per capire le
 * differenze tra le due classi.
 * Per l'estrazione vengono utilizzate le librerie di google service.
 */
public class ActivityHandler_v28 {

    private static final String TAG = "ActivityHandler_v28";
    private static List<ActivityTransition> activityTransitionList;

    private static final String TRANSITIONS_RECEIVER_ACTION =
            BuildConfig.APPLICATION_ID + ".TRANSITIONS_RECEIVER_ACTION";
    private static PendingIntent mActivityTransitionsPendingIntent;

    /**
     * Avvia una richiesta per registrare l'intento che si occuperà di estrarre i dati sulle attività
     * @param context
     */
    public static void execute(Context context){
        setActivityTransitionList();
        Intent intentActivity = new Intent(TRANSITIONS_RECEIVER_ACTION);
        mActivityTransitionsPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext() , 123 , intentActivity , PendingIntent.FLAG_UPDATE_CURRENT);

        AtivityReceiver_v28 ativityReceiver_v28 = new AtivityReceiver_v28();
        context.getApplicationContext().registerReceiver(ativityReceiver_v28
        , new IntentFilter(TRANSITIONS_RECEIVER_ACTION)
        );

        ActivityTransitionRequest request = new ActivityTransitionRequest(activityTransitionList);
        Task<Void> task = ActivityRecognition.getClient(context.getApplicationContext())
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


    }

    /**
     * Setta il tipo di attività da estrarre, attualmente sono settate tutte le possibili attività estraibili
     */
    private static void setActivityTransitionList(){
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

}
