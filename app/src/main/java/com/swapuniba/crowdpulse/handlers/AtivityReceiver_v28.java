package com.swapuniba.crowdpulse.handlers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.swapuniba.crowdpulse.business_object.ActivityData;
import com.swapuniba.crowdpulse.utility.Utility;

import java.util.List;

public class AtivityReceiver_v28 extends BroadcastReceiver {
    private static final String TAG = "AtivityReceiver_v28";
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

                    ActivityHandler.saveActivity(activityData , context.getApplicationContext());

                }

            }

        }
    }

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

}
